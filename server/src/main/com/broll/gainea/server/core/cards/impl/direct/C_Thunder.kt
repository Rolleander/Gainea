package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCardimport

com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.player.Playerimport com.broll.gainea.server.core.utils.GameUtilsimport java.util.Collections
class C_Thunder : DirectlyPlayedCard(50, "Donnerschauer", "Verursacht 1 Schaden an " + COUNT + " zufälligen Einheiten im Spiel") {
    override fun play() {
        val units: MutableList<Unit?> = ArrayList()
        units.addAll(GameUtils.getUnits(game.objects))
        game.activePlayers.stream().map { obj: Player? -> obj.getUnits() }.forEach { collection: List<Unit?>? -> units.addAll(collection!!) }
        Collections.shuffle(units)
        var i = 0
        while (i < COUNT && i < units.size) {
            damage(game, units[i], 1)
            i++
        }
    }

    companion object {
        private const val COUNT = 7
    }
}
