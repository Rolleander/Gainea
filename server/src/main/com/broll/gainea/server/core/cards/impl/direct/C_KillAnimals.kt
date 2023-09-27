package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCardimport

com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.objects.monster.GodDragonimport com.broll.gainea.server.core.objects.monster.Monsterimport com.broll.gainea.server.core.utils.StreamUtilsimport com.broll.gainea.server.core.utils.UnitControl
class C_KillAnimals : DirectlyPlayedCard(5, "Trockenzeit", COUNT.toString() + " schwache Monster sterben aus") {
    override fun play() {
        StreamUtils.safeForEach(
                game.objects.stream().filter { it: MapObject? -> it is Monster && it is GodDragon == false }.map { it: MapObject? -> it as Monster? }.sorted { m1: Monster?, m2: Monster? -> Integer.compare(m1.getStars(), m2.getStars()) }.limit(COUNT.toLong())
        ) { monster: Monster? -> UnitControl.kill(game!!, monster) }
    }

    companion object {
        private const val COUNT = 3
    }
}
