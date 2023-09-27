package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.actions.ActionHandlers
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.UnitControl
import org.apache.commons.lang3.mutable.MutableBoolean
import java.util.stream.Collectors

class MonkFraction : Fraction(FractionType.MONKS) {
    private var killedMonsters = 0
    override fun description(): FractionDescription {
        val desc = FractionDescription("")
        desc.plus("Jede Runde erhält eine Einheit +1 Leben")
        desc.plus("Werden nicht von Monstern angegriffen")
        desc.contra("Jedes dritte besiegte Monster gibt keine Belohnung")
        return desc
    }

    override fun battleIntention(context: BattleContext?, cancelFight: MutableBoolean) {
        val neutralMonsters = context.getAttackers().stream().filter { it: Unit? -> it.getOwner() == null && it is Monster }.collect(Collectors.toList())
        val defendingMonks = context.getDefenders().stream().filter { it: Unit? -> it is MonkSoldier }.collect(Collectors.toList())
        if (!neutralMonsters.isEmpty() && !defendingMonks.isEmpty()) {
            cancelFight.setTrue()
            //move monsters anyway
            UnitControl.move(game!!, neutralMonsters, context!!.location)
        }
    }

    override fun killedMonster(monster: Monster?) {
        killedMonsters++
        if (killedMonsters == 3) {
            killedMonsters = 0
            return
        }
        super.killedMonster(monster)
    }

    override fun turnStarted(actionHandlers: ActionHandlers?) {
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
        soldier.setStats(Fraction.Companion.SOLDIER_POWER, Fraction.Companion.SOLDIER_HEALTH)
        soldier.name = "Mönch"
        soldier.icon = 108
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander: Soldier = MonkSoldier(owner)
        commander.isCommander = true
        commander.setStats(Fraction.Companion.COMMANDER_POWER, Fraction.Companion.COMMANDER_HEALTH)
        commander.name = "Großmeister Eron"
        commander.icon = 107
        return commander
    }

    private class MonkSoldier(owner: Player?) : Soldier(owner)
}
