package com.broll.gainea.server.core.objects.monster

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.utils.UnitControl


private fun Monster.getPossibleTargets() = moveTargets
private fun Location.getEnemyStrength() = units.sumOf { it.battleStrength }

enum class MonsterBehavior(val label: String, private val action: (GameContainer, Monster) -> kotlin.Unit) {
    RESIDENT("Sesshaft", { _, _ -> }),
    RANDOM("Wild", { game, monster ->
        monster.getPossibleTargets().randomOrNull()?.let {
            UnitControl.conquer(game, listOf(monster), it)
        }
    }),
    AGGRESSIVE("Aggressiv",
            { game, monster ->
                val target = monster.getPossibleTargets().filter {
                    !it.free && monster.battleStrength > it.getEnemyStrength()
                }.randomOrNull()
                if (target != null) {
                    UnitControl.conquer(game, listOf(monster), target)
                } else {
                    RANDOM.action(game, monster)
                }
            }),
    FLEEING("Scheu",
            { game, monster ->
                monster.getPossibleTargets().filter { it.free }.randomOrNull()?.let {
                    UnitControl.move(game, monster, it)
                }
            }),
    FRIENDLY("Freundlich", { game, monster ->
        monster.getPossibleTargets().randomOrNull()?.let {
            UnitControl.move(game, monster, it)
        }
    });

    fun doAction(game: GameContainer, monster: Monster) = action(game, monster)


}
