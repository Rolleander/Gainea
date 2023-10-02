package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.buffs.TimedEffect
import com.broll.gainea.server.core.utils.UnitControl.recruit

class C_Prisoners : Card(20, "Kriegsgefangene", "Rekrutiert alle besiegte feindliche Soldaten beim nächsten siegreichen Kampf in diesem Zug (Ausser Feldherren)") {
    init {
        drawChance = 0.6f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        TimedEffect.forCurrentTurn(game, object : TimedEffect() {
            override fun battleResult(result: BattleResult) {
                if (result.isWinner(owner)) {
                    val killedSoldiers = result.getOpposingUnits(owner).filter { it is Soldier && it.dead && !it.isCommander }
                    game.recruit(owner, killedSoldiers, result.getEndLocation(owner))
                    unregister()
                }
            }
        })
    }
}
