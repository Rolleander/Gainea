package com.broll.gainea.server.core.battle

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.battle.RollResult.Roll
import com.broll.gainea.server.core.objects.Unit

open class Battle(
    private val context: BattleContext,
    private val attackerRolls: RollResult,
    private val defenderRolls: RollResult,
    private val attackers: List<Unit> = context.aliveAttackers,
    private val defenders: List<Unit> = context.aliveDefenders,
) {
    constructor(context: BattleContext) :
            this(
                context = context,
                attackerRolls = RollResult(context, context.aliveAttackers),
                defenderRolls = RollResult(context, context.aliveDefenders)
            )

    fun fight(): FightResult {
        val attacks = attackerRolls.count()
        val blocks = defenderRolls.count()
        val battleSize = Math.min(attacks, blocks)
        val allAttackRolls = attackerRolls.rolls
        val allBlockRolls = defenderRolls.rolls
        val attackRolls = allAttackRolls.take(battleSize)
        val blockRolls = allBlockRolls.take(battleSize)
        val result = FightResult(allAttackRolls, allBlockRolls)
        for (i in 0 until battleSize) {
            val attack = attackRolls[i]
            val block = blockRolls[i]
            if (attack.value > block.value) {
                dealDamage(result, attack, attackers, defenders)
            } else {
                dealDamage(result, block, defenders, attackers)
            }
        }
        val deadAttackers = attackers.filter { it.dead }
        val deadDefenders = defenders.filter { it.dead }
        result.killed(deadAttackers, deadDefenders)
        return result
    }

    private fun dealDamage(
        result: FightResult,
        winningRoll: Roll,
        sourceUnits: List<Unit>,
        targetUnits: List<Unit>
    ) {
        val target = getDamageTarget(targetUnits)
        target?.let {
            damage(result, winningRoll.source ?: RandomUtils.pickRandom(sourceUnits), it)
        }
    }

    protected open fun damage(result: FightResult, source: Unit, target: Unit) {
        val lethal = target.takeDamage()
        if (lethal) {
            source.killedEnemy(target)
        }
        result.damage(source, target, lethal)
    }

    private fun getDamageTarget(targetUnits: List<Unit>) =
        targetUnits.filter { it.alive }.shuffled().minByOrNull { it.power.value }

}
