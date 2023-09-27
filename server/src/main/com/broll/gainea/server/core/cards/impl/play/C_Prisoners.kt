package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.battle.BattleResultimport

com.broll.gainea.server.core.cards.Cardimport com.broll.gainea.server.core.objects.Soldierimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.buffs.TimedEffectimport com.broll.gainea.server.core.utils.PlayerUtilsimport com.broll.gainea.server.core.utils.UnitControlimport java.util.stream.Collectors
class C_Prisoners : Card(20, "Kriegsgefangene", "Rekrutiert alle besiegte feindliche Soldaten beim nächsten siegreichen Kampf in diesem Zug (Ausser Feldherren)") {
    init {
        drawChance = 0.6f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        TimedEffect.Companion.forCurrentTurn(game!!, object : TimedEffect() {
            override fun battleResult(result: BattleResult) {
                if (result.isWinner(owner)) {
                    val killedSoldiers = result.getOpposingUnits(owner)!!.stream()
                            .filter { it: Unit -> it is Soldier && it.isDead() && !PlayerUtils.isCommander(it) }
                            .collect(Collectors.toList())
                    UnitControl.recruit(game!!, owner!!, killedSoldiers, result.getEndLocation(owner))
                    unregister()
                }
            }
        })
    }
}
