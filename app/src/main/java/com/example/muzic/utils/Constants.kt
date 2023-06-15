package com.example.muzic.utils

import android.net.Uri

object Constants {
    const val LOCAL_BROADCAST_RECEIVER = "com.dore.myapplication.service.RECEIVER"

    const val GLOBAL_BROADCAST_RECEIVER = "android.appwidget.action.APPWIDGET_UPDATE"

    const val NOTIFICATION_CHANNEL_ID = "muzic_app_channel_id"

    const val ONGOING_NOTIFICATION_ID = 99

    const val NOTIFICATION_DATA_ACTION = "noti_event_action"

    const val WIDGET_DATA_ACTION = "widget_event_action"

    const val MAX_SEEKBAR_VALUE = 1000

    const val KEY_SONG_POSITION = "key_for_song_pos"

    const val KEY_SONG_LIST = "key_for_list_song"

    const val ACTION_PLAY = 0

    const val ACTION_PAUSE = 1

    const val ACTION_CLOSE = 2

    const val ACTION_NEXT = 3

    const val ACTION_PREV = 4

    const val ACTION_OPEN_APP = 5

    val ALBUM_ART_URI: Uri = Uri.parse("content://media/external/audio/albumart")
}