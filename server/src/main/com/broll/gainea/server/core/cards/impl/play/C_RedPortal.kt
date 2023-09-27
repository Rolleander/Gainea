package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_BoardEffectimport

com.broll.gainea.server.core.cards.Cardimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.objects.MapEffectimport com.broll.gainea.server.core.objects.buffs.TimedEffectimport java.util.stream.Collectors
class C_RedPortal : Card(1, "Dunkles Portal",
        "Wählt zwei freie Gebiete von der gleichen Karte. Stellt ein Portal zwischen diesen Gebieten für " + ROUNDS + " Runden her.") {
    init {
        drawChance = 0.6f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val from = selectHandler!!.selectLocation("Wähle den Startort für das Portal", game.map.allAreas.stream().filter { obj: Area? -> obj!!.isFree }.collect(Collectors.toList()))
        val startPortal = MapEffect(NT_BoardEffect.EFFECT_PORTAL, "", from)
        MapEffect.Companion.spawn(game!!, startPortal)
        val to = selectHandler!!.selectLocation("Wähle den Zielort für das Portal", from.container.expansion.allAreas.stream()
                .filter { it: Area? -> it!!.isFree && it !== from && !from.connectedLocations.contains(it) }.collect(Collectors.toList()))
        if (to != null) {
            val endPortal = MapEffect(NT_BoardEffect.EFFECT_PORTAL, "", to)
            MapEffect.Companion.spawn(game!!, endPortal)
            from.connectedLocations.add(to)
            to.connectedLocations.add(from)
            TimedEffect.Companion.forPlayerRounds(game!!, owner, ROUNDS, object : TimedEffect() {
                override fun unregister() {
                    super.unregister()
                    MapEffect.Companion.despawn(game!!, startPortal)
                    MapEffect.Companion.despawn(game!!, endPortal)
                    from.connectedLocations.remove(to)
                    to.connectedLocations.remove(from)
                }
            })
        } else {
            MapEffect.Companion.despawn(game!!, startPortal)
        }
    }

    companion object {
        private const val ROUNDS = 4
    }
}
