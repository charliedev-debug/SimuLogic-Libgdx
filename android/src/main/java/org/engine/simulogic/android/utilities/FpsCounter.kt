package org.engine.simulogic.android.utilities

import com.badlogic.gdx.Gdx

class FpsCounter {
    private var elapsedTime = 0f
    private var frames = 0
    private var frameCounter = 0
    fun update(){
          elapsedTime+= Gdx.graphics.deltaTime
        frameCounter++
        if (elapsedTime >= 1f) {
            frames = frameCounter
            frameCounter = 0
            elapsedTime = 0f
        }
    }

    fun getFps():Int{
        return frames
    }

    fun getTime():Float{
        return elapsedTime
    }

}
