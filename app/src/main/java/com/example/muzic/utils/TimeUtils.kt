package com.example.muzic.utils

import com.example.muzic.database.model.Song

object TimeUtils {
    fun posToTime(pos: Int): String {
        val m = pos / 1000 / 60
        val s = pos / 1000 % 60
        val min = if (m < 10) "0$m" else "" + m
        val sec = if (s < 10) "0$s" else "" + s
        return "$min:$sec"
    }

    fun getSongTime(song: Song): String {
        val pos: Int = song.dur!!.toInt()
        val m = pos / 1000 / 60
        val s = pos / 1000 % 60
        val min = if (m < 10) "0$m" else "" + m
        val sec = if (s < 10) "0$s" else "" + s
        return "$min:$sec"
    }
}