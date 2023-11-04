package com.broll.gainea.server.core.objects

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.UnitControl.despawn
import com.broll.gainea.server.core.utils.getUnits
import com.broll.gainea.server.core.utils.owner

open class Collectible(
    game: Game,
    val despawn: Boolean = true,
    val affectNeutral: Boolean = false
) : MapObject(game.neutralPlayer) {


    lateinit var onPickup: ((Player) -> kotlin.Unit)

    override fun unitsMoved(objects: List<MapObject>, location: Location) {
        if (location == this.location && objects.getUnits().isNotEmpty()) {
            val owner = objects.getUnits().owner()
            if (!owner.isNeutral() || affectNeutral) {
                if (despawn) {
                    game.despawn(this)
                }
                onPickup(owner)
            }
        }
    }

}