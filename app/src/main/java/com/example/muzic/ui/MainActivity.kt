package com.example.muzic.ui

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.muzic.R
import com.example.muzic.database.model.Song
import com.example.muzic.service.MusicService
import com.example.muzic.utils.Constants.LOCAL_BROADCAST_RECEIVER
import com.example.muzic.utils.Constants.MAX_SEEKBAR_VALUE


class MainActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var mainNavController: NavController
    private lateinit var mMusicService: MusicService
    private var mReceiver: BroadcastReceiver? = null

    private val viewModel: MainViewModel by viewModels()

    private var mBound = false
    private var mSong: Song? = null
    private var mIsPlaying = false
    private var mSongDur: Int = MAX_SEEKBAR_VALUE

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder: MusicService.MusicBinder = service as MusicService.MusicBinder
            Log.d("muzicTag from activity", "service connected")

            mMusicService = binder.service
            mBound = true
            mSong = mMusicService.playingSong
            if (mSong != null) {
                mIsPlaying = mMusicService.isPlaying
                mSongDur = mMusicService.songDur
                mSong = mMusicService.playingSong
                showController()
            } else {
                hideController()
            }
            viewModel.setMusicService(mMusicService)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mBound = false
            Log.d("muzicTag from activity", "service disconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBindService()
        initBroadcast()
        initViewModel()
        initSongs()
    }

    private fun initViewModel() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.list_container) as NavHostFragment
        mainNavController = navHostFragment.navController
        viewModel.createNavController(navController = mainNavController)
        viewModel.createProvider(this)
    }

    private fun initSongs() {
        viewModel.providerSong.observe(this) {
            if (it != null) {
                viewModel.getListSong()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("muzicTag from activity", "register broadcast")

        mReceiver?.let {
            LocalBroadcastManager.getInstance(this)
                .registerReceiver(it, IntentFilter(LOCAL_BROADCAST_RECEIVER))
        }
    }

    override fun onStop() {
        Log.d("muzicTag from activity", "unregister broadcast")

        mReceiver?.let {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(it)
        }
        super.onStop()
    }

    private fun initBroadcast() {
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d("muzicTag from activity", "Update Controller")
            }
        }
    }

    private fun initBindService() {
        Log.d("muzicTag from activity", "bind service")

        val iStartService = Intent(this, MusicService::class.java)
        bindService(iStartService, connection, BIND_AUTO_CREATE)
    }

    private fun showController(){
        Log.d("muzicTag from activity", "ShowController")
    }

    private fun hideController(){
        Log.d("muzicTag from activity", "HideController")
    }

    override fun onDestroy() {
        Log.d("muzicTag from activity", "destroy")

        unbindService(connection)
        super.onDestroy()
    }
}