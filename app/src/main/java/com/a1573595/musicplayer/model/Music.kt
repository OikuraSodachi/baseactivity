package com.a1573595.musicplayer.model

import android.net.Uri
import android.provider.MediaStore

data class Music(
    val id: String,
    val title: String?,
    val artist: String?,
    val albumId: String?,
    val duration: Long
){
    fun getMusicUri(): Uri {
        return Uri.withAppendedPath(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id     // 음원의 주소
        )
    }          //-----------음원의 Uri 주소 호출하는 함수

    fun getAlbumUri(): Uri {

        return Uri.parse(
            "content://media/external/audio/albumart/$albumId"    //앨범 이미지 주소
        )
    }
}