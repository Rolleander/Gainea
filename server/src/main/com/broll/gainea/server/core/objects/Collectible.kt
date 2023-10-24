package com.broll.gainea.server.core.objects

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.owner
import com.broll.gainea.server.core.utils.remove

class Collectible(game: Game) : MapObject(game.neutralPlayer) {

    lateinit var onPickup: ((Player) -> kotlin.Unit)

    override fun moved(units: List<MapObject>, location: Location) {
        if (location == this.location) {
            val owner = units.filterIsInstance<Unit>().owner()
            if (!owner.isNeutral()) {
                game.remove(this)
                onPickup(owner)
            }
        }
    }

}