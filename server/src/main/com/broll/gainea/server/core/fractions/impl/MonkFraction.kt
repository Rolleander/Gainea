package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.fractions.UnitDescription
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.UnitControl.heal
import com.broll.gainea.server.core.utils.UnitControl.move
import org.apache.commons.lang3.mutable.MutableBoolean

class MonkFraction : Fraction(FractionType.MONKS) {
    private var killedMonsters = 0

    override val description: FractionDescription = FractionDescription(
        "",
        soldier = UnitDescription(name = "Mönch", icon = 108),
        commander = UnitDescription(
            name = "Großmeister Eron",
            icon = 107,
            power = 3,
            health = 3
        ),
    ).apply {
        plus("Jede Runde erhält eine Einheit +1 Leben")
        plus("Werden nicht von Monstern angegriffen")
        contra("Jedes dritte besiegte Monster gibt keine Belohnung")
    }

    override fun battleIntention(context: BattleContext, cancelFight: MutableBoolean) {
        val neutralMonsters = context.attackers.filter { it.owner.isNeutral() && it is Monster }
        val defendingMonks = context.defenders.filterIsInstance<MonkSoldier>()
        if (neutralMonsters.isNotEmpty() && defendingMonks.isNotEmpty()) {
            cancelFight.setTrue()
            //move monsters without battle
            game.move(neutralMonsters, context.location)
        }
    }

    override fun killedMonster(monster: Monster, battleResult: BattleResult) {
        killedMonsters++
        if (killedMonsters == 3) {
            killedMonsters = 0
            return
        }
        super.killedMonster(monster, battleResult)
    }

    override fun prepareTurn() {
        super.prepareTurn()
        val randomUnit = RandomUtils.pickRandom(owner.units)
        if (randomUnit != null) {
            //inc life
            randomUnit.maxHealth.addValue(1)
            //heal
            game.heal(randomUnit, 1)
        }
    }

    override fun createCommander(): Soldier {
        val commander = MonkSoldier(owner)
        commander.commander = true
        commander.initFrom(description.commander)
        return commander
    }

    override fun createSoldier(): Soldier {
        val soldier = MonkSoldier(owner)
        soldier.initFrom(description.soldier)
        return soldier
    }

    private inner class MonkSoldier(owner: Player) : Soldier(owner, fraction = this@MonkFraction)
}
