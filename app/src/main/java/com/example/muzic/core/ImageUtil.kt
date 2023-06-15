package com.example.muzic.core

import android.content.Context
import android.content.pm.PackageManager
import android.widget.ImageView
import com.example.muzic.database.model.Song

object ImageUtil {
    fun showImageWithPackage(
        context: Context,
        packageName: String,
        packageManager: PackageManager,
        viewTarget: ImageView
    ) {
//        Glide
//            .with(context)
//            .load(packageManager.getApplicationIcon(packageName))
//            .placeholder(R.drawable.ic_home)
//            .centerCrop()
//            .into(viewTarget)
    }

    fun showIconSong(
        context: Context,
        song: Song,
        viewTarget: ImageView
    ) {
//        Glide
//            .with(context)
//            .load(appInfo.icon)
//            .placeholder(R.drawable.ic_home)
//            .centerCrop()
//            .into(viewTarget)
    }
}