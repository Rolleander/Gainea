package com.broll.gainea.server.core.fractions

import com.broll.gainea.net.NT_Unit

data class UnitDescription(
    val name: String,
    val icon: Int,
    val power: Int = 1,
    val health: Int = 1,
    val description: String? = null
) {

    fun nt() = NT_Unit().also {
        it.health = health.toShort()
        it.power = power.toShort()
        it.name = name
        it.icon = icon.toShort()
        it.description = description
    }
}
