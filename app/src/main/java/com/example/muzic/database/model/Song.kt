package com.example.muzic.database.model

import android.net.Uri
import java.io.Serializable

class Song(
    var imgPath: String? = null,
    var name: String? = null,
    var author: String? = null,
    var path: String? = null,
    var album: String? = null,
    var dur: String? = null
) : Serializable {
    fun getExtension(): String {
        return path!!.substring(path!!.lastIndexOf('.'))
    }

    fun getUri(): Uri? {
        return Uri.parse(path)
    }

}