package com.broll.gainea.server.core.bot.impl

import com.broll.gainea.net.NT_Actionimport

com.broll.gainea.net.NT_Action_Attackimport com.broll.gainea.net.NT_Battle_Updateimport com.broll.gainea.net.NT_Reactionimport com.broll.gainea.net.NT_Unitimport com.broll.gainea.server.core.bot.BotOptionalActionimport com.broll.gainea.server.core.bot.BotUtilsimport com.broll.gainea.server.core.bot.impl .BotAttack.AttackOptionimport com.broll.gainea.server.core.bot.strategy.BattleSimulationimport com.broll.gainea.server.core.bot.strategy.GoalStrategyimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.utils.LocationUtilsimport org.apache.commons.lang3.ArrayUtilsimport java.util.Arraysimport java.util.stream.Collectors
class BotAttack : BotOptionalAction<NT_Action_Attack, AttackOption?>() {
    override fun score(action: NT_Action_Attack): AttackOption? {
        val location = BotUtils.getLocation(game!!, action.location.toInt())
        val usableUnits = usableUnits(action.units)
        val fightType = getFighType(location)
        val winChance = getRequiredWinChance(fightType)
        val unitIds = Arrays.stream(action.units).mapToInt { it: NT_Unit -> it.id.toInt() }.toArray()
        val attackFromOptions = usableUnits.stream().map { obj: Unit? -> obj.getLocation() }.distinct().collect(Collectors.toList())
        for (attackFrom in attackFromOptions) {
            val groupedUnits = usableUnits.stream().filter { it: Unit? -> it.getLocation() === attackFrom }.collect(Collectors.toList())
            if (groupedUnits.isEmpty()) {
                continue
            }
            val fighters = BattleSimulation.calculateRequiredFighters(location, groupedUnits, winChance)
            if (fighters != null) {
                val option = AttackOption(((3 - fightType) * 5).toFloat())
                option.type = fightType
                option.location = location
                option.attackUnits = fighters.stream().mapToInt { it: Unit? -> ArrayUtils.indexOf(unitIds, it.getId()) }.toArray()
                return option
            }
        }
        return null
    }

    override fun react(action: NT_Action_Attack, reaction: NT_Reaction) {
        reaction.options = selectedOption.attackUnits
    }

    override val actionClass: Class<out NT_Action?>?
        get() = NT_Action_Attack::class.java

    fun keepAttacking(update: NT_Battle_Update): Boolean {
        val attackers = BotUtils.getObjects(game!!, update.attackers)
        val defenders = BotUtils.getObjects(game!!, update.defenders)
        if (attackers!!.stream().noneMatch { obj: Unit? -> obj!!.isAlive } || defenders!!.stream().noneMatch { obj: Unit? -> obj!!.isAlive }) {
            return false
        }
        val winChance = getRequiredWinChance(selectedOption.type)
        return BattleSimulation.calculateCurrentWinChance(attackers, defenders) >= winChance
    }

    private fun usableUnits(ntUnits: Array<NT_Unit>): List<Unit?> {
        val units = BotUtils.getObjects(game!!, ntUnits)
        return units!!.stream().filter { it: Unit? ->
            if (strategy.moveTargets[it] === it.getLocation()) {
                return@filter false
            }
            val goalStrategy = strategy!!.getStrategy(it) ?: return@filter true
            goalStrategy.allowFighting(it)
        }.collect(Collectors.toList())
    }

    private fun getRequiredWinChance(fightType: Int): Float {
        when (fightType) {
            FIGHT_TARGET -> return strategy.constants.winchanceForTargetConquer
            FIGHT_PLAYER -> return strategy.constants.winchanceForPlayerFight
            FIGHT_WILD -> return strategy.constants.winchanceForWildFight
        }
        return 0
    }

    private fun getFighType(location: Location?): Int {
        if (isTargetLocation(location)) {
            return FIGHT_TARGET
        }
        return if (!LocationUtils.getMonsters(location).isEmpty()) {
            FIGHT_WILD
        } else FIGHT_PLAYER
    }

    private fun isTargetLocation(location: Location?): Boolean {
        return strategy!!.goalStrategies.stream().flatMap { it: GoalStrategy? -> it.getTargetLocations().stream() }.anyMatch { it: Location? -> it === location }
    }

    class AttackOption(score: Float) : BotOption(score) {
        val attackUnits: IntArray
        val type = 0
        val location: Location? = null
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
