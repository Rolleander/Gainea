package com.broll.gainea.server.core.battle

import com.broll.gainea.net.NT_Battle_Intention
import com.broll.gainea.net.NT_Battle_Reaction
import com.broll.gainea.net.NT_Battle_Start
import com.broll.gainea.net.NT_Battle_Update
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.actions.ReactionActions
import com.broll.gainea.server.core.objects.IUnit
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.UnitControl.despawn
import com.broll.gainea.server.core.utils.UnitControl.move
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
    private var grantRewards = true
    fun reset() {
        isBattleActive = false
    }

    fun startBattle(
        attackers: List<Unit>,
        defenders: List<Unit>,
        allowRetreat: Boolean = true,
        grantRewards: Boolean = true
    ) {
        if (!isBattleActive) {
            this.allowRetreat = allowRetreat
            this.grantRewards = grantRewards
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
        ProcessingUtils.pause(BATTLE_INTENT_DELAY)
    }

    private fun sendFightStart() {
        Log.trace("Start fight")
        game.updateReceiver.battleBegin(context, rollManipulator)
        val start = NT_Battle_Start()
        start.attackers = context.attackers.sortedBy { it.id }.map { it.nt() }.toTypedArray()
        start.defenders = context.defenders.sortedBy { it.id }.map { it.nt() }.toTypedArray()
        start.allowRetreat = allowRetreat
        start.location = context.location.number
        context.getControllingAttacker()?.let {
            start.attacker = it.serverPlayer.id
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
        rollManipulator.roundStarts(context, attackerRolls, defenderRolls)
        return Battle(
            context = context,
            attackerRolls = attackerRolls,
            defenderRolls = defenderRolls
        ).fight()
    }

    private fun List<IUnit>.toInfoString() =
        map { "${it.id} | ${it.name} ${it.power.value} ${it.health.value}" }.joinToString(", ")

    private fun logContext(prefix: String) {
        Log.info(
            prefix + " Attackers: (" + context.aliveAttackers.toInfoString() + ")"
                    + ")   Defenders: (" + context.aliveDefenders.toInfoString() + ")"
        )
    }

    private fun fightRound() {
        logContext("Fight round begin:")
        val result = rollFight()
        val update = NT_Battle_Update()
        update.attackerRolls = result.attackRolls.map { it.nt() }.toTypedArray()
        update.defenderRolls = result.defenderRolls.map { it.nt() }.toTypedArray()
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
        context.rounds += BattleRound(result.getDamage())
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
            val controllingAttacker = context.getControllingAttacker()
            if (controllingAttacker != null && !controllingAttacker.serverPlayer.isOnline) {
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
        Log.info(
            "Battle over! Surviving Attackers: (" + result.aliveAttackers.toInfoString() + ")" +
                    " Killed Attackers: (" + result.killedAttackers.toInfoString() + ")" +
                    " Surviving Defenders: (" + result.aliveDefenders.toInfoString() + ")" +
                    " Killed Defenders: (" + result.killedDefenders.toInfoString() + ")"
        )
        isBattleActive = false
        val updateReceiver = game.updateReceiver
        val fallenUnits = mutableListOf<Unit>()
        fallenUnits.addAll(result.killedAttackers.map { it.source })
        fallenUnits.addAll(result.killedDefenders.map { it.source })
        game.despawn(fallenUnits)
        fallenUnits.forEach { it.onDeath(result) }
        fallenUnits.forEach { updateReceiver.unitKilled(it, result) }
        updateReceiver.battleResult(result)
        game.sendUpdate(game.nt())
        //if defenders lost, move surviving attackers to location
        if (result.attackersWon) {
            game.move(result.aliveAttackers.map { it.source }, result.location)
        }
        //find dead monsters to give killing player rewards
        if (grantRewards) {
            result.getNonNeutralAttackers()
                .forEach { rewardKilledMonsters(it, result.killedDefenders) }
            result.getNonNeutralDefenders()
                .forEach { rewardKilledMonsters(it, result.killedAttackers) }
        }
    }

    private fun rewardKilledMonsters(killer: Player, units: List<UnitSnapshot>) {
        if (killer.isNeutral()) {
            return
        }
        units.map { it.source }.filterIsInstance(Monster::class.java)
            .forEach {
                killer.goalHandler.addStars(it.stars)
                if (it.owner.isNeutral()) {
                    killer.fraction.killedNeutralMonster(it)
                }
            }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(BattleHandler::class.java)
        const val BATTLE_ANIMATION_DELAY = 1600
        const val BATTLE_INTENT_DELAY = 1300
        fun getAnimationDelay(atkRolls: Int, defRolls: Int): Int {
            return BATTLE_ANIMATION_DELAY / 2 + 600 * Math.min(atkRolls, defRolls)
        }
    }
}
