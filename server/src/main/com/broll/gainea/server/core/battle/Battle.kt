package com.broll.gainea.server.core.battle

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.battle.RollResult.Roll
import com.broll.gainea.server.core.objects.Unit
import java.util.Collections
import java.util.stream.Collectors

open class Battle(private val attackers: List<Unit?>?, private val defenders: List<Unit?>?, private val attackerRolls: RollResult, private val defenderRolls: RollResult) {
    constructor(context: BattleContext?, attackingUnits: List<Unit?>?, defendingUnits: List<Unit?>?) : this(attackingUnits, defendingUnits, RollResult(context, attackingUnits),
            RollResult(context, defendingUnits))

    fun fight(): FightResult {
        val attacks = attackerRolls.count()
        val blocks = defenderRolls.count()
        val battleSize = Math.min(attacks, blocks)
        val allAttackRolls = attackerRolls.rolls
        val allBlockRolls = defenderRolls.rolls
        val attackRolls = allAttackRolls!!.stream().limit(battleSize.toLong()).collect(Collectors.toList())
        val blockRolls = allBlockRolls!!.stream().limit(battleSize.toLong()).collect(Collectors.toList())
        val result = FightResult(allAttackRolls.stream().map { it: Roll? -> it!!.value }.collect(Collectors.toList()),
                allBlockRolls.stream().map { it: Roll? -> it!!.value }.collect(Collectors.toList()))
        for (i in 0 until battleSize) {
            val attack = attackRolls[i]
            val block = blockRolls[i]
            if (attack!!.value > block!!.value) {
                dealDamage(result, attack, attackers, defenders)
            } else {
                dealDamage(result, block, defenders, attackers)
            }
        }
        val deadAttackers = attackers!!.stream().filter { obj: Unit? -> obj!!.isDead }.collect(Collectors.toList())
        val deadDefenders = defenders!!.stream().filter { obj: Unit? -> obj!!.isDead }.collect(Collectors.toList())
        result.killed(deadAttackers, deadDefenders)
        return result
    }

    private fun dealDamage(result: FightResult, winningRoll: Roll?, sourceUnits: List<Unit?>?, targetUnits: List<Unit?>?) {
        var source = winningRoll!!.source
        if (source == null) {
            source = RandomUtils.pickRandom(sourceUnits)
        }
        val target = getDamageTarget(targetUnits)
        target?.let { damage(result, source, it) }
    }

    protected open fun damage(result: FightResult, source: Unit?, target: Unit) {
        val lethal = target.takeDamage()
        if (lethal) {
            source!!.addKill()
        }
        result.damage(source, target, lethal)
    }

    private fun getDamageTarget(targetUnits: List<Unit?>?): Unit? {
        val targets = targetUnits!!.stream().filter { obj: Unit? -> obj!!.isAlive }.collect(Collectors.toList())
        //shuffe for damage (so that same powerlevel units get hit randomly)
        Collections.shuffle(targets)
        //sort ascending (so that weakest power level units die first)
        return targets.stream().min(Comparator.comparingInt { o: Unit? -> o.getPower().value }).orElse(null)
    }
}
