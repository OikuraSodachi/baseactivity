package com.a1573595.musicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.a1573595.musicplayer.player.PlayerService
import java.util.*

abstract class BaseSongActivity<P : BasePresenter<*>> : BaseActivity<P>(), Observer {
    private val REQUEST_WRITE_EXTERNAL_STORAGE: Int = 10

    private lateinit var player: PlayerService

    private var isBound: Boolean = false

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder) {
            val localBinder = binder as PlayerService.LocalBinder

            localBinder.service?.let {
                player = it

                player.addPlayerObserver(this@BaseSongActivity)
                isBound = true
                playerBound(player)
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, PlayerService::class.java)
        startService(intent)


        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)

    }

    override fun onRestart() {
        super.onRestart()

        if (isBound) {
            player.addPlayerObserver(this)
            updateState()
        }
    }

    override fun onStop() {
        super.onStop()

        if (isBound) {
            player.deletePlayerObserver(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isBound) {
            isBound = false
            unbindService(mConnection)
        }
    }



    abstract fun playerBound(player: PlayerService)

    abstract fun updateState()
}