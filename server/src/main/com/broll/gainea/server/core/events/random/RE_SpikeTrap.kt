package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeArea
import com.broll.gainea.server.core.objects.impl.SpikeTrap
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_SpikeTrap : RandomEvent() {
    override fun run(game: Game) {
        game.freeArea { area ->
            game.spawn(SpikeTrap(game, 5), area)
        }
    }

}