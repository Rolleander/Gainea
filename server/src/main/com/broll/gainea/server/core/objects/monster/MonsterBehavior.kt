package com.broll.gainea.server.core.objects.monster

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.UnitControl
import com.google.common.collect.Lists
import java.util.function.BiConsumer
import java.util.stream.Collectors
import java.util.stream.Stream

enum class MonsterBehavior(@JvmField val label: String, private val action: BiConsumer<GameContainer, Monster>) {
    RESIDENT("Sesshaft", BiConsumer { game: GameContainer?, monster: Monster? -> }),
    RANDOM("Wild", BiConsumer { game: GameContainer, monster: Monster ->
        val target = RandomUtils.pickRandom(getPossibleTargets(monster).collect(Collectors.toList()))
        if (target != null) {
            UnitControl.conquer(game, Lists.newArrayList<Unit?>(monster), target)
        }
    }),
    AGGRESSIVE("Aggressiv", BiConsumer { game: GameContainer, monster: Monster ->
        val target = RandomUtils.pickRandom(getPossibleTargets(monster).filter { it: Location ->
            !it.isFree &&
                    monster.battleStrength > LocationUtils.getUnits(it).stream().map { obj: Unit? -> obj.getBattleStrength() }.reduce(0) { a: Int, b: Int -> Integer.sum(a, b) }
        }.collect(Collectors.toList()))
        if (target != null) {
            UnitControl.conquer(game, Lists.newArrayList<Unit?>(monster), target)
        } else {
            RANDOM.doAction(game, monster)
        }
    }),
    FLEEING("Scheu", BiConsumer { game: GameContainer, monster: Monster ->
        val target = RandomUtils.pickRandom(getPossibleTargets(monster).filter { obj: Location -> obj.isFree }.collect(Collectors.toList()))
        if (target != null) {
            UnitControl.move(game, monster, target)
        }
    }),
    FRIENDLY("Freundlich", BiConsumer { game: GameContainer, monster: Monster ->
        val target = RandomUtils.pickRandom(getPossibleTargets(monster).collect(Collectors.toList()))
        if (target != null) {
            UnitControl.move(game, monster, target)
        }
    });

    fun doAction(game: GameContainer, monster: Monster) {
        action.accept(game, monster)
    }

    companion object {
        private fun getPossibleTargets(monster: Monster): Stream<Location> {
            return monster.moveTargets.stream()
        }
    }
}
