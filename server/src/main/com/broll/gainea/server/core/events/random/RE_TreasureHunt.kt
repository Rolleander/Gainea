package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeArea
import com.broll.gainea.server.core.objects.Collectible
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_TreasureHunt : RandomEvent() {
    override fun run(game: Game) {
        game.freeArea { area ->
            game.spawn(TreasureMap(game), area)
        }
    }

    private class TreasureMap(game: Game, step: Int = 0) : Collectible(game) {

        init {
            onPickup = {
                game.freeArea { area ->
                    val nextStep = step + 1
                    val nextSpawn =
                        if (nextStep < 3) TreasureMap(game, nextStep) else Treasure(game)
                    game.spawn(nextSpawn, area)
                }
            }
            name = "Verlorene Schatzkarte"
            description =
                if (step == 2) "Eroberer deckt den verlorenen Schatz auf" else "Eroberer deckt die nächste Schatzkarte auf"
            icon = 135
        }

    }

    private class Treasure(game: Game) : Collectible(game) {

        init {
            onPickup = {
                it.goalHandler.addPoints(2)
            }
            name = "Verlorener Schatz"
            description = "Eroberer erhält 2 Siegespunkte"
            icon = 134
        }
    }

}