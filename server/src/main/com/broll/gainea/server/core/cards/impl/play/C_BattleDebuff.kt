package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.battle.BattleContextimport

com.broll.gainea.server.core.battle.RollManipulatorimport com.broll.gainea.server.core.battle.RollResultimport com.broll.gainea.server.core.objects.buffs.TimedEffect com.broll.gainea.server.core.cards.Card
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import com.broll.networklib.server.ICLIExecutor
import kotlin.Throws
import com.broll.networklib.server.ILobbyServerListener

class C_BattleDebuff : Card(53, "Pfeilhagel", "-1 Zahl für die feindliche Armee bei eurem nächsten Kampf diesen Zug.") {
    init {
        drawChance = 0.6f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        TimedEffect.Companion.forCurrentTurn(game!!, object : TimedEffect() {
            override fun battleBegin(context: BattleContext?, rollManipulator: RollManipulator?) {
                rollManipulator!!.register { attackerRolls: RollResult, defenderRolls: RollResult ->
                    if (context!!.isAttacker(owner)) {
                        defenderRolls.plusNumber(-1)
                    } else {
                        attackerRolls.plusNumber(-1)
                    }
                }
                unregister()
            }
        })
    }
}
