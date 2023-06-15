package com.example.muzic.database.model

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import com.example.muzic.utils.Constants.ALBUM_ART_URI

class ProviderSong(private val context: Context) {
    private var cursor: Cursor? = null
    private var mAllSongs: MutableList<Song> = mutableListOf()

    var idColumn = 0
    var nameColumn = 0
    var artistColumn = 0
    var dataColumn = 0
    var albumColumn = 0
    var durColumn = 0

    init {
        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
        )

        cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        if (cursor != null && cursor!!.moveToFirst()) {
            idColumn = cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            albumColumn = cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            nameColumn = cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE)
            artistColumn = cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            dataColumn = cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA)
            durColumn = cursor!!.getColumnIndex(MediaStore.Audio.Media.DURATION)
        }
    }

    fun getAllSong() : MutableList<Song>{
        mAllSongs.clear()
        if (cursor != null && cursor!!.moveToFirst()) {
            do {
                val uriSong = ContentUris
                    .withAppendedId(ALBUM_ART_URI, cursor!!.getString(idColumn).toLong())
                    .toString()
                val song = Song(
                    uriSong,
                    cursor!!.getString(nameColumn),
                    cursor!!.getString(artistColumn),
                    cursor!!.getString(dataColumn),
                    cursor!!.getString(albumColumn),
                    cursor!!.getString(durColumn)
                )
                if (song.getExtension() == ".mp3" && song.name != "tone") {
                    mAllSongs.add(song)
                }
            } while (cursor!!.moveToNext())
        }
        Log.d("MuzicTag", "Provider Song size: ${mAllSongs.size}")

        return mAllSongs
    }
}