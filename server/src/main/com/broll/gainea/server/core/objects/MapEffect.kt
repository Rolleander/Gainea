package com.broll.gainea.server.core.objects

import com.broll.gainea.net.NT_BoardEffect
import com.broll.gainea.net.NT_Event_BoardEffect
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.utils.GameUtils
import java.util.Arrays

class MapEffect(private val type: Int, private val info: String, location: Location?) {
    private var x = 0f
    private var y = 0f
    private var id = 0

    init {
        place(location)
    }

    fun add(game: GameContainer) {
        id = game.newObjectId()
        game.effects.add(this)
    }

    fun remove(game: GameContainer) {
        game.effects.remove(this)
    }

    fun place(location: Location?) {
        x = location.getCoordinates().displayX
        y = location.getCoordinates().displayY
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
        fun spawn(game: GameContainer, vararg effects: MapEffect) {
            for (effect in effects) {
                effect.add(game)
            }
            val nt = NT_Event_BoardEffect()
            nt.effects = Arrays.stream<MapEffect>(effects).map<NT_BoardEffect> { obj: MapEffect -> obj.nt() }.toArray<NT_BoardEffect> { _Dummy_.__Array__() }
            GameUtils.sendUpdate(game, nt)
        }

        fun despawn(game: GameContainer, vararg effects: MapEffect) {
            for (effect in effects) {
                effect.remove(game)
            }
            GameUtils.sendUpdate(game, game.nt())
        }
    }
}
