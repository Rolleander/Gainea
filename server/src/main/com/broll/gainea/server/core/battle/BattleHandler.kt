package com.broll.gainea.server.core.battle

import com.broll.gainea.net.NT_Battle_Intention
import com.broll.gainea.net.NT_Battle_Reaction
import com.broll.gainea.net.NT_Battle_Start
import com.broll.gainea.net.NT_Battle_Update
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.actions.ReactionActions
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.UnitControl.move
import com.broll.gainea.server.core.utils.remove
import com.broll.gainea.server.core.utils.sendUpdate
import org.apache.commons.lang3.mutable.MutableBoolean
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

class BattleHandler(private val game: Game, private val reactionResult: ReactionActions) {
    var isBattleActive = false
        private set
    private var keepAttacking: CompletableFuture<Boolean>? = null
    private lateinit var rollManipulator: RollManipulator
    private lateinit var context: BattleContext
    private var allowRetreat = true
    fun reset() {
        isBattleActive = false
    }

    fun startBattle(attackers: List<Unit>, defenders: List<Unit>, allowRetreat: Boolean = true) {
        if (!isBattleActive) {
            this.allowRetreat = allowRetreat
            context = BattleContext(attackers.toList(), defenders.toList())
            rollManipulator = RollManipulator()
            if (context.hasSurvivingAttackers() && context.hasSurvivingDefenders()) {
                prepareFight()
            } else {
                Log.warn("Could not start battle because no alive attackers or defenders")
            }
        } else {
            Log.warn("Could not start battle because a battle is still going on")
        }
    }

    private fun prepareFight() {
        Log.trace("Prepare fight")
        val cancelFight = MutableBoolean(false)
        game.updateReceiver.battleIntention(context, cancelFight)
        if (cancelFight.isTrue) {
            Log.info("Battle cancelled from receiver")
            return
        }
        isBattleActive = true
        //attackers are always of one owner
        if (context.isNeutralAttacker) {
            //wild monsters are attackers, will always keep attacking
            allowRetreat = false
        }
        sendFightIntention()
        sendFightStart()
        ProcessingUtils.pause(BATTLE_ANIMATION_DELAY)
        fightRound()
    }

    private fun sendFightIntention() {
        val intention = NT_Battle_Intention()
        intention.fromLocation = context.sourceLocation.number
        intention.toLocation = context.location.number
        reactionResult.sendGameUpdate(intention)
        ProcessingUtils.pause(BATTLE_ANIMATION_DELAY)
    }

    private fun sendFightStart() {
        Log.trace("Start fight")
        game.updateReceiver.battleBegin(context, rollManipulator)
        val start = NT_Battle_Start()
        start.attackers = context.attackers.sortedBy { it.id }.map { it.nt() }.toTypedArray()
        start.defenders = context.defenders.sortedBy { it.id }.map { it.nt() }.toTypedArray()
        start.allowRetreat = allowRetreat
        start.location = context.location.number
        if (!context.isNeutralAttacker) {
            start.attacker = context.attackingPlayer.serverPlayer.id
        }
        reactionResult.sendGameUpdate(start)
    }

    fun playerReaction(player: Player, battle_reaction: NT_Battle_Reaction) {
        if (context.isAttacker(player) && keepAttacking != null) {
            Log.trace("Handle battle reaction")
            keepAttacking!!.complete(battle_reaction.keepAttacking)
        } else {
            Log.warn("Battle reaction ignored because player is not attacker")
        }
    }

    private fun rollFight(): FightResult {
        val attackerRolls = RollResult(context, context.aliveAttackers)
        val defenderRolls = RollResult(context, context.aliveDefenders)
        rollManipulator.roundStarts(attackerRolls, defenderRolls)
        return Battle(context.aliveAttackers, context.aliveDefenders, attackerRolls, defenderRolls).fight()
    }

    private fun List<Unit>.toInfoString() = map { "${it.id} | ${it.name} ${it.power.value} ${it.health.value}" }.joinToString(", ")

    private fun logContext(prefix: String) {
        Log.info(prefix + " Attackers: (" + context.aliveAttackers.toInfoString() + ")"
                + ")   Defenders: (" + context.aliveDefenders.toInfoString() + ")")
    }

