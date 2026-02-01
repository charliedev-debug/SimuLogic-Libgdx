package org.engine.simulogic.android.utilities

import com.badlogic.gdx.Gdx

class Timer(private val limit:Float, private val listener: ITimerListener) {
    private var elapsedTime = 0f
    fun update(){
        elapsedTime+= Gdx.graphics.deltaTime
        if (elapsedTime >= limit) {
            listener.onTick()
            elapsedTime = 0f
        }
    }

    fun getTime():Float{
        return elapsedTime
    }

    interface ITimerListener{
        fun onTick()
    }

}
