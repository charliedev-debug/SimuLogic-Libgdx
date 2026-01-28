package org.engine.simulogic.android

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.utils.ScreenUtils
import org.engine.simulogic.android.circuits.components.decorators.GridDecorator
import org.engine.simulogic.android.circuits.components.gates.CAnd
import org.engine.simulogic.android.circuits.logic.ComponentManager
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ConnectionManager
import org.engine.simulogic.android.circuits.logic.ListNode
import org.engine.simulogic.android.events.CollisionDetector
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.scene.PlayGroundScene
import org.engine.simulogic.android.utilities.FpsCounter
import org.engine.simulogic.android.views.interfaces.IFpsListener

class SimulationLoop(private val fpsListener:IFpsListener) : ApplicationAdapter() {

    private lateinit var batch: SpriteBatch
    private lateinit var camera:OrthographicCamera
    lateinit var gestureListener: MotionGestureListener
    lateinit var componentManager:ComponentManager
    private var fpsCounter = FpsCounter()
    private val assetManager = AssetManager()
    private val connection = Connection()
    private val collisionDetector = CollisionDetector(connection)
    private lateinit var connectionManager : ConnectionManager
    private lateinit var scene:PlayGroundScene
    private lateinit var gridDecorator: GridDecorator
    var isReady = false
    companion object {
         const val CAMERA_WIDTH = 720f
         const val CAMERA_HEIGHT = 1280f
    }
    override fun create() {
        camera = OrthographicCamera()
        camera.setToOrtho(false, CAMERA_WIDTH, CAMERA_HEIGHT)
        batch = SpriteBatch()

        assetManager.load("component.atlas", TextureAtlas::class.java)


        assetManager.setLoader(FreeTypeFontGenerator::class.java,FreeTypeFontGeneratorLoader(assetManager.fileHandleResolver))
        assetManager.setLoader(BitmapFont::class.java, FreetypeFontLoader(assetManager.fileHandleResolver))


        val fontParameter = FreeTypeFontLoaderParameter()
            fontParameter.fontFileName = "fonts/RobotoMono-SemiBold.ttf"
            fontParameter.fontParameters.size = 15
        assetManager.load("RobotoMono-SemiBold.ttf", BitmapFont::class.java, fontParameter)

        assetManager.finishLoading()


        scene = PlayGroundScene(spriteBatch = batch, camera = camera, assetManager = assetManager)

        //connection.insertNode(ListNode(CAnd(200f,200f,scene)))
      //  connection.insertNode(ListNode(CAnd(400f,300f,scene)))
        gestureListener = MotionGestureListener(camera, connection,collisionDetector,scene)
        connectionManager = ConnectionManager(connection, collisionDetector, scene)
        gridDecorator = GridDecorator(assetManager.get("RobotoMono-SemiBold.ttf"),scene, camera)
        gestureListener.gridDecorator = gridDecorator
        componentManager = ComponentManager(connection, assetManager, scene, gestureListener)
        componentManager.read()
        InputMultiplexer().apply {
            addProcessor(GestureDetector(gestureListener))
            Gdx.input.inputProcessor = this
        }
        isReady = true
    }

    override fun render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f)
        connection.update()
        gridDecorator.update()
        connectionManager.resolveConnection()
        scene.update()
        scene.draw()
        fpsCounter.update(fpsListener)
    }

    override fun dispose() {
        batch.dispose()
        assetManager.dispose()
    }
}
