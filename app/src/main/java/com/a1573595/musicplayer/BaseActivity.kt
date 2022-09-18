package com.a1573595.musicplayer

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle

abstract class BaseActivity<P : BasePresenter<*>> : AppCompatActivity(), BaseView {
    protected lateinit var presenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = createPresenter()
    }

    override fun isActive(): Boolean = lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

    override fun context(): Context = this

    protected abstract fun createPresenter(): P
}