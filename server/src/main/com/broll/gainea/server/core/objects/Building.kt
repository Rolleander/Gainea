package com.broll.gainea.server.core.objects

import com.broll.gainea.net.NT_BoardObject
import com.broll.gainea.server.core.player.Player


open class Building(owner: Player) : MapObject(owner) {

    override fun nt(): NT_BoardObject {
        val nt = super.nt()
        nt.building = true
        return nt
    }
}