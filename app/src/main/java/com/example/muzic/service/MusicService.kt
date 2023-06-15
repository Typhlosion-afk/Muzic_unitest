package com.example.muzic.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.muzic.database.model.Song
import com.example.muzic.notification.MusicNotificationManager
import com.example.muzic.utils.Constants.ACTION_CLOSE
import com.example.muzic.utils.Constants.ACTION_NEXT
import com.example.muzic.utils.Constants.ACTION_PLAY
import com.example.muzic.utils.Constants.ACTION_PREV
import com.example.muzic.utils.Constants.KEY_SONG_LIST
import com.example.muzic.utils.Constants.KEY_SONG_POSITION
import com.example.muzic.utils.Constants.LOCAL_BROADCAST_RECEIVER
import com.example.muzic.utils.Constants.MAX_SEEKBAR_VALUE
import com.example.muzic.utils.Constants.NOTIFICATION_DATA_ACTION
import com.example.muzic.utils.Constants.ONGOING_NOTIFICATION_ID
import com.example.muzic.utils.Constants.WIDGET_DATA_ACTION

class MusicService() : Service(), OnPreparedListener,
    OnCompletionListener, MediaPlayer.OnErrorListener, OnAudioFocusChangeListener {
    private val binder: IBinder = MusicBinder()
    private var mMusicNotiManager: MusicNotificationManager? = null
    private var mPlayerNotification: Notification? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mSong: Song? = null
    private var mListSong: List<Song> = ArrayList()
    var playingSongPos = 0
        private set
    private var mCurrent = 0
    var isPlaying = false
    private val mHandler = Handler()
    private var mRunnable: Runnable? = null
    private var mBroadcaster: LocalBroadcastManager? = null
    private val mSongDataIntent: Intent = Intent(LOCAL_BROADCAST_RECEIVER)
    private var isForegroundRunning = false
    private var isBinding = false
    private var mAudioManager: AudioManager? = null
    private var isInCalling = false

    val songDur: Int
        get() = if (mMediaPlayer == null) MAX_SEEKBAR_VALUE else mMediaPlayer!!.duration

    val playingSong: Song?
        get() = mSong

    val listSong: List<Song>
        get() = mListSong

    override fun onCreate() {
        super.onCreate()
        mBroadcaster = LocalBroadcastManager.getInstance(this)
        mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        mAudioManager!!.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        isInCalling = false
        initMediaPlayer()
        initNotification()
        if (mSong != null) {
            startForeground(mSong)
        }
        startSongRunnable()

        Log.d("muzicTag", "onCreate")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notificationAction = intent.getIntExtra(NOTIFICATION_DATA_ACTION, -1)
        val widgetAction = intent.getIntExtra(WIDGET_DATA_ACTION, -1)
        if (notificationAction != -1) {
            handleAction(notificationAction)
        }
        if (widgetAction != -1) {
            handleAction(widgetAction)
        }
        if (intent.getIntExtra(KEY_SONG_POSITION, -1) != -1) {
            mListSong =
                intent.getSerializableExtra(KEY_SONG_LIST, ArrayList<Song>().javaClass) as List<Song>
            playingSongPos = intent.getIntExtra(KEY_SONG_POSITION, -1)
            mSong = mListSong[playingSongPos]
            playSong()
        }
        Log.d("muzicTag from service", "onStartCommand list song size: ${mListSong.size}")

        return START_NOT_STICKY
    }

    private fun sendSongData() {
        mSongDataIntent.putExtra("song", mSong)
        mSongDataIntent.putExtra("dur", mMediaPlayer!!.duration)
        mSongDataIntent.putExtra("playing", isPlaying)
        mSongDataIntent.putExtra("cur", mMediaPlayer!!.currentPosition)
        mSongDataIntent.putExtra("session", mMediaPlayer!!.audioSessionId)
        mBroadcaster!!.sendBroadcast(mSongDataIntent)
    }

    private fun initNotification() {
        mMusicNotiManager = MusicNotificationManager(this)
        mPlayerNotification = mMusicNotiManager!!.playerNotification
    }

    private fun handleAction(action: Int) {
        when (action) {
            ACTION_PLAY -> {
                if (!isPlaying) {
                    resumeSong()
                } else {
                    pauseSong()
                }
            }

            ACTION_CLOSE -> {
                stopSong()
            }

            ACTION_NEXT -> {
                nextSong()
            }

            ACTION_PREV -> {
                prevSong()
            }

            else -> {
                Log.d("MuzicTag", "onStartCommand: unknown")
            }
        }
    }

    private fun initMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer()
        }
        mMediaPlayer!!.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mMediaPlayer!!.setOnPreparedListener(this)
        mMediaPlayer!!.setOnCompletionListener(this)
        mMediaPlayer!!.setOnErrorListener(this)
    }

    fun playSong() {
        Log.d("muzicTag from service", "play song")
        if (mMediaPlayer != null) {
            isPlaying = false
            mMediaPlayer!!.release()
            mMediaPlayer = MediaPlayer.create(this, mSong!!.getUri())
            initMediaPlayer()
            isPlaying = true
            updateNotification()
            sendSongData()
        }
    }

    fun pauseSong() {
        Log.d("muzicTag from service", "pause song")

        if (mMediaPlayer != null) {
            mMediaPlayer!!.pause()
            mCurrent = mMediaPlayer!!.currentPosition
            isPlaying = false
            updateNotification()
            sendSongData()
        }
    }

    fun resumeSong() {
        Log.d("muzicTag from service", "reusme song")

        if (mMediaPlayer != null) {
            mMediaPlayer!!.seekTo(mCurrent)
            mMediaPlayer!!.start()
            isPlaying = true
            updateNotification()
            sendSongData()
        }
    }

    fun nextSong() {
        Log.d("muzicTag from service", "next song")

        if (playingSongPos < mListSong.size - 1) {
            playingSongPos++
        } else {
            playingSongPos = 0
        }
        mSong = mListSong[playingSongPos]
        playSong()
    }

    fun prevSong() {
        Log.d("muzicTag from service", "prev song")

        if (playingSongPos > 0) {
            playingSongPos--
        } else {
            playingSongPos = mListSong.size - 1
        }
        mSong = mListSong[playingSongPos]
        playSong()
    }

    fun stopSong() {
        Log.d("muzicTag from service", "stop song")

        if (mMediaPlayer != null) {
            isPlaying = false
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mCurrent = 0
        }
        with(mMusicNotiManager!!) {
            setIsForeRunning(false)
            updateViewNotification(mSong)
            removeNotification()
        }
        stopMuzicForeground()
        if (!isBinding) {
            stopSelf()
            Log.d("muzicTag from service", "stop service")
        } else {
            mMediaPlayer = MediaPlayer.create(this, mSong!!.getUri())
            sendSongData()
        }
    }

    private fun updateNotification() {
        with(mMusicNotiManager!!) {
            setIsMediaPlaying(mMediaPlayer!!.isPlaying)
            setIsMediaPlaying(isPlaying)
            updateViewNotification(mSong)
        }
    }

    fun seekTo(mSes: Int) {
        mMediaPlayer!!.seekTo(mSes)
        mCurrent = mSes
    }

    fun startForeground(song: Song?) {
        mSong = song
        startForeground(ONGOING_NOTIFICATION_ID, mPlayerNotification)
        isForegroundRunning = true
    }

    private fun stopMuzicForeground() {
        isForegroundRunning = false
        stopForeground(true)
    }

    private fun startSongRunnable() {
        if (mRunnable != null) {
            mRunnable = null
        }
        mRunnable = Runnable {
            if (mMediaPlayer != null && isPlaying) {
                mSongDataIntent.putExtra("cur", mMediaPlayer!!.currentPosition)
                mBroadcaster!!.sendBroadcast(mSongDataIntent)
            }
            mHandler.postDelayed((mRunnable)!!, 100)
        }
        mRunnable!!.run()
    }

    private fun stopSongRunnable() {
        mHandler.removeCallbacks(mRunnable!!)
    }

    override fun onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
        mAudioManager!!.abandonAudioFocus(this)
        stopSongRunnable()
        Log.d("muzicTag from service", "destroy service")
        super.onDestroy()
    }

    override fun onCompletion(mp: MediaPlayer) {
        nextSong()
    }

    override fun onPrepared(mp: MediaPlayer) {
        mp.start()
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        return false
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0) {
            pauseSong()
        } else {
            resumeSong()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        isBinding = true
        return binder
    }

    override fun onRebind(intent: Intent) {
        Log.d("TAG", "reBind: ")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d("TAG", "unBind: ")
        isBinding = false
        return super.onUnbind(intent)
    }

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }
}