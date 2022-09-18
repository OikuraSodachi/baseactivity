package com.a1573595.musicplayer.songList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.a1573595.musicplayer.databinding.AdapterSongListBinding
import com.a1573595.musicplayer.model.Music
import com.a1573595.musicplayer.model.TimeUtil

class SongListAdapter(private val presenter: SongListPresenter) :
    RecyclerView.Adapter<SongListAdapter.SongHolder>() {
    inner class SongHolder(val viewBinding: AdapterSongListBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        init {
            itemView.setOnClickListener {
                presenter.onSongClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val viewBinding =
            AdapterSongListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongHolder(viewBinding)
    }

    override fun getItemCount(): Int = presenter.getItemCount()

    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        val music: Music = presenter.getItem(position)

        holder.viewBinding.tvName.text = music.title
        holder.viewBinding.tvArtist.text = music.artist
        holder.viewBinding.tvDuration.text = TimeUtil.timeMillisToTime(music.duration)
    }
}