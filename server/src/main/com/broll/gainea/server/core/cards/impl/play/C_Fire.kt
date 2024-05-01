package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_BoardEffect
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.DISRUPTION
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapEffect
import com.broll.gainea.server.core.objects.buffs.TimedEffect

class C_Fire : Card(
    75,
    DISRUPTION,
    "Drachenfeuer",
    "Wählt ein unbesetztes Gebiet (ausser Seen), dieses kann für " + ROUNDS + " Runden nicht besetzt werden."
) {

    override val isPlayable: Boolean
        get() = targets.isNotEmpty()
    private val targets: List<Location>
        get() = game.map.allAreas.filter { it.type !== AreaType.LAKE && it.free }

    override fun play() {
        val target = selectHandler.selectLocation(owner, "Welches Gebiet blockieren?", targets)
        val effect = MapEffect(NT_BoardEffect.EFFECT_FIRE, "", target)
        MapEffect.spawn(game, effect)
        target.traversable = false
        TimedEffect.forPlayerRounds(game, owner, ROUNDS, object : TimedEffect() {
            override fun unregister() {
                super.unregister()
                MapEffect.despawn(game, effect)
                target.traversable = true
            }
        })
    }

    companion object {
        private const val ROUNDS = 5
    }
}
