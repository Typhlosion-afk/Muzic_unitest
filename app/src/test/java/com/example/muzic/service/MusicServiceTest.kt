package com.example.muzic.service

import android.app.Service
import android.app.Service.START_NOT_STICKY
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import com.example.muzic.database.model.Song
import com.example.muzic.utils.Constants.KEY_SONG_LIST
import com.example.muzic.utils.Constants.KEY_SONG_POSITION
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.util.ReflectionHelpers
import java.io.Serializable
import org.mockito.Mockito.`when` as _when

@RunWith(RobolectricTestRunner::class)
class MusicServiceTest {
    private lateinit var mMusicService: TestMusicService
    private lateinit var mContext: Context

    @Mock
    private lateinit var mAudioManager: AudioManager

    private inner class TestMusicService(val context: Context) : MusicService() {

        override fun onCreate() {
            try {
                attachBaseContext(context)
            } catch (e: Exception) {
                Log.d("TAG", "onCreate: $e")
            }
            super.onCreate()
        }
    }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        mContext = spy(RuntimeEnvironment.getApplication())
        _when(mContext.getSystemService(eq(Service.AUDIO_SERVICE))).thenReturn(mAudioManager)

        mMusicService = TestMusicService(mContext)
        mMusicService.onCreate()

        verify(mAudioManager, times(1)).requestAudioFocus(
            any(),
            eq(AudioManager.STREAM_MUSIC),
            eq(AudioManager.AUDIOFOCUS_GAIN)
        )
    }

    @Test
    fun testOnBind() {
        assertNotNull(mMusicService.onBind(Intent()))
        val isBinding: Boolean = ReflectionHelpers.getField(mMusicService, "isBinding")
        assertTrue(isBinding)
    }

    @Test
    fun testOnUnbind() {
        mMusicService.onUnbind(Intent())
        val isBinding: Boolean = ReflectionHelpers.getField(mMusicService, "isBinding")
        assertFalse(isBinding)
    }

    @Test
    fun testOnStartCommand() {
        val song = Song("imgPath", "name", "author", "path", "album", "1111");
        val listSong = listOf(song)
        val intent = Intent()
        intent.putExtra(KEY_SONG_POSITION, 0)
        intent.putExtra(KEY_SONG_LIST, listSong as Serializable)

        val result = mMusicService.onStartCommand(intent, 1, 1)
        val mSong: Song = ReflectionHelpers.getField(mMusicService, "mSong")

        assertEquals(mSong, song)
        assertEquals(result, START_NOT_STICKY)
    }

    @Test
    fun testNextSong() {
        val song1 = Song("imgPath1", "name1", "author", "path", "album", "1111");
        val song2 = Song("imgPath2", "name2", "author", "path", "album", "1111");
        val listSong = listOf(song1, song2)
        val intent = Intent()
        intent.putExtra(KEY_SONG_POSITION, 0)
        intent.putExtra(KEY_SONG_LIST, listSong as Serializable)

        //set value mSong and ListSong
        mMusicService.onStartCommand(intent, 1, 1)

        mMusicService.nextSong()
        val mSong: Song = ReflectionHelpers.getField(mMusicService, "mSong")
        assertEquals(mSong, song2)
    }

    @Test
    fun testPrevSong() {
        val song1 = Song("imgPath1", "name1", "author", "path", "album", "1111");
        val song2 = Song("imgPath2", "name2", "author", "path", "album", "1111");
        val listSong = listOf(song1, song2)
        val intent = Intent()
        intent.putExtra(KEY_SONG_POSITION, 1)
        intent.putExtra(KEY_SONG_LIST, listSong as Serializable)

        //set value mSong and ListSong
        mMusicService.onStartCommand(intent, 1, 1)

        mMusicService.prevSong()
        val mSong: Song = ReflectionHelpers.getField(mMusicService, "mSong")
        assertEquals(mSong, song1)
    }
}