package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.map.AreaType.SNOW
import com.broll.gainea.server.core.objects.Conquerable
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.objects.monster.MonsterActivity.OFTEN
import com.broll.gainea.server.core.objects.monster.MonsterBehavior.RANDOM
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.UnitControl.spawn
import org.apache.commons.lang3.mutable.MutableBoolean

class RE_IceCastle : RandomEvent() {


    override fun pickSpot() =
        game.map.allAreas.filter { it.free && it.type == SNOW }.randomOrNull()

    override fun run() {
        game.spawn(IceCastle(game), location)
    }

    private class IceCastle(game: Game) : Conquerable(game) {

        init {
            name = "Eispalast"
            description =
                "Besetzer-Effekt: Ruft einen Eisdämon herbei, immer wenn ihr angegriffen werdet"
            icon = 6
        }

        //todo stirbt nach kampf,  behavior kaputt
        override fun battleIntention(context: BattleContext, cancelFight: MutableBoolean) {
            if (!owner.isNeutral() && context.defendingPlayers.contains(owner)) {
                val m = Monster(owner)
                m.setStats(2, 2)
                m.icon = 140
                m.activity = OFTEN
                m.behavior = RANDOM
                m.controllable = false
                m.name = "Eisdämon"
                game.spawn(m, context.location)
                context.addDefender(m)
            }
        }

    }

}