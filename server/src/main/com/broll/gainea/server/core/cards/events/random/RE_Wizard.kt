package com.broll.gainea.server.core.cards.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.cards.RandomEvent
import com.broll.gainea.server.core.cards.impl.play.C_Buff
import com.broll.gainea.server.core.cards.impl.play.C_HealingSpell
import com.broll.gainea.server.core.fractions.impl.FireRain
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_Wizard : RandomEvent() {
    override fun run(game: Game) {
        game.freeArea { area ->
            val wizard = object : Soldier(game.neutralPlayer) {
                override fun onDeath(throughBattle: BattleResult?) {
                    throughBattle?.getKillingPlayers(this)?.forEach {
                        with(it.cardHandler) {
                            receiveCard(C_Buff())
                            receiveCard(FireRain())
                            receiveCard(C_HealingSpell())
                        }
                    }
                }
            }
            wizard.description = "Verleiht 3 Zauber an Bezwinger"
            wizard.icon = 47
            wizard.setStats(4, 3)
            game.spawn(wizard, area)
        }
    }

}