package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.net.NT_Unit
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.fractions.UnitDescription
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.UnitControl.damage
import com.broll.gainea.server.core.utils.isAreaType

class RangerFraction : Fraction(FractionType.RANGER) {
    override fun description(): FractionDescription {
        val desc = FractionDescription(
            "",
            soldier = UnitDescription(name = "Waldläuferin", icon = 114),
            commander = UnitDescription(name = "Elfenschützin", icon = 115, power = 3, health = 3),
        )
        desc.plus("Gegen Monster +1 Zahl")
        desc.plus("Immer wenn ihr ein Monster besiegt, wird einer zufälligen feindlichen Einheit auf der gleichen Landmasse 1 Schaden zugefügt")
        desc.contra("Auf Schnee und Wüste -1 Zahl")
        return desc
    }

    override fun killedMonster(monster: Monster, battleResult: BattleResult) {
        super.killedMonster(monster, battleResult)
        battleResult.location.container.areas.flatMap { it.units }
            .filter { !it.owner.isNeutral() && it.owner != owner }.randomOrNull()?.let {
                game.damage(it)
            }
    }

    override fun calcFightingPower(soldier: Soldier, context: BattleContext): FightingPower {
        val power = super.calcFightingPower(soldier, context)
        if (context.getOpposingFightingArmy(soldier).any { it is Monster }) {
            power.changeNumberPlus(1)
        }
        if (context.location.isAreaType(AreaType.SNOW, AreaType.DESERT)) {
            power.changeNumberPlus(-1)
        }
        return power
    }

    override fun createSoldier(): Soldier {
        val soldier = Soldier(owner, fraction = this)
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH)
        soldier.name = "Waldläuferin"
        soldier.setType(NT_Unit.TYPE_FEMALE.toInt())
        soldier.icon = 114
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander = Soldier(owner, fraction = this)
        commander.commander = true
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH)
        commander.name = "Elfenschützin"
        commander.setType(NT_Unit.TYPE_FEMALE.toInt())
        commander.icon = 115
        return commander
    }
}