    private fun fightRound() {
        logContext("Fight round begin:")
        val result = rollFight()
        val update = NT_Battle_Update()
        update.attackerRolls = result.attackRolls.toIntArray()
        update.defenderRolls = result.defenderRolls.toIntArray()
        update.attackers = context.aliveAttackers.sortedBy { it.id }.map { it.nt() }.toTypedArray()
        update.defenders = context.aliveDefenders.sortedBy { it.id }.map { it.nt() }.toTypedArray()
        logContext("Fight round result:")
        var state = NT_Battle_Update.STATE_FIGHTING
        val attackersDead = context.aliveAttackers.isEmpty()
        val defendersDead = context.aliveDefenders.isEmpty()
        if (attackersDead && defendersDead) {
            state = NT_Battle_Update.STATE_DRAW
        } else if (attackersDead) {
            state = NT_Battle_Update.STATE_DEFENDER_WON
        } else if (defendersDead) {
            state = NT_Battle_Update.STATE_ATTACKER_WON
        }
        update.state = state
        update.damage = result.getDamage().map { it.nt() }.toTypedArray()
        //send update
        reactionResult.sendGameUpdate(update)
        val delay = getAnimationDelay(result.attackRolls.size, result.defenderRolls.size)
        ProcessingUtils.pause(delay)
        if (state == NT_Battle_Update.STATE_FIGHTING) {
            //wait for player if he wants to keep attacking
            if (allowRetreat) {
                keepAttacking = CompletableFuture()
            }
            prepareNextRound()
        } else {
            //battle finished, all attackers or defenders died
            ProcessingUtils.pause(BATTLE_ANIMATION_DELAY)
            battleFinished(false)
        }
    }

    private fun prepareNextRound() {
        //wait for player if he wants to keep attacking
        var startNextRound = true
        if (allowRetreat) {
            //disconnect check
            if (!context.attackingPlayer.isNeutral() && !context.attackingPlayer.serverPlayer.isOnline) {
                //retreat cause offline
                Log.info("Retreat from battle because attacking player is offline")
                battleFinished(true)
            }

            try {
                Log.trace("Wait for battle reaction")
                startNextRound = keepAttacking!!.get()
            } catch (e: InterruptedException) {
                Log.error("Failed getting future", e)
            } catch (e: ExecutionException) {
                Log.error("Failed getting future", e)
            }
        }
        if (startNextRound) {
            //schedule next fight round
            Log.trace("Start next battle round")
            fightRound()
        } else {
            //end battle, attackers retreat
            battleFinished(true)
        }
    }

    private fun battleFinished(retreated: Boolean) {
        val result = BattleResult(retreated, context)
        Log.info("Battle over! Surviving Attackers: (" + result.aliveAttackers.toInfoString() + ")" +
                " Killed Attackers: (" + result.killedAttackers.toInfoString() + ")" +
                " Surviving Defenders: (" + result.aliveDefenders.toInfoString() + ")" +
                " Killed Defenders: (" + result.killedDefenders.toInfoString() + ")")
        isBattleActive = false
        val updateReceiver = game.updateReceiver
        val fallenUnits = mutableListOf<Unit>()
        fallenUnits.addAll(result.killedAttackers)
        fallenUnits.addAll(result.killedDefenders)
        fallenUnits.forEach { game.remove(it) }
        fallenUnits.forEach { it.onDeath(result) }
        fallenUnits.forEach { updateReceiver.killed(it, result) }
        updateReceiver.battleResult(result)
        //if defenders lost, move surviving attackers to location
        if (result.attackersWon) {
            game.move(result.aliveAttackers, result.location)
        }
        game.sendUpdate(game.nt())
        //find dead monsters to give killing player rewards
        rewardKilledMonsters(result.attackingPlayer, result.killedDefenders)
        result.getNonNeutralDefenders().forEach { rewardKilledMonsters(it, result.killedAttackers) }
    }

    private fun rewardKilledMonsters(killer: Player, units: List<Unit>) {
        if (killer.isNeutral()) {
            return
        }
        units.filterIsInstance(Monster::class.java).filter { it.owner.isNeutral() }.forEach {
            killer.goalHandler.addStars(it.stars)
            killer.fraction.killedMonster(it)
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(BattleHandler::class.java)
        const val BATTLE_ANIMATION_DELAY = 2000
        fun getAnimationDelay(atkRolls: Int, defRolls: Int): Int {
            return BATTLE_ANIMATION_DELAY / 2 + 800 * Math.min(atkRolls, defRolls)
        }
    }
}
