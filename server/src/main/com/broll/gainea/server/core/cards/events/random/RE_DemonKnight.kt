package com.broll.gainea.server.core.cards.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.RandomEvent
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.objects.monster.MonsterActivity
import com.broll.gainea.server.core.objects.monster.MonsterBehavior
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.getWeakestPlayer

class RE_DemonKnight : RandomEvent() {
    override fun run(game: Game) {
        game.freeArea { area ->
            val demon = Monster(game.getWeakestPlayer())
            demon.icon = 93
            demon.controllable = false
            demon.behavior = MonsterBehavior.AGGRESSIVE
            demon.activity = MonsterActivity.OFTEN
            demon.setStats(4, 4)
            game.spawn(demon, area)
        }
    }

}