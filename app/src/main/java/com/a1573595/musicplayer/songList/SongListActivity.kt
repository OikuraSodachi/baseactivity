package com.a1573595.musicplayer.songList

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.a1573595.musicplayer.BaseSongActivity
import com.a1573595.musicplayer.databinding.ActivitySongListBinding
import com.a1573595.musicplayer.model.Music
import com.a1573595.musicplayer.playSong.PlaySongActivity
import com.a1573595.musicplayer.player.PlayerManager
import com.a1573595.musicplayer.player.PlayerService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.util.*

class SongListActivity : BaseSongActivity<SongListPresenter>(), SongListView {
    private lateinit var viewBinding: ActivitySongListBinding

    private var loadingDialog: AlertDialog? = null
    lateinit var activityResult: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySongListBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initRecyclerView()

        viewBinding.title.isSelected = true

        activityResult =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startProcess()
                } else {
                    finish()
                }
            }
        activityResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun onDestroy() {
        loadingDialog?.dismiss()
        loadingDialog = null

        super.onDestroy()
    }

    override fun playerBound(player: PlayerService) {
        presenter.setPlayerManager(player)

        setListen()
    }

    override fun updateState() {
        presenter.fetchSongState()
    }

    override fun createPresenter(): SongListPresenter = SongListPresenter(this)

    override fun showLoading() {
        lifecycleScope.launch {


            loadingDialog = MaterialAlertDialogBuilder(context()).create().apply {
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                setCancelable(false)

                show()
            }

        }
    }

    override fun stopLoading() {
        lifecycleScope.launch {
            loadingDialog?.dismiss()
            loadingDialog = null

            viewBinding.recyclerView.scheduleLayoutAnimation()
        }
    }

    override fun updateSongState(music: Music, isPlaying: Boolean) {
        lifecycleScope.launch {
            viewBinding.title.text = music.title
            viewBinding.tvArtist.text = music.artist

        }
    }

    override fun update(o: Observable?, any: Any?) {
        when (any) {
            PlayerManager.ACTION_PLAY, PlayerManager.ACTION_PAUSE -> {
                presenter.fetchSongState()
            }
            PlayerService.ACTION_FIND_NEW_SONG, PlayerService.ACTION_NOT_SONG_FOUND -> {
            }
        }
    }

    override fun onSongClick() {
        viewBinding.bottomAppBar.performShow()
    }




    private fun initRecyclerView() {
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = SongListAdapter(presenter)
        viewBinding.recyclerView.adapter = adapter
        presenter.setAdapter(adapter)

    }

    private fun setListen() {


        viewBinding.bottomAppBar.setOnClickListener {
            if (viewBinding.title.text.isNotEmpty() || viewBinding.tvArtist.text.isNotEmpty()) {
                val p1: Pair<View, String> =
                    Pair.create(viewBinding.imgDisc, viewBinding.imgDisc.transitionName)
                val p2: Pair<View, String> =
                    Pair.create(viewBinding.title, viewBinding.title.transitionName)

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2)

                startActivity(Intent(this, PlaySongActivity::class.java), options.toBundle())
            }
        }
    }
}

fun startProcess() {}


// model.Music.kt 에서 duration을 Long->Long?으로 변경할것. 변경 안하면 오류 위험성 있을지도