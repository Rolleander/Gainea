package com.broll.gainea.server.core.cards

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.utils.getRandomFree

abstract class RandomEvent {

    abstract fun run(game: Game)

    protected fun Game.freeArea(body: (Area) -> Unit) =
            map.allAreas.getRandomFree()?.let { body(it as Area) }


    protected fun Game.freeContinentArea(body: (Area) -> Unit) =
            map.allContinents.flatMap { it.areas }.filter { it.free }.randomOrNull()?.let { body(it) }

}