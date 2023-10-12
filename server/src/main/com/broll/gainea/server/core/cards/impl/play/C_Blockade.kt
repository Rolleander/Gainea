package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.buffs.BuffType
import com.broll.gainea.server.core.objects.buffs.IntBuff
import com.broll.gainea.server.core.objects.buffs.roundsActive
import com.broll.gainea.server.core.utils.UnitControl.spawn

class C_Blockade : Card(54, "Burgfried", "Platziert eine neutrale Befestigung (3/10) auf ein beliebiges freies Feld. Sie zerfällt nach " + ROUNDS + " Runden.") {
    init {
        drawChance = 0.4f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val buff = IntBuff(BuffType.ADD, 10)
        val soldier = Blockade(buff)
        soldier.addHealthBuff(buff)
        val locations = game.map.allAreas.filter { it.free }
        val location = selectHandler.selectLocation("Wo soll die Befestigung errichtet werden?", locations)
        game.spawn(soldier, location)
        game.buffProcessor.timeoutBuff(buff, ROUNDS)
    }

    private inner class Blockade(val buff: IntBuff) : Unit(game.neutralPlayer) {
        init {
            icon = 127
            name = "Befestigung"
            setStats(3, 0)
            updateDescription()
        }

        private fun updateDescription() {
            description = "Verbleibende Runden: ${ROUNDS - buff.roundsActive(game)}"
        }

        override fun turnStart() {
            updateDescription()
            super.turnStart()
        }
    }

    companion object {
        private const val ROUNDS = 7
    }
}
