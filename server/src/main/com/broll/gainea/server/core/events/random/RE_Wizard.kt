package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.cards.impl.play.C_DirectPlay
import com.broll.gainea.server.core.cards.impl.play.C_HealingSpell
import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeArea
import com.broll.gainea.server.core.fractions.impl.FireRain
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_Wizard : RandomEvent() {

    override fun pickSpot() = game.freeArea()
    override fun run() {
        val wizard = object : Soldier(game.neutralPlayer) {
            override fun onDeath(throughBattle: BattleResult?) {
                throughBattle?.getKillingPlayers(this)?.forEach {
                    with(it.cardHandler) {
                        receiveCard(FireRain())
                        receiveCard(C_HealingSpell())
                        receiveCard(C_DirectPlay())
                    }
                }
            }
        }
        wizard.name = "Zaubermeister"
        wizard.description = "Verleiht 3 Karten an Bezwinger"
        wizard.icon = 47
        wizard.setStats(4, 3)
        game.spawn(wizard, location)
    }

}