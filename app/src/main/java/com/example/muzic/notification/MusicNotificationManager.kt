package com.example.muzic.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.muzic.R
import com.example.muzic.database.model.Song
import com.example.muzic.service.MusicService
import com.example.muzic.ui.MainActivity
import com.example.muzic.utils.Constants.ACTION_OPEN_APP
import com.example.muzic.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.example.muzic.utils.Constants.NOTIFICATION_DATA_ACTION

class MusicNotificationManager(private val mContext: Context) {
    var mNotificationManagerCompat: NotificationManagerCompat =
        NotificationManagerCompat.from(mContext)
    private var mLargeNotificationLayout: RemoteViews? = null
    private var mSmallNotificationLayout: RemoteViews? = null
    private var builder: NotificationCompat.Builder? = null
    val playerNotification: Notification
    private var isMediaPlaying = true
    private var mIsRunning = true

    init {
        playerNotification = createPlayerNotification()
        playerNotification.sound = null
    }

    fun setIsMediaPlaying(isPlaying: Boolean) {
        isMediaPlaying = isPlaying
    }

    @SuppressLint("MissingPermission")
    fun updateViewNotification(song: Song?) {
        if (song != null) {
            mLargeNotificationLayout!!.setTextViewText(R.id.txt_noti_song_name, song.name)
            mLargeNotificationLayout!!.setTextViewText(R.id.txt_noti_author, song.author)

            mSmallNotificationLayout!!.setTextViewText(R.id.txt_noti_song_name, song.name)
            mSmallNotificationLayout!!.setTextViewText(R.id.txt_noti_author, song.author)
            mNotificationManagerCompat.notify(1, builder!!.build())

        }
    }

    private fun createPlayerNotification(): Notification {
        mLargeNotificationLayout = RemoteViews(
            mContext.packageName,
            R.layout.notification_music_large
        )
        mSmallNotificationLayout = RemoteViews(
            mContext.packageName,
            R.layout.notification_music_small
        )

        builder = NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_noti_small)
            .setCustomContentView(mSmallNotificationLayout)
            .setCustomBigContentView(mLargeNotificationLayout)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(mIsRunning)
            .setOnlyAlertOnce(true)
        return builder!!.build()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getNotiPendingIntent(action: Int): PendingIntent {
        Log.d("TAG", "getNotiPendingIntent: $action")
        val i = Intent(mContext, MusicService::class.java)
        i.putExtra(NOTIFICATION_DATA_ACTION, action)
        return PendingIntent.getService(mContext, action, i, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @get:SuppressLint("UnspecifiedImmutableFlag")
    private val openAppIntent: PendingIntent
        get() {
            val taskStackBuilder = TaskStackBuilder.create(mContext)
            taskStackBuilder.addParentStack(MainActivity::class.java)
            taskStackBuilder.addNextIntent(Intent())
            return taskStackBuilder.getPendingIntent(
                ACTION_OPEN_APP,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

    fun setIsForeRunning(isForeRunning: Boolean) {
        mIsRunning = isForeRunning
    }

    fun removeNotification() {
        mNotificationManagerCompat.cancelAll()
    }
}