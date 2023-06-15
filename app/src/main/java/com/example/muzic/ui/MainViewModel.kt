package com.example.muzic.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.muzic.database.model.ProviderSong
import com.example.muzic.database.model.Song
import com.example.muzic.service.MusicService

class MainViewModel : ViewModel() {
    private val _providerSong = MutableLiveData<ProviderSong>()
    private val _navController = MutableLiveData<NavController>()
    private val _listSong = MutableLiveData(mutableListOf<Song>())
    private val _musicService = MutableLiveData<MusicService>()

    val providerSong get() = _providerSong
    val navController get() = _navController
    val listSong get() = _listSong
    val musicService get() = _musicService

    fun setMusicService(service: MusicService) {
        musicService.value = service
    }

    fun createProvider(context: Context) {
        _providerSong.value = ProviderSong(context)
    }

    fun createNavController(navController: NavController) {
        _navController.value = navController
    }

    fun getListSong() {
        _listSong.value!!.clear();
        _listSong.value!!.addAll(providerSong.value!!.getAllSong())
        Log.d("MuzicTag", "Song size: ${_listSong.value!!.size}")
    }
}