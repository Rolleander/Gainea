package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.battle.UnitSnapshot
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.fractions.UnitDescription
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.isAreaType

class ShadowFraction : Fraction(FractionType.SHADOW) {
    override fun description(): FractionDescription {
        val desc = FractionDescription(
            "",
            soldier = UnitDescription(name = "Schatten", icon = 12),
            commander = UnitDescription(
                name = "Erznekromant Bal",
                icon = 21,
                power = 3,
                health = 3
            ),
        )
        desc.plus("Bei Kämpfen können gefallene Feinde zu Skeletten (1/1) werden")
        desc.contra("Erhält keine Belohnung für besiegte Monster auf Steppen")
        desc.contra("Skelette haben -1 Zahl")
        return desc
    }

    override fun killedMonster(monster: Monster, battleResult: BattleResult) {
        if (monster.location.isAreaType(AreaType.PLAINS)) {
            return
        }
        super.killedMonster(monster, battleResult)
    }

    override fun createSoldier(): Soldier {
        val soldier = Soldier(owner, fraction = this)
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH)
        soldier.name = "Schatten"
        soldier.icon = 12
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander = Soldier(owner, fraction = this)
        commander.commander = true
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH)
        commander.name = "Erznekromant Bal"
        commander.icon = 21
        return commander
    }

    private fun summonSkeletons(killedEnemies: List<UnitSnapshot>, location: Location) {
        killedEnemies.forEach { _ ->
            if (RandomUtils.randomBoolean(SUMMON_CHANCE)) {
                summon(location)
            }
        }
    }

    private fun summon(location: Location) {
        val skeleton = Soldier(owner, fraction = this@ShadowFraction)
        skeleton.numberPlus.value = -1
        skeleton.setStats(SOLDIER_POWER, SOLDIER_HEALTH)
        skeleton.icon = 94
        skeleton.name = "Skelett"
        game.spawn(skeleton, location)
    }

    override fun battleResult(result: BattleResult) {
        if (result.isParticipating(owner)) {
            val spawnLocation = result.getEndLocation(owner)!!
            val killedEnemies = result.getOpposingUnits(owner).filter { it.dead }
            summonSkeletons(killedEnemies, spawnLocation)
        }
    }

    companion object {
        private const val SUMMON_CHANCE = 0.7f
    }
}
