package org.engine.simulogic.android.utilities

import com.badlogic.gdx.Gdx
import org.engine.simulogic.android.views.interfaces.IFpsListener

class FpsCounter {
    private var elapsedTime = 0f
    private var frames = 0
    fun update(fpsListener:IFpsListener? = null){
          elapsedTime+= Gdx.graphics.deltaTime
          frames++
        if (elapsedTime >= 1f) {
            fpsListener?.onFPSUpdate(frames)
            frames = 0
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
