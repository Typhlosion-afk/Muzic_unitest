package com.example.muzic.ui.playsong

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.muzic.database.model.Song
import com.example.muzic.databinding.FragmentPlayingBinding
import com.example.muzic.service.MusicService
import com.example.muzic.ui.MainViewModel
import com.example.muzic.utils.Constants.LOCAL_BROADCAST_RECEIVER
import com.example.muzic.utils.Constants.MAX_SEEKBAR_VALUE
import com.example.muzic.utils.TimeUtils

class PlayingFragment : Fragment() {
    private var mService: MusicService? = null
    private var receiver: BroadcastReceiver? = null
    private var _binding: FragmentPlayingBinding? = null
    private val binding get() = _binding!!
    private var mSong: Song? = null
    private var mSongCur = 0
    private var mSongDur = MAX_SEEKBAR_VALUE
    private var mIsPlaying = false
    private var isSeekBarTouching = false
    private var mAudioSession = -1
    private var mListSong: List<Song> = ArrayList()
    private var mSongPos = 0
    private var mSeekBarProcessFromUser = 0

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayingBinding.inflate(LayoutInflater.from(activity), container, false)

        initBroadcast()
        initDataService()
        initAction()

        return binding.root
    }

    private fun initAction() {
        binding.btnPlay.setOnClickListener {
            if (mIsPlaying) {
                mService?.pauseSong()
            } else {
                mService?.resumeSong()
            }
        }

        binding.btnNext.setOnClickListener {
            mService?.nextSong()

        }

        binding.btnPrev.setOnClickListener {
            mService?.prevSong()
        }

        binding.seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    binding.tvDuration.text = TimeUtils.posToTime(progress)
                    mSeekBarProcessFromUser = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeekBarTouching = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isSeekBarTouching = false
                mService?.seekTo(mSeekBarProcessFromUser)
            }

        })
    }

    private fun initDataService() {
        mService = mainViewModel.musicService.value
        mService?.let {
            mSong = it.playingSong
            mListSong = it.listSong
            mIsPlaying = it.isPlaying
            mSongPos = it.playingSongPos
            mSongDur = it.songDur
        }
    }

    private fun initBroadcast() {
        receiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onReceive(context: Context, intent: Intent) {
                update(
                    intent.getSerializableExtra("song", Song::class.java),
                    intent.getIntExtra("dur", MAX_SEEKBAR_VALUE),
                    intent.getIntExtra("cur", 0),
                    intent.getBooleanExtra("playing", false),
                    intent.getIntExtra("session", -1)
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        receiver?.let {
            LocalBroadcastManager
                .getInstance(requireContext())
                .registerReceiver(it, IntentFilter(LOCAL_BROADCAST_RECEIVER))
        }
    }

    override fun onStop() {
        receiver?.let {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(it)
        }
        super.onStop()
    }

    @SuppressLint("SetTextI18n")
    private fun update(song: Song?, dur: Int, cur: Int, playing: Boolean, audioSession: Int) {
        if (song !== mSong) {
            mSong = song
            mSong?.let {
                binding.tvSongName.text = it.name
                mSongDur = dur

                binding.seekBar.max = mSongDur
                binding.tvMaxDuration.text = TimeUtils.posToTime(mSongDur)
            }
        }
        if (mIsPlaying != playing) {
            mIsPlaying = playing
            if (mIsPlaying) {
                binding.btnPlay.text = "Pause"
            } else {
                binding.btnPlay.text = "Play"
            }
        }
        if (!isSeekBarTouching) {
            mSongCur = cur
            updateUiWithCur()
        }
    }

    private fun updateUiWithCur() {
        if (mSongDur != 0) {
            binding.seekBar.progress = mSongCur
            binding.tvDuration.text = TimeUtils.posToTime(mSongCur)
        }
    }
}