package org.engine.simulogic.android.ui.models

import org.engine.simulogic.android.ui.adapters.SettingsPrefAdapter

data class SettingsOption(val viewType:Int, val title: String, val description: String,
                          val drawableLeft: Int, val selected: Boolean = false, var listener: SettingsPrefAdapter.OnItemClickListener? = null)
