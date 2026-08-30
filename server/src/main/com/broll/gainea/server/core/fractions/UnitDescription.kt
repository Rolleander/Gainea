package com.broll.gainea.server.core.fractions

import com.broll.gainea.net.proto.NT_Unit

data class UnitDescription(
    val name: String,
    val icon: Int,
    val power: Int = 1,
    val health: Int = 1,
    val description: String? = null
) {

    fun nt(): NT_Unit {
        val builder = NT_Unit.newBuilder()
            .setHealth(health)
            .setPower(power)
            .setName(name)
            .setIcon(icon)
        description?.let(builder::setDescription)
        return builder.build()
    }
}
