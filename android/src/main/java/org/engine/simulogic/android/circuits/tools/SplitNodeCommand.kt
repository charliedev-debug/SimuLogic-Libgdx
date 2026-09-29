package org.engine.simulogic.android.circuits.tools

import com.badlogic.gdx.math.Vector2
import org.engine.simulogic.android.circuits.components.gates.CSignal
import org.engine.simulogic.android.circuits.components.lines.LineMarker

class SplitNodeCommand (private val marker: LineMarker ) : Command() {

    private val previous = mutableListOf<Pair<CSignal, Vector2>>()
    private val current = mutableListOf<Pair<CSignal, Vector2>>()

    fun fetchPrevious(){
        marker.signals.onEach {
            previous.add(Pair(it, Vector2(it.getPosition())))
        }
    }

    fun fetchCurrent(){
        marker.signals.onEach {
            current.add(Pair(it,Vector2(it.getPosition())))
        }
    }

    override fun undo() {
        marker.hardResetSignals(previous)
    }

    override fun redo() {
        marker.hardResetSignals(current)
    }
}
