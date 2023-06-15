package com.example.muzic.ui.listsong

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.muzic.core.ImageUtil
import com.example.muzic.database.model.Song
import com.example.muzic.databinding.CardSongsBinding

class ListSongAdapter(
    var onItemClickListener: OnItemClick,
    private var listSong: List<Song> = emptyList(),
    private var context: Context
) : RecyclerView.Adapter<ListSongAdapter.ListSongViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListSongViewHolder {
        val itemBinding = CardSongsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ListSongViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = listSong.size

    override fun onBindViewHolder(holder: ListSongViewHolder, position: Int) {
        if (position != -1) {
            val song = listSong[position]
            holder.initView(song)
            holder.initAction(listSong, position)
        }
    }

    inner class ListSongViewHolder(private val binding: CardSongsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun initView(song: Song) {
            binding.tvName.text = song.name
        }

        fun initAction(list: List<Song>, pos: Int) {
            itemView.setOnClickListener {
                onItemClickListener.onClick(list, pos)
            }
        }
    }

    fun submitListSong(newList: List<Song>) {
        val oldList = listSong
        val diffCallback = DiffCallback(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listSong = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class OnItemClick(val listener: (listSong: List<Song>, pos: Int) -> Unit) {
        fun onClick(listSong: List<Song>, pos: Int) = listener(listSong, pos)
    }

    class DiffCallback(private val oldList: List<Song>, private val newList: List<Song>) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return newItem === oldItem
        }
    }
}