package com.broll.gainea.server.core.objects

import com.broll.gainea.net.NT_BoardEffect
import com.broll.gainea.net.NT_Event_BoardEffect
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.utils.sendUpdate

class MapEffect(private val type: Int, private val info: String, location: Location) {
    private var x = 0f
    private var y = 0f
    private var id = 0

    init {
        place(location)
    }

    fun add(game: Game) {
        id = game.newObjectId()
        game.effects.add(this)
    }

    fun remove(game: Game) {
        game.effects.remove(this)
    }

    fun place(location: Location) {
        x = location.coordinates.displayX
        y = location.coordinates.displayY
    }

    fun nt(): NT_BoardEffect {
        val nt = NT_BoardEffect()
        nt.id = id
        nt.effect = type
        nt.info = info
        nt.x = x
        nt.y = y
        return nt
    }

    companion object {
        fun spawn(game: Game, vararg effects: MapEffect) {
            for (effect in effects) {
                effect.add(game)
            }
            val nt = NT_Event_BoardEffect()
            nt.effects = effects.map { it.nt() }.toTypedArray()
            game.sendUpdate(nt)
        }

        fun despawn(game: Game, vararg effects: MapEffect) {
            for (effect in effects) {
                effect.remove(game)
            }
            game.sendUpdate(game.nt())
        }
    }
}
