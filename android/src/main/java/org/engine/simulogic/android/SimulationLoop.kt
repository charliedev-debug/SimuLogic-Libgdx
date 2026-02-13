package org.engine.simulogic.android

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
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
import org.engine.simulogic.android.circuits.components.wireless.ChannelBuffer
import org.engine.simulogic.android.circuits.logic.ComponentManager
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.ConnectionManager
import org.engine.simulogic.android.circuits.logic.Executor
import org.engine.simulogic.android.circuits.storage.AutoSave
import org.engine.simulogic.android.circuits.storage.ProjectOptions
import org.engine.simulogic.android.events.CollisionDetector
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.options.SimulationOptions
import org.engine.simulogic.android.scene.PlayGroundScene
import org.engine.simulogic.android.utilities.FpsCounter
import org.engine.simulogic.android.views.interfaces.ISimulationListener

class SimulationLoop(private val projectOptions: ProjectOptions, private val simulationOptions: SimulationOptions, private val listener:ISimulationListener) : ApplicationAdapter(){

    private lateinit var batch: SpriteBatch
    private lateinit var camera:OrthographicCamera
    lateinit var gestureListener: MotionGestureListener
    lateinit var componentManager:ComponentManager
    private val assetManager = AssetManager()
    private val connection = Connection()
    private val collisionDetector = CollisionDetector(connection)
    private lateinit var scene:PlayGroundScene
    private lateinit var gridDecorator: GridDecorator
    private lateinit var executor: Executor
    var fpsCounter = FpsCounter()
    var isReady = false
    companion object {
         const val CAMERA_WIDTH = 720f
         const val CAMERA_HEIGHT = 1280f
    }
    override fun create() {
        camera = OrthographicCamera()
        camera.setToOrtho(false, CAMERA_WIDTH, CAMERA_HEIGHT)
        camera.zoom = 1.5f
        batch = SpriteBatch()

        assetManager.load("component.atlas", TextureAtlas::class.java)


        assetManager.setLoader(FreeTypeFontGenerator::class.java,FreeTypeFontGeneratorLoader(assetManager.fileHandleResolver))
        assetManager.setLoader(BitmapFont::class.java, FreetypeFontLoader(assetManager.fileHandleResolver))


        val fontParameter = FreeTypeFontLoaderParameter()
            fontParameter.fontFileName = "fonts/RobotoMono-SemiBold.ttf"
            fontParameter.fontParameters.size = 25
        assetManager.load("RobotoMono-SemiBold.ttf", BitmapFont::class.java, fontParameter)

        assetManager.finishLoading()

        scene = PlayGroundScene(spriteBatch = batch, camera = camera, assetManager = assetManager)

       // connection.insertNode(ListNode(CAnd(200f,200f,scene)))
       // connection.insertNode(ListNode(CAnd(400f,300f,scene)))
        gestureListener = MotionGestureListener(camera, connection,collisionDetector,scene)
        gridDecorator = GridDecorator(assetManager.get("RobotoMono-SemiBold.ttf"),scene, camera)
        gestureListener.gridDecorator = gridDecorator
        executor = Executor(connection)
        AutoSave.initialize(projectOptions, gestureListener, connection)
        componentManager = ComponentManager(projectOptions,executor,assetManager.get("RobotoMono-SemiBold.ttf"),connection, scene, gestureListener)
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
        InputMultiplexer().apply {
            addProcessor(GestureDetector(gestureListener))
            addProcessor(object : InputAdapter() {
                override fun keyDown(keycode: Int): Boolean {

                    return keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE
                }
            })
            Gdx.input.inputProcessor = this
        }
        // we loaded the project so we reset this value
        AutoSave.dataChanged = false
        isReady = true
        listener.onCreate()
    }

    override fun render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f)
        connection.update()
        gridDecorator.update()
        gestureListener.update()
        scene.update()
        scene.draw()

        fpsCounter.update()
        executor.execute()
        AutoSave.instance.run()

        gridDecorator.toggleGrid(simulationOptions.showGrid)
        gridDecorator.toggleLabels(simulationOptions.showGridLabel)
        AutoSave.instance.enabled = simulationOptions.autoSaveEnabled
        executor.isActive = simulationOptions.executionEnabled
    }

    override fun dispose() {
        batch.dispose()
        assetManager.dispose()
        ChannelBuffer.clear()
    }



}
