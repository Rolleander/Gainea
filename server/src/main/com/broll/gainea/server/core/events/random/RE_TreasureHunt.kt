package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeArea
import com.broll.gainea.server.core.objects.Collectible
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_TreasureHunt : RandomEvent() {

    override fun pickSpot() = game.freeArea()
    override fun run() {
        game.spawn(TreasureMap(game), location)
    }

    private class TreasureMap(game: Game, step: Int = 0) : Collectible(game) {

        init {
            onPickup = {
                game.freeArea()?.let { area ->
                    val nextStep = step + 1
                    if (nextStep < 3) {
                        game.spawn(TreasureMap(game, nextStep), area)
                    } else {
                        game.spawn(Treasure(game), area)
                        game.spawn(TreasureDrake(game), area)
                    }
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
                it.goalHandler.addPoints(3)
            }
            name = "Verlorener Schatz"
            description = "Eroberer erhält 3 Siegespunkte"
            icon = 134
        }
    }

    private class TreasureDrake(game: Game) : Monster(game.neutralPlayer) {

        init {
            name = "Schatzdrache"
            icon = 138
            description = "Zahl +2 als Verteidiger"
            setStats(3, 7)
        }

        override fun calcFightingPower(context: BattleContext): FightingPower {
            val power = super.calcFightingPower(context)
            if (context.isDefending(this)) {
                power.changeNumberPlus(2)
            }
            return power
        }
    }

}