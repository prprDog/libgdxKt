package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.TimeUtils
class MyGdxGame : BaseGame() {

    lateinit var dropImage: Texture
    lateinit var bucketImage: Texture
    lateinit var bulletImage: Texture
    lateinit var dropSound: Sound
    lateinit var rainMusic: Music
    lateinit var batch: SpriteBatch
    lateinit var bucket: Rectangle
    lateinit var camera: OrthographicCamera
    lateinit var touchPos: Vector3
    lateinit var shotPos: Vector3
    lateinit var raindrops: Array<Rectangle>
    lateinit var bullets: Array<Rectangle>
    var lastDropTime: Long = 0
    var lastShotTime: Long = 0

    override fun create() {
        //可封装成注解？
        dropImage = createTexture("droplet.png")
        bucketImage = createTexture("bucket.png")
        bulletImage = createTexture("bullet.png")
        dropSound = createSound("waterdrop.wav")
        rainMusic = createMusic("undertreeinrain.mp3").also {
            it.isLooping = true
            it.play()
        }
        camera = OrthographicCamera().also {
            it.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        }
        batch = SpriteBatch()
        bucket = Rectangle().also {
            it.x = (Gdx.graphics.width / 2 - 64 / 2).toFloat()
            it.y = 20.toFloat()
            it.width = 64F
            it.height = 64F
        }
        raindrops = Array()
        bullets = Array()
        spawnRadinDrop()
    }

    override fun disposeArray(): MutableList<Disposable> {
        return mutableListOf(dropImage, bucketImage, bulletImage, dropSound, rainMusic)
    }

    override fun render() {
        Gdx.gl.glClearColor(0F, 0F, 0.2F, 1F)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        camera.update()
        with(batch){
            projectionMatrix = camera.combined
           startBatch {
                draw(bucketImage, bucket.x, bucket.y)
                for (raindrop in raindrops) {
                    batch.draw(dropImage, raindrop.x, raindrop.y)
                }
                for (bullet in bullets) {
                    batch.draw(bulletImage, bullet.x, bullet.y)
                }
            }
        }

        //移动木桶
        if (Gdx.input.isTouched) {
            touchPos = Vector3().also {
                it.set(Gdx.input.getX().toFloat(), Gdx.input.getY().toFloat(), 0F)
                camera.unproject(it)
                bucket.x = it.x - 64F / 2F
            }
        }
        //子弹发射
        if (Gdx.input.isTouched(1) && TimeUtils.nanoTime() - lastShotTime > 1000000000){
                val bullet = Rectangle().apply {
                    this.x = bucket.x
                    this.y = bucket.y + 64F
                    this.height = 64F
                    this.width = 64F
                    bullets.add(this)
                    lastShotTime = TimeUtils.nanoTime()
            }
        }
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
            spawnRadinDrop()
        }
        val iter = raindrops.iterator().also {
            while (it.hasNext()) {
                val raindrop = it.next()
                raindrop.y -= 200 * Gdx.graphics.deltaTime
                if (raindrop.y + 64 < 0) {
                    it.remove()
                }
                if (raindrop.overlaps(bucket)){
                    dropSound.play()
                    it.remove()
                }
            }
       }
        val bulletIter = bullets.iterator().also {
            while (it.hasNext()){
                val bullet = it.next()
                with(bullet){
                    y += 300 * Gdx.graphics.deltaTime
                    if (y >= Gdx.graphics.height){
                        it.remove()
                    }
                    if (overlapsWithRain(bullet)){
                        it.remove()
                    }
                }
            }
        }

    }

    fun spawnRadinDrop() {
        val raninDrop = Rectangle().also {
            it.x = MathUtils.random(0F, Gdx.graphics.width.toFloat() - 64)
            it.y = Gdx.graphics.height.toFloat()
            it.width = 64F
            it.height = 64F
        }
        raindrops.add(raninDrop)
        lastDropTime = TimeUtils.nanoTime()
    }

    fun overlapsWithRain(bullet: Rectangle): Boolean{
        val rainInter = raindrops.iterator().also {
            while (it.hasNext()){
                val raindrop = it.next()
                if (bullet.overlaps(raindrop)) {
                    it.remove()
                    return true
                }
            }
        }
        return false
    }
}
