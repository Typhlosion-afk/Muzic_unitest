package com.example.muzic.ui.listsong

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.muzic.R
import com.example.muzic.database.model.Song
import com.example.muzic.databinding.FragmentListSongBinding
import com.example.muzic.service.MusicService
import com.example.muzic.ui.MainViewModel
import com.example.muzic.utils.Constants.KEY_SONG_LIST
import com.example.muzic.utils.Constants.KEY_SONG_POSITION
import java.io.Serializable

class ListSongFragment : Fragment() {

    private var _binding: FragmentListSongBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListSongAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListSongBinding.inflate(LayoutInflater.from(activity), container, false)

        initAdapter()
        subscribeUi()

        return binding.root
    }

    private fun initAdapter() {
        recyclerView = binding.recyclerViewList
        adapter = ListSongAdapter(
            onItemClickListener = ListSongAdapter.OnItemClick { list, pos ->
                playSong(list, pos)
            },
            context = requireContext()
        )
        LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false).apply {
            recyclerView.layoutManager = this
        }
        recyclerView.adapter = adapter
    }

    private fun playSong(list: List<Song>, pos: Int){
        mainViewModel.navController.value?.navigate(R.id.action_listSongFragment_to_playingFragment)

        val i = Intent(requireContext(), MusicService::class.java)
        i.putExtra(KEY_SONG_LIST, list as Serializable)
        i.putExtra(KEY_SONG_POSITION, pos)

        requireActivity().startService(i)

        Log.d("MuzicTag from ListFragment", "Play song: ${pos}/${list.size - 1}")

    }

    private fun subscribeUi() {
        mainViewModel.listSong.observe(requireActivity()) {
            if(it.size > 0) {
                adapter.submitListSong(it)
                Log.d("MuzicTag from ListFragment", "listSong size: ${it.size}")
            }else {
                Log.d("MuzicTag from ListFragment", "Show empty screen")
            }
        }
    }
}