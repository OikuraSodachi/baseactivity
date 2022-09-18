package com.a1573595.musicplayer.playSong

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.a1573595.musicplayer.BaseSongActivity
import com.a1573595.musicplayer.R
import com.a1573595.musicplayer.databinding.ActivityPlaySongBinding
import com.a1573595.musicplayer.model.Music
import com.a1573595.musicplayer.model.TimeUtil
import com.a1573595.musicplayer.player.PlayerManager
import com.a1573595.musicplayer.player.PlayerService
import java.util.*

class PlaySongActivity : BaseSongActivity<PlaySongPresenter>(), PlaySongView {
    companion object {
        private val STATE_PLAY = intArrayOf(R.attr.state_pause)
        private val STATE_PAUSE = intArrayOf(-R.attr.state_pause)
    }

    private lateinit var viewBinding: ActivityPlaySongBinding

    private lateinit var seekBarUpdateRunnable: Runnable
    private val seekBarUpdateDelayMillis: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()

        viewBinding = ActivityPlaySongBinding.inflate(layoutInflater)
        setScreenHigh()
        setContentView(viewBinding.root)

        viewBinding.title.isSelected = true
    }

    override fun onStop() {
        super.onStop()
        viewBinding.seekBar.removeCallbacks(seekBarUpdateRunnable)
    }

    override fun playerBound(player: PlayerService) {
        initSeekBarUpdateRunnable()

        presenter.setPlayerManager(player)

        setListen()
    }

    override fun updateState() {
        presenter.fetchSongState()
    }

    override fun createPresenter(): PlaySongPresenter = PlaySongPresenter(this)

    override fun updateSongState(music: Music, isPlaying: Boolean, progress: Int) {
        viewBinding.seekBar.removeCallbacks(seekBarUpdateRunnable)

        viewBinding.title.text = music.title
        viewBinding.songTotalTime.text = TimeUtil.timeMillisToTime(music.duration)
        viewBinding.seekBar.max = (music.duration / 1000).toInt()
        viewBinding.seekBar.progress = progress
        viewBinding.songCurrentProgress.text =
            TimeUtil.timeMillisToTime((viewBinding.seekBar.progress * 1000).toLong())
        viewBinding.imgPlay.setImageState(if (isPlaying) STATE_PLAY else STATE_PAUSE, false)

        if (isPlaying) {
            viewBinding.seekBar.postDelayed(seekBarUpdateRunnable, seekBarUpdateDelayMillis)
        } else {
        }
    }

    override fun showRepeat(isRepeat: Boolean) {
        viewBinding.repeatButton.imageAlpha = if (isRepeat) 255 else 80
    }

    override fun showRandom(isRandom: Boolean) {
        viewBinding.shuffleButton.imageAlpha = if (isRandom) 255 else 80
    }

    override fun update(o: Observable?, any: Any?) {
        when (any) {
            PlayerManager.ACTION_PLAY, PlayerManager.ACTION_PAUSE -> {
                updateState()
            }
        }
    }

    private fun hideStatusBar() {
//        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_LOW_PROFILE)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            // Hide the status bar
            hide(WindowInsetsCompat.Type.statusBars())
            // Allow showing the status bar with swiping from top to bottom
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }


    private fun setScreenHigh() {
        ViewCompat.setOnApplyWindowInsetsListener(
            viewBinding.root
        ) { view: View, windowInsets: WindowInsetsCompat ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams).apply {
                // draw on top of the bottom navigation bar
                bottomMargin = insets.bottom
            }

            // Return CONSUMED if you don't want the window insets to keep being
            // passed down to descendant views.
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun initSeekBarUpdateRunnable() {
        seekBarUpdateRunnable = Runnable {
            viewBinding.seekBar.progress = viewBinding.seekBar.progress + 1
            viewBinding.seekBar.postDelayed(seekBarUpdateRunnable, seekBarUpdateDelayMillis)
        }
    }


    private fun setListen() {

        viewBinding.repeatButton.setOnClickListener {
            viewBinding.repeatButton.imageAlpha = if (presenter.updateRepeat()) 255 else 80
        }

        viewBinding.shuffleButton.setOnClickListener {
            viewBinding.shuffleButton.imageAlpha = if (presenter.updateRandom()) 255 else 80
        }

        viewBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewBinding.seekBar.removeCallbacks(seekBarUpdateRunnable)
                }

                viewBinding.songCurrentProgress.text =
                    TimeUtil.timeMillisToTime((viewBinding.seekBar.progress * 1000).toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                viewBinding.seekBar.removeCallbacks(seekBarUpdateRunnable)

                presenter.seekTo(seekBar.progress)
                viewBinding.songCurrentProgress.text =
                    TimeUtil.timeMillisToTime((viewBinding.seekBar.progress * 1000).toLong())

                viewBinding.seekBar.postDelayed(seekBarUpdateRunnable, seekBarUpdateDelayMillis)
            }
        })

        viewBinding.previousButton.setOnClickListener {
            presenter.skipToPrevious()                  //  이전곡 재생함수 실행
        }

        viewBinding.imgPlay.setOnClickListener {
            presenter.onSongPlay()
        }

        viewBinding.nextButton.setOnClickListener {
            presenter.skipToNext()

        }
    }
}