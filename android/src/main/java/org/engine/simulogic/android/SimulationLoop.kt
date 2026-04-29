package org.engine.simulogic.android

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.PixmapPacker
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.utils.ScreenUtils
import org.engine.simulogic.android.circuits.components.CDefaults
import org.engine.simulogic.android.circuits.components.decorators.GridDecorator
import org.engine.simulogic.android.circuits.components.wireless.ChannelBuffer
import org.engine.simulogic.android.circuits.logic.ComponentManager
import org.engine.simulogic.android.circuits.logic.Connection
import org.engine.simulogic.android.circuits.logic.Executor
import org.engine.simulogic.android.circuits.storage.AutoSave
import org.engine.simulogic.android.circuits.storage.ProjectOptions
import org.engine.simulogic.android.circuits.theme.EnvironmentTheme
import org.engine.simulogic.android.events.CollisionDetector
import org.engine.simulogic.android.events.MotionGestureListener
import org.engine.simulogic.android.options.SimulationOptions
import org.engine.simulogic.android.scene.PlayGroundScene
import org.engine.simulogic.android.utilities.FpsCounter
import org.engine.simulogic.android.utilities.TimerManager
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
         var isPortrait = true
         var offsetTop = 0f
         val offsetLeft = 0f
    }

    override fun create() {
        camera = OrthographicCamera()
        // set camera according to screen orientation
        if (Gdx.graphics.width > Gdx.graphics.height) {
            isPortrait = false
            camera.setToOrtho(false, CAMERA_HEIGHT, CAMERA_WIDTH)
        } else {
            isPortrait = true
            camera.setToOrtho(false, CAMERA_WIDTH, CAMERA_HEIGHT)
        }

        camera.zoom = 1.0f
        batch = SpriteBatch()

        assetManager.load("${simulationOptions.theme}.atlas", TextureAtlas::class.java)


        assetManager.setLoader(FreeTypeFontGenerator::class.java,FreeTypeFontGeneratorLoader(assetManager.fileHandleResolver))
        assetManager.setLoader(BitmapFont::class.java, FreetypeFontLoader(assetManager.fileHandleResolver))

        EnvironmentTheme.name = simulationOptions.theme
        when(simulationOptions.theme){
            "rosepine"->{
                EnvironmentTheme.colorBackground = Color(  25f/255f, 23f/255f, 36f/255f, 1f)
                EnvironmentTheme.colorOutline = Color(64f/255f, 61f/255f, 82f/255f, 1f)
                EnvironmentTheme.colorOutline2 = Color(102f/255f, 97f/255f, 130f/255f, 1f)
                EnvironmentTheme.colorPrimary = Color(235f/255f, 111f/255f, 146f/255f, 1f)
                EnvironmentTheme.colorOnBackground = Color(224f/255f, 222f/255f, 244f/255f, 1f)
                EnvironmentTheme.colorSecondary = Color(156f/255f, 207f/255f, 216f/255f, 1f)
            }
            "gruvbox"->{
                EnvironmentTheme.colorBackground = Color(  29f/255f, 32f/255f, 33f/255f, 1f)
                EnvironmentTheme.colorOutline = Color(80f/255f, 73f/255f, 69f/255f, 1f)
                EnvironmentTheme.colorOutline2 = Color( 113f/255f, 101f/255f, 94f/255f, 1f)
                EnvironmentTheme.colorPrimary = Color(131f/255f, 165f/255f, 152f/255f, 1f)
                EnvironmentTheme.colorOnBackground = Color(235f/255f, 219f/255f, 178f/255f, 1f)
                EnvironmentTheme.colorSecondary = Color(118f/255f, 199f/255f, 88f/255f, 1f)
            }
            "tokyonight"->{
                EnvironmentTheme.colorBackground = Color(  26f/255f, 27f/255f, 38f/255f, 1f)
                EnvironmentTheme.colorOutline = Color(80f/255f, 73f/255f, 69f/255f, 1f)
                EnvironmentTheme.colorOutline2 = Color( 111f/255f, 100f/255f, 93f/255f, 1f)
                EnvironmentTheme.colorPrimary = Color(122f/255f, 162f/255f, 247f/255f, 1f)
                EnvironmentTheme.colorOnBackground = Color(192f/255f, 202f/255f, 245f/255f, 1f)
                EnvironmentTheme.colorSecondary = Color(125f/255f, 207f/255f, 255f/255f, 1f)
            }
            "kanagawa"->{
                EnvironmentTheme.colorBackground = Color(  31f/255f, 31f/255f, 40f/255f, 1f)
                EnvironmentTheme.colorOutline = Color(84f/255f, 84f/255f, 109f/255f, 1f)
                EnvironmentTheme.colorOutline2 = Color(114f/255f, 114f/255f, 149f/255f, 1f)
                EnvironmentTheme.colorPrimary = Color(126f/255f, 156f/255f, 216f/255f, 1f)
                EnvironmentTheme.colorOnBackground = Color(220f/255f, 215f/255f, 186f/255f, 1f)
                EnvironmentTheme.colorSecondary = Color(152f/255f, 187f/255f, 108f/255f, 1f)
            }
            "catppuccin"->{
                EnvironmentTheme.colorBackground = Color(  30f/255f, 30f/255f, 46f/255f, 1f)
                EnvironmentTheme.colorOutline = Color(69f/255f, 71f/255f, 90f/255f, 1f)
                EnvironmentTheme.colorOutline2 = Color(113f/255f, 117f/255f, 147f/255f, 1f)
                EnvironmentTheme.colorPrimary = Color(137f/255f, 180f/255f, 250f/255f, 1f)
                EnvironmentTheme.colorOnBackground = Color(205f/255f, 214f/255f, 244f/255f, 1f)
                EnvironmentTheme.colorSecondary = Color(166f/255f, 227f/255f, 161f/255f, 1f)
            }

        }

        val fontParameter = FreeTypeFontLoaderParameter()
            fontParameter.fontFileName = "fonts/RobotoMono-SemiBold.ttf"
            fontParameter.fontParameters.size = CDefaults.MAX_FONT_RESOLUTION
            fontParameter.fontParameters.packer =
                PixmapPacker(4096, 4096, Pixmap.Format.RGBA8888, 2, false);
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
        componentManager = ComponentManager(projectOptions,assetManager.get("RobotoMono-SemiBold.ttf"),connection, scene, gestureListener)
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
        listener.onCreate()
    }

    // set camera according to screen orientation
    override fun resize(width: Int, height: Int) {
           val x = camera.position.x
           val y = camera.position.y
            if (width > height && isPortrait) {
                isPortrait = false
                camera.setToOrtho(false, CAMERA_HEIGHT, CAMERA_WIDTH)
                camera.position.set(x, y, 0f)
            } else if(width < height && !isPortrait) {
                isPortrait = true
                camera.setToOrtho(false, CAMERA_WIDTH, CAMERA_HEIGHT)
                camera.position.set(x, y, 0f)
            }
        gestureListener.gridDecorator?.refresh = true
    }

    override fun pause() {
        super.pause()

    }

    override fun render() {
        ScreenUtils.clear(EnvironmentTheme.colorBackground)
        if(isReady) {
            connection.update()
            gridDecorator.update()
            gestureListener.update()
            componentManager.eventBridge.evaluate()
            if (simulationOptions.executionEnabled) {
                TimerManager.getInstance().update()
            }


            scene.update()
            scene.draw()

            fpsCounter.update()
            executor.execute()
            AutoSave.instance.run()
        }

        gridDecorator.toggleGrid(simulationOptions.showGrid)
        gridDecorator.toggleLabels(simulationOptions.showGridLabel)
        AutoSave.instance.enabled = simulationOptions.autoSaveEnabled
        executor.isActive = simulationOptions.executionEnabled
    }

    override fun dispose() {
        batch.dispose()
        assetManager.dispose()
        ChannelBuffer.clear()
        TimerManager.reset()
    }
}
