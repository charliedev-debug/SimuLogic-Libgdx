package org.engine.simulogic.android.utilities

import android.annotation.SuppressLint

class ConvertUtils private constructor() {
    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        @SuppressLint("DefaultLocale")
        fun byte2FitMemorySize(byteSize: Long, precision: Int): String {
            require(precision >= 0) { "precision shouldn't be less than zero!" }
            return if (byteSize < 0) {
                throw IllegalArgumentException("byteSize shouldn't be less than zero!")
            } else if (byteSize < MemoryConstants.KB) {
                String.format("%." + precision + "fB", byteSize.toDouble())
            } else if (byteSize < MemoryConstants.MB) {
                java.lang.String.format(
                    "%." + precision + "fKB",
                    byteSize.toDouble() / MemoryConstants.KB
                )
            } else if (byteSize < MemoryConstants.GB) {
                java.lang.String.format(
                    "%." + precision + "fMB",
                    byteSize.toDouble() / MemoryConstants.MB
                )
            } else {
                java.lang.String.format(
                    "%." + precision + "fGB",
                    byteSize.toDouble() / MemoryConstants.GB
                )
            }
        }
        fun kb2FitMemorySize(kbSize: Long, precision: Int): String {
            require(precision >= 0) { "precision shouldn't be less than zero!" }
            return if (kbSize < 0) {
                throw IllegalArgumentException("byteSize shouldn't be less than zero!")
            } else if (kbSize < MemoryConstants.MB) {
                java.lang.String.format(
                    "%." + precision + "fKB",
                    kbSize.toDouble() / MemoryConstants.KB
                )
            } else if (kbSize < MemoryConstants.GB) {
                java.lang.String.format(
                    "%." + precision + "fMB",
                    kbSize.toDouble() / MemoryConstants.MB
                )
            } else {
                java.lang.String.format(
                    "%." + precision + "fGB",
                    kbSize.toDouble() / MemoryConstants.GB
                )
            }
        }
        fun kb2FitMemorySize(kbSize: Double, precision: Int): String {
            require(precision >= 0) { "precision shouldn't be less than zero!" }
            return if (kbSize < 0) {
                throw IllegalArgumentException("byteSize shouldn't be less than zero!")
            } else if (kbSize < MemoryConstants.MB) {
                java.lang.String.format(
                    "%." + precision + "fKB",
                    kbSize / MemoryConstants.KB
                )
            } else if (kbSize < MemoryConstants.GB) {
                java.lang.String.format(
                    "%." + precision + "fMB",
                    kbSize/ MemoryConstants.MB
                )
            } else {
                java.lang.String.format(
                    "%." + precision + "fGB",
                    kbSize/ MemoryConstants.GB
                )
            }
        }
    }
}
