package com.broll.gainea.server.core.objects

import com.broll.gainea.net.proto.NT_BoardObject
import com.broll.gainea.server.core.player.Player


open class Building(owner: Player) : MapObject(owner) {

    override fun boardNt(): NT_BoardObject {
        return super.boardNt().toBuilder().setBuilding(true).build()
    }
}
