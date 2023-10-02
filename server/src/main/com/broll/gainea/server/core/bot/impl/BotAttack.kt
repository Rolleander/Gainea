package com.broll.gainea.server.core.bot.impl

import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_Action_Attack
import com.broll.gainea.net.NT_Battle_Update
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.net.NT_Unit
import com.broll.gainea.server.core.bot.BotOptionalAction
import com.broll.gainea.server.core.bot.BotUtils
import com.broll.gainea.server.core.bot.strategy.BattleSimulation
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.utils.getMonsters

class BotAttack : BotOptionalAction<NT_Action_Attack, BotAttack.AttackOption>() {
    override fun score(action: NT_Action_Attack): AttackOption? {
        val location = BotUtils.getLocation(game, action.location.toInt())
        val usableUnits = usableUnits(action.units)
        val fightType = getFighType(location)
        val winChance = getRequiredWinChance(fightType)
        val attackFromOptions = usableUnits.map { it.location }.toTypedArray()
        for (attackFrom in attackFromOptions) {
            val groupedUnits = usableUnits.filter { it.location === attackFrom }
            if (groupedUnits.isEmpty()) {
                continue
            }
            val fighters = BattleSimulation.calculateRequiredFighters(location, groupedUnits, winChance)
            if (fighters != null) {
                return AttackOption(score = ((3 - fightType) * 5).toFloat(), fighters.map { it.id }.toIntArray(),
                        fightType, location)
            }
        }
        return null
    }

    override fun react(action: NT_Action_Attack, reaction: NT_Reaction) {
        reaction.options = selectedOption!!.attackUnits
    }

    override val actionClass: Class<out NT_Action>
        get() = NT_Action_Attack::class.java

    fun keepAttacking(update: NT_Battle_Update): Boolean {
        val attackers = BotUtils.getObjects(game, update.attackers)
        val defenders = BotUtils.getObjects(game, update.defenders)
        if (attackers.none { it.alive } || defenders.none { it.alive }) {
            return false
        }
        val winChance = getRequiredWinChance(selectedOption!!.type)
        return BattleSimulation.calculateCurrentWinChance(attackers, defenders) >= winChance
    }

    private fun usableUnits(ntUnits: Array<NT_Unit>): List<Unit> {
        val units = BotUtils.getObjects(game, ntUnits)
        return units.filter {
            if (strategy.moveTargets[it] === it.location) {
                return@filter false
            }
            val goalStrategy = strategy.getStrategy(it) ?: return@filter true
            goalStrategy.allowFighting(it)
        }
    }

    private fun getRequiredWinChance(fightType: Int): Float {
        when (fightType) {
            FIGHT_TARGET -> return strategy.constants.winchanceForTargetConquer
            FIGHT_PLAYER -> return strategy.constants.winchanceForPlayerFight
            FIGHT_WILD -> return strategy.constants.winchanceForWildFight
        }
        return 0F
    }

    private fun getFighType(location: Location): Int {
        if (isTargetLocation(location)) {
            return FIGHT_TARGET
        }
        return if (location.getMonsters().isNotEmpty()) {
            FIGHT_WILD
        } else FIGHT_PLAYER
    }

    private fun isTargetLocation(location: Location) = strategy.getGoalStrategies().flatMap { it.targetLocations }.any { it === location }

    class AttackOption(score: Float,
                       val attackUnits: IntArray,
                       val type: Int = 0,
                       val location: Location) : BotOption(score) {

        override fun toString(): String {
            return "attack " + location + " with " + attackUnits.size + " units"
        }
    }

    companion object {
        private const val FIGHT_TARGET = 0
        private const val FIGHT_PLAYER = 1
        private const val FIGHT_WILD = 2
    }
}
