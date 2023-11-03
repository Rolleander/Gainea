package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.buffs.BuffType
import com.broll.gainea.server.core.objects.buffs.IntBuff

class C_BattleSummon :
    Card(66, "Rachedämon", "Beschwört einen Rachedämon (5/3) der nächste Runde stirbt.") {
    init {
        drawChance = 0.6f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val locations = owner.controlledLocations
        val demon = Soldier(owner)
        demon.setStats(5, 0)
        demon.icon = 125
        demon.name = "Rachedämon"
        demon.description = "Stirbt in einer Runde"
        val buff = IntBuff(BuffType.ADD, 3)
        demon.addHealthBuff(buff)
        demon.prepareForTurnStart()
        placeUnitHandler.placeUnit(
            owner,
            demon,
            locations.toList(),
            "Wählt einen Ort für die Beschwörung"
        )
        game.buffProcessor.timeoutBuff(buff, 1)
    }
}
