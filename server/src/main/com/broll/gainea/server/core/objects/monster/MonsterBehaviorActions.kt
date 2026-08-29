package com.broll.gainea.server.core.objects.monster

import com.broll.gainea.core.objects.monster.MonsterBehavior

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.utils.UnitControl.conquer
import com.broll.gainea.server.core.utils.UnitControl.move

private fun Monster.possibleTargets() = moveTargets

fun MonsterBehavior.doAction(game: Game, monster: Monster) {
    when (this) {
        MonsterBehavior.RESIDENT -> Unit
        MonsterBehavior.RANDOM -> monster.possibleTargets().randomOrNull()?.let { game.conquer(listOf(monster), it) }
        MonsterBehavior.AGGRESSIVE -> {
            val target = monster.possibleTargets().filter { !it.free }.randomOrNull()
            if (target != null) game.conquer(listOf(monster), target)
            else MonsterBehavior.RANDOM.doAction(game, monster)
        }
        MonsterBehavior.FLEEING -> monster.possibleTargets().filter { it.free }.randomOrNull()?.let { game.move(monster, it) }
        MonsterBehavior.FRIENDLY -> monster.possibleTargets().randomOrNull()?.let { game.move(monster, it) }
    }
}
