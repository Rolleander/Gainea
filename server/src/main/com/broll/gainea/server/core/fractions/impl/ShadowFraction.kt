package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.LocationUtils
import java.util.function.Consumer
import java.util.stream.Collectors

class ShadowFraction : Fraction(FractionType.SHADOW) {
    override fun description(): FractionDescription {
        val desc = FractionDescription("")
        desc.plus("Bei Kämpfen können gefallene Feinde zu Skeletten (1/1) werden")
        desc.contra("Erhält keine Belohnung für besiegte Monster auf Steppen")
        desc.contra("Skelette haben -1 Zahl")
        return desc
    }

    override fun killedMonster(monster: Monster?) {
        if (LocationUtils.isAreaType(monster.getLocation(), AreaType.PLAINS)) {
            return
        }
        super.killedMonster(monster)
    }

    override fun createSoldier(): Soldier {
        val soldier = Soldier(owner)
        soldier.setStats(Fraction.Companion.SOLDIER_POWER, Fraction.Companion.SOLDIER_HEALTH)
        soldier.name = "Schatten"
        soldier.icon = 12
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander = Soldier(owner)
        commander.isCommander = true
        commander.setStats(Fraction.Companion.COMMANDER_POWER, Fraction.Companion.COMMANDER_HEALTH)
        commander.name = "Erznekromant Bal"
        commander.icon = 21
        return commander
    }

    private fun summonSkeletons(killedEnemies: List<Unit>, location: Location?) {
        killedEnemies.forEach(Consumer { it: Unit? ->
            if (RandomUtils.randomBoolean(SUMMON_CHANCE)) {
                summon(location)
            }
        })
    }

    private fun summon(location: Location?) {
        val skeleton: Soldier = object : Soldier(owner) {
            override fun calcFightingPower(context: BattleContext?): FightingPower {
                return super.calcFightingPower(context)!!.changeNumberPlus(-1)
            }
        }
        skeleton.setStats(Fraction.Companion.SOLDIER_POWER, Fraction.Companion.SOLDIER_HEALTH)
        skeleton.icon = 94
        skeleton.name = "Skelett"
        spawn(game, skeleton, location)
    }

    override fun battleResult(result: BattleResult) {
        if (result.isParticipating(owner)) {
            val spawnLocation = result.getEndLocation(owner)
            val killedEnemies = result.getOpposingUnits(owner)!!.stream().filter { obj: Unit -> obj.isDead }.collect(Collectors.toList())
            summonSkeletons(killedEnemies, spawnLocation)
        }
    }

    companion object {
        private const val SUMMON_CHANCE = 0.4f
    }
}
