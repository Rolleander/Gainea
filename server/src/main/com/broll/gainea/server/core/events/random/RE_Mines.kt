package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.cards.impl.play.C_Buff
import com.broll.gainea.server.core.cards.impl.play.C_BuffAttack
import com.broll.gainea.server.core.cards.impl.play.C_BuffDefence
import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.map.AreaType.MOUNTAIN
import com.broll.gainea.server.core.objects.Conquerable
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_Mines : RandomEvent() {


    override fun pickSpot() =
        game.map.allAreas.filter { it.free && it.type == MOUNTAIN }.randomOrNull()

    override fun run() {
        val obj = Conquerable(game)
        obj.whenRoundsHold = { player ->
            player.cardHandler.receiveCard(
                listOf(
                    C_Buff(), C_BuffAttack(), C_BuffDefence()
                ).random()
            )
        }
        obj.name = "Zwergenmine"
        obj.description = "Besetzer erhält jede Runde eine Ausrüstungs-Karte"
        obj.icon = 3
        game.spawn(obj, location)
        val m = Monster(game.neutralPlayer)
        m.icon = 96
        m.name = "Lavagolem"
        m.setStats(5, 5)
        game.spawn(m, location)
    }

}