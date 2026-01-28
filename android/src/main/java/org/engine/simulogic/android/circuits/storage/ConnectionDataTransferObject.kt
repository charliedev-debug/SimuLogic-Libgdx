package org.engine.simulogic.android.circuits.storage

data class ConnectionDataTransferObject(val from:Int, val to:Int, val signalFrom:Int, val signalTo:Int, val list:MutableList<SignalDataTransferObject> = mutableListOf())
