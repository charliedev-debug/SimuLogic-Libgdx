package org.engine.simulogic.android.circuits.components.other
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import org.engine.simulogic.android.circuits.components.CNode
import org.engine.simulogic.android.circuits.components.lines.CLine
import org.engine.simulogic.android.circuits.theme.EnvironmentTheme
import org.engine.simulogic.android.scene.PlayGroundScene

class CAlignmentLine(x:Float, y:Float, width:Float, height:Float, private val scene: PlayGroundScene): CNode() {
    private val lines = mutableListOf<CLine>()
    init {
        val textureAtlas = scene.assetManager.get("${EnvironmentTheme.name}.atlas", TextureAtlas::class.java)
        val spriteRegion = textureAtlas.findRegion("TRANSPARENT")
        sprite = Sprite(spriteRegion).apply {
            setOrigin(x, y)
            setSize(width, height)
            setOriginCenter()
            rotation = 0f
            setPosition(x - width / 2f, y - height / 2f)
        }
    }
}
