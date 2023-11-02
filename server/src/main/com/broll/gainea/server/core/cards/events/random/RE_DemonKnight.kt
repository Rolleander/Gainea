package com.broll.gainea.server.core.cards.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.RandomEvent
import com.broll.gainea.server.core.objects.impl.DemonKnight
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.getWeakestPlayer

class RE_DemonKnight : RandomEvent() {
    override fun run(game: Game) {
        game.freeArea { area ->
            val demon = DemonKnight(game.getWeakestPlayer())
            game.spawn(demon, area)
        }
    }

}

