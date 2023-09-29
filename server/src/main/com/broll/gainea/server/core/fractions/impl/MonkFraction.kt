package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.actions.ActionHandlers
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.UnitControl
import com.broll.gainea.server.core.utils.UnitControl.heal
import org.apache.commons.lang3.mutable.MutableBoolean

class MonkFraction : Fraction(FractionType.MONKS) {
    private var killedMonsters = 0
    override fun description(): FractionDescription {
        val desc = FractionDescription("")
        desc.plus("Jede Runde erhält eine Einheit +1 Leben")
        desc.plus("Werden nicht von Monstern angegriffen")
        desc.contra("Jedes dritte besiegte Monster gibt keine Belohnung")
        return desc
    }

    override fun battleIntention(context: BattleContext, cancelFight: MutableBoolean) {
        val neutralMonsters = context.attackers.filter { it.owner.isNeutral() && it is Monster }
        val defendingMonks = context.defenders.filterIsInstance<MonkSoldier>()
        if (neutralMonsters.isNotEmpty() && defendingMonks.isNotEmpty()) {
            cancelFight.setTrue()
            //move monsters without battle
            UnitControl.move(game, neutralMonsters, context.location)
        }
    }

    override fun killedMonster(monster: Monster) {
        killedMonsters++
        if (killedMonsters == 3) {
            killedMonsters = 0
            return
        }
        super.killedMonster(monster)
    }

    override fun turnStarted(actionHandlers: ActionHandlers) {
        val randomUnit = RandomUtils.pickRandom(owner.units)
        if (randomUnit != null) {
            //inc life
            randomUnit.maxHealth.addValue(1)
            //heal
            heal(game, randomUnit, 1)
        }
        super.turnStarted(actionHandlers)
    }

    override fun createSoldier(): Soldier {
        val soldier: Soldier = MonkSoldier(owner)
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH)
        soldier.name = "Mönch"
        soldier.icon = 108
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander: Soldier = MonkSoldier(owner)
        commander.isCommander = true
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH)
        commander.name = "Großmeister Eron"
        commander.icon = 107
        return commander
    }

    private class MonkSoldier(owner: Player) : Soldier(owner)
}
