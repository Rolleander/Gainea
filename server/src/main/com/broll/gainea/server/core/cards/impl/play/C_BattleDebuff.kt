package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.RollManipulator
import com.broll.gainea.server.core.battle.RollResult
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.buffs.TimedEffect

class C_BattleDebuff : Card(53, "Pfeilhagel", "-1 Zahl für die feindliche Armee bei eurem nächsten Kampf diesen Zug.") {
    init {
        drawChance = 0.6f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        TimedEffect.forCurrentTurn(game, object : TimedEffect() {
            override fun battleBegin(context: BattleContext, rollManipulator: RollManipulator) {
                rollManipulator.register { attackerRolls: RollResult, defenderRolls: RollResult ->
                    if (context.isAttacker(owner)) {
                        defenderRolls.plusNumber(-1)
                        unregister()
                    } else if (context.isDefender(owner)) {
                        attackerRolls.plusNumber(-1)
                        unregister()
                    }
                }
            }
        })
    }
}
