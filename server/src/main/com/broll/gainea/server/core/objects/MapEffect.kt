package com.broll.gainea.server.core.objects

import com.broll.gainea.core.protocol.BoardEffectType
import com.broll.gainea.net.proto.NT_BoardEffect
import com.broll.gainea.net.proto.NT_Event_BoardEffect
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.utils.sendUpdate

class MapEffect(private val type: BoardEffectType, private val info: String, location: Location) {
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
        return NT_BoardEffect.newBuilder().setId(id).setEffect(type.value).setInfo(info)
            .setX(x).setY(y).build()
    }

    companion object {
        fun spawn(game: Game, vararg effects: MapEffect) {
            for (effect in effects) {
                effect.add(game)
            }
            game.sendUpdate(NT_Event_BoardEffect.newBuilder()
                .addAllEffects(effects.map { it.nt() }).build())
        }

        fun despawn(game: Game, vararg effects: MapEffect) {
            for (effect in effects) {
                effect.remove(game)
            }
            game.sendUpdate(game.nt())
        }
    }
}
