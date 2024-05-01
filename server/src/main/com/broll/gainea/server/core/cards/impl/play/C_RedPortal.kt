package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_BoardEffect
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.MOVEMENT
import com.broll.gainea.server.core.objects.MapEffect
import com.broll.gainea.server.core.objects.buffs.TimedEffect

class C_RedPortal : Card(
    1, MOVEMENT, "Dunkles Portal",
    "Wählt zwei freie Gebiete von der gleichen Karte. Stellt ein Portal zwischen diesen Gebieten für " + ROUNDS + " Runden her."
) {
    init {
        drawChance = 0.6f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val from = selectHandler.selectLocation(
            "Wähle den Startort für das Portal",
            game.map.allAreas.filter { it.free })
        val toLocations = from.container.expansion.allAreas
            .filter { it.free && it !== from && !from.connectedLocations.contains(it) }
        if (toLocations.isEmpty()) {
            return;
        }
        val startPortal = MapEffect(NT_BoardEffect.EFFECT_PORTAL, "", from)
        MapEffect.spawn(game, startPortal)
        val to = selectHandler.selectLocation("Wähle den Zielort für das Portal", toLocations)
        val endPortal = MapEffect(NT_BoardEffect.EFFECT_PORTAL, "", to)
        MapEffect.spawn(game, endPortal)
        from.connectedLocations.add(to)
        to.connectedLocations.add(from)
        TimedEffect.forPlayerRounds(game, owner, ROUNDS, object : TimedEffect() {
            override fun unregister() {
                super.unregister()
                MapEffect.despawn(game, startPortal)
                MapEffect.despawn(game, endPortal)
                from.connectedLocations.remove(to)
                to.connectedLocations.remove(from)
            }
        })
    }

    companion object {
        private const val ROUNDS = 4
    }
}
