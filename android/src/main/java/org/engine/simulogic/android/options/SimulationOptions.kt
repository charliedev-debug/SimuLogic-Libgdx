package org.engine.simulogic.android.options

import java.io.Serializable

data class SimulationOptions(var showTopBar:Boolean = true, var showGrid:Boolean = true,
                             var showGridLabel:Boolean = true, var showMenu:Boolean = true, var autoSaveEnabled:Boolean = false, var executionEnabled:Boolean = true, var theme:String = "rosepine"):
    Serializable
