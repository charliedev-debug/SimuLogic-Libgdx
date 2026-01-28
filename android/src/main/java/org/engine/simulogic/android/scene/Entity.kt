package org.engine.simulogic.android.scene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.gates.CSignal
import java.util.Collections
import java.util.concurrent.CopyOnWriteArrayList

abstract class Entity {
    var value = 0
    var rotation = 0
    var selected = false
    var isVisible = true
    var isRemoved = false
    protected val data: MutableList<Entity> = Collections.synchronizedList(mutableListOf<Entity>())// mutableListOf<Entity>()
    val signals = mutableListOf<CSignal>()

    open fun setWidth(value:Float){

    }

    open fun setHeight(value: Float){

    }

    open fun setSize(width:Float, height:Float){

    }

    open fun attachChild(entity: Entity) {
        synchronized(data){
            data.add(entity)
        }
    }

    open fun attachSelf(){
        isRemoved = false
    }

    open fun detachSelf(){
        isRemoved = true
    }

    open fun detachChild(entity: Entity) {
        synchronized(data){
            data.remove(entity)
        }
    }

    open fun attachChildAt(index: Int) {
        synchronized(data){
            data.removeAt(index)
        }
    }

    open fun detachChildAt(index: Int) {
        synchronized(data){
            data.removeAt(index)
        }
    }

    fun autoDetachChildren(){
        synchronized(data) {
            data.removeIf { it.isRemoved }
        }
    }
    open fun updatePosition(x:Float, y:Float){}
    open fun updatePosition(position:Vector2){}
    open fun updatePosition(position:Vector3){}
    open fun updatePosition(x1:Float, y1:Float, x2:Float, y2:Float){}
    open fun updateColor(color:Color){}
    open fun getPosition():Vector2?{
        return null
    }
    open fun getCenter():Vector2?{
        return null
    }
    open fun getChildAt(index:Int):Entity{
        return data[index]
    }
    open fun update(){}
    open fun draw(){}
    open fun draw(spriteBatch: SpriteBatch){}
    open fun draw(shapeRenderer: ShapeRenderer){}
    open fun clone():Entity?{return null}


}
