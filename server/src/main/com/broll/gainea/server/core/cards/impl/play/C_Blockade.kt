package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.buffs.BuffTypeimport com.broll.gainea.server.core.objects.buffs.IntBuffimport java.util.stream.Collectors
class C_Blockade : Card(54, "Burgfried", "Platziert eine neutrale Befestigung (3/10) auf ein beliebiges freies Feld. Sie zerfällt nach " + ROUNDS + " Runden.") {
    init {
        drawChance = 0.4f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val soldier = Blockade()
        val buff = IntBuff(BuffType.ADD, 10)
        soldier.addHealthBuff(buff)
        val locations = game.map.allAreas.stream().filter { obj: Area? -> obj!!.isFree }.collect(Collectors.toList())
        val location = selectHandler!!.selectLocation("Wo soll die Befestigung errichtet werden?", locations)
        spawn(game, soldier, location)
        game.buffProcessor.timeoutBuff(buff, ROUNDS)
    }

    private inner class Blockade : Unit(null) {
        init {
            icon = 127
            name = "Befestigung"
            setStats(3, 0)
        }
    }

    companion object {
        private const val ROUNDS = 7
    }
}
