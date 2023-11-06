package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeArea
import com.broll.gainea.server.core.objects.impl.DemonKnight
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.getWeakestPlayer

class RE_DemonKnight : RandomEvent() {

    override fun pickSpot() = game.freeArea()
    override fun run() {
        val demon = DemonKnight(game.getWeakestPlayer())
        game.spawn(demon, location)
    }


}

