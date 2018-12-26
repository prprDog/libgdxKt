package com.mygdx.game

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.Texture

fun createTexture(path: String, type: Files.FileType = Files.FileType.Internal): Texture{
    return Texture(createFileHandle(path, type))
}

fun createSound(path: String, type: Files.FileType = Files.FileType.Internal): Sound {
    return Gdx.audio.newSound(createFileHandle(path, type))
}

fun createMusic(path: String, type: Files.FileType = Files.FileType.Internal): Music{
    return Gdx.audio.newMusic(createFileHandle(path, type))
}

fun createFileHandle(path: String, type: Files.FileType = Files.FileType.Internal): FileHandle{
    return when (type){
        Files.FileType.Absolute ->{
            Gdx.files.absolute(path)
        }
        Files.FileType.Classpath ->{
            Gdx.files.classpath(path)
        }
        Files.FileType.Local ->{
            Gdx.files.local(path)
        }
        else -> {
            Gdx.files.internal(path)
        }
    }
}

//自动关闭每次事务
fun SpriteBatch.startBatch(block: SpriteBatch.() -> Unit){
    begin()
    block()
    end()
}
