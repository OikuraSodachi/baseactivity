package com.a1573595.musicplayer.songList

import com.a1573595.musicplayer.BaseView
import com.a1573595.musicplayer.model.Music

interface SongListView : BaseView {
    fun showLoading()

    fun stopLoading()

    fun updateSongState(music: Music, isPlaying: Boolean)

    fun onSongClick()
}