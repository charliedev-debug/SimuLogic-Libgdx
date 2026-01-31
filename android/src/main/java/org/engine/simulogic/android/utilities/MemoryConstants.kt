package org.engine.simulogic.android.utilities

import androidx.annotation.IntDef


object MemoryConstants {
    const val BYTE = 1
    const val KB = 1024
    const val MB = 1048576
    const val GB = 1073741824

    @IntDef(*[BYTE, KB, MB, GB])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Unit
}
