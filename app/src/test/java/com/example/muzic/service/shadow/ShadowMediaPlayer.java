package com.example.muzic.service.shadow;

import android.media.MediaPlayer;

import com.example.muzic.service.MusicService;

import org.robolectric.annotation.Implements;

@Implements(value = MediaPlayer.class)
public class ShadowMediaPlayer {

}
