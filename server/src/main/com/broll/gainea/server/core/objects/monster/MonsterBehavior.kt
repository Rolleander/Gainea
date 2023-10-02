package com.broll.gainea.server.core.objects.monster

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.utils.UnitControl.conquer
import com.broll.gainea.server.core.utils.UnitControl.move


private fun Monster.getPossibleTargets() = moveTargets
private fun Location.getEnemyStrength() = units.sumOf { it.battleStrength }

enum class MonsterBehavior(val label: String, private val action: (Game, Monster) -> kotlin.Unit) {
    RESIDENT("Sesshaft", { _, _ -> }),
    RANDOM("Wild", { game, monster ->
        monster.getPossibleTargets().randomOrNull()?.let {
            game.conquer(listOf(monster), it)
        }
    }),
    AGGRESSIVE("Aggressiv",
            { game, monster ->
                val target = monster.getPossibleTargets().filter {
                    !it.free && monster.battleStrength > it.getEnemyStrength()
                }.randomOrNull()
                if (target != null) {
                    game.conquer(listOf(monster), target)
                } else {
                    RANDOM.action(game, monster)
                }
            }),
    FLEEING("Scheu",
            { game, monster ->
                monster.getPossibleTargets().filter { it.free }.randomOrNull()?.let {
                    game.move(monster, it)
                }
            }),
    FRIENDLY("Freundlich", { game, monster ->
        monster.getPossibleTargets().randomOrNull()?.let {
            game.move(monster, it)
        }
    });

    fun doAction(game: Game, monster: Monster) = action(game, monster)


}
