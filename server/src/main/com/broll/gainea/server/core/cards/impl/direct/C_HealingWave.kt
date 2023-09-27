package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCardimport

com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.objects.Unit
class C_HealingWave : DirectlyPlayedCard(64, "Kristallenergie", "Heilt alle neutralen Einheiten") {
    override fun play() {
        game.objects.stream().filter { it: MapObject? -> it is Unit }.map<Unit?> { it: MapObject? -> it as Unit? }.filter { obj: Unit? -> obj!!.isHurt }.forEach { unit: Unit? -> heal(game, unit, 10000) }
    }
}
