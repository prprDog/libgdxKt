package com.mygdx.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.utils.Disposable

abstract class BaseGame: ApplicationAdapter() {
    private lateinit var disposeArray: MutableList<Disposable>
    override fun create() {
        super.create()
        disposeArray = disposeArray()
    }

    override fun dispose() {
        super.dispose()
        for (disposable in disposeArray) {
            disposable.dispose()
        }
    }

    /**
     * 用来退出时自动取消资源
     */
    abstract fun disposeArray(): MutableList<Disposable>
}