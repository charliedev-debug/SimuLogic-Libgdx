package org.engine.simulogic.android.circuits.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.engine.simulogic.android.dataStore

class UserSettings {

    companion object {
        val GRID_LABEL_ENABLED = booleanPreferencesKey("GRID_LABEL_ENABLED")
        val GRID_ENABLED = booleanPreferencesKey("GRID_ENABLED")
        val GRID_STYLE = intPreferencesKey("GRID_STYLE")
        val TOOLBAR_ENABLED = booleanPreferencesKey("TOOLBAR_ENABLED")
        val AUTO_SAVE_ENABLED = booleanPreferencesKey("AUTO_SAVE_ENABLED")
    }

    fun prepare(){

    }

    suspend fun saveBooleanPref(context: Context, key:Preferences.Key<Boolean>, value:Boolean){
          context.dataStore.edit {pref->
              pref[key] = value
          }
    }

    fun getDataBoolean(context: Context, key: Preferences.Key<Boolean>, default:Boolean = true): Flow<Boolean> {
        return context.dataStore.data.map { it[key]?:default }
    }
}
