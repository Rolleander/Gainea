package com.broll.gainea.server.core.battle

import com.broll.gainea.net.NT_Battle_Damage
import com.broll.gainea.net.NT_Battle_Intention
import com.broll.gainea.net.NT_Battle_Reaction
import com.broll.gainea.net.NT_Battle_Start
import com.broll.gainea.net.NT_Battle_Update
import com.broll.gainea.net.NT_Unit
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.actions.ReactionActions
import com.broll.gainea.server.core.battle.FightResult.AttackDamage
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.GameUtils
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.UnitControl
import org.apache.commons.lang3.mutable.MutableBoolean
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors

class BattleHandler(private val game: GameContainer, private val reactionResult: ReactionActions) {
    var isBattleActive = false
        private set
    private var keepAttacking: CompletableFuture<Boolean>? = null
    private var rollManipulator: RollManipulator? = null
    private var context: BattleContext? = null
    private var allowRetreat = true
    fun reset() {
        isBattleActive = false
    }

    @JvmOverloads
    fun startBattle(attackers: List<Unit?>?, defenders: List<Unit?>?, allowRetreat: Boolean = true) {
        if (!isBattleActive) {
            this.allowRetreat = allowRetreat
            context = BattleContext(ArrayList(attackers), ArrayList(defenders))
            rollManipulator = RollManipulator()
            if (context!!.hasSurvivingAttackers() && context!!.hasSurvivingDefenders()) {
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
        if (context!!.isNeutralAttacker) {
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
        intention.fromLocation = context.getSourceLocation().number
        intention.toLocation = context!!.getLocation().number
        reactionResult.sendGameUpdate(intention)
        ProcessingUtils.pause(BATTLE_ANIMATION_DELAY)
    }

    private fun sendFightStart() {
        Log.trace("Start fight")
        game.updateReceiver.battleBegin(context, rollManipulator)
        val start = NT_Battle_Start()
        start.attackers = context.getAttackers().stream().sorted(sortById()).map<NT_Unit?> { obj: Unit? -> obj!!.nt() }.toArray<NT_Unit> { _Dummy_.__Array__() }
        start.defenders = context.getDefenders().stream().sorted(sortById()).map<NT_Unit?> { obj: Unit? -> obj!!.nt() }.toArray<NT_Unit> { _Dummy_.__Array__() }
        start.allowRetreat = allowRetreat
        start.location = context!!.getLocation().number
        if (!context!!.isNeutralAttacker) {
            start.attacker = context.getAttackingPlayer().serverPlayer.id
        }
        reactionResult.sendGameUpdate(start)
    }

    private fun sortById(): Comparator<Unit?> {
        return Comparator.comparingInt { obj: Unit? -> obj.getId() }
    }

    fun playerReaction(player: Player?, battle_reaction: NT_Battle_Reaction) {
        if (context!!.isAttacker(player) && keepAttacking != null) {
            Log.trace("Handle battle reaction")
            keepAttacking!!.complete(battle_reaction.keepAttacking)
        } else {
            Log.warn("Battle reaction ignored because player is not attacker")
        }
    }

    private fun rollFight(): FightResult? {
        val attackerRolls = RollResult(context, context.getAliveAttackers())
        val defenderRolls = RollResult(context, context.getAliveDefenders())
        rollManipulator!!.roundStarts(attackerRolls, defenderRolls)
        return Battle(context.getAliveAttackers(), context.getAliveDefenders(), attackerRolls, defenderRolls).fight()
    }

    private fun logContext(prefix: String) {
        Log.info(prefix + " Attackers: (" + context.getAliveAttackers().stream().map { it: Unit? -> it.getId().toString() + "| " + it.getName() + " " + it.getPower().value + " " + it.getHealth().value }.collect(Collectors.joining(", "))
                + ")   Defenders: (" + context.getAliveDefenders().stream().map { it: Unit? -> it.getId().toString() + "| " + it.getName() + " " + it.getPower().value + " " + it.getHealth().value }.collect(Collectors.joining(", ")) + ")")
    }

    private fun fightRound() {
        logContext("Fight round begin:")
        val result = rollFight()
        val update = NT_Battle_Update()
        update.attackerRolls = result.getAttackRolls().stream().mapToInt { i: Int? -> i!! }.toArray()
        update.defenderRolls = result.getDefenderRolls().stream().mapToInt { i: Int? -> i!! }.toArray()
        update.attackers = context.getAliveAttackers().stream().sorted(sortById()).map<NT_Unit?> { obj: Unit? -> obj!!.nt() }.toArray<NT_Unit> { _Dummy_.__Array__() }
        update.defenders = context.getAliveDefenders().stream().sorted(sortById()).map<NT_Unit?> { obj: Unit? -> obj!!.nt() }.toArray<NT_Unit> { _Dummy_.__Array__() }
        logContext("Fight round result:")
        var state = NT_Battle_Update.STATE_FIGHTING
        val attackersDead = context.getAliveAttackers().isEmpty()
        val defendersDead = context.getAliveDefenders().isEmpty()
        if (attackersDead && defendersDead) {
            state = NT_Battle_Update.STATE_DRAW
        } else if (attackersDead) {
            state = NT_Battle_Update.STATE_DEFENDER_WON
        } else if (defendersDead) {
            state = NT_Battle_Update.STATE_ATTACKER_WON
        }
        update.state = state
        update.damage = result!!.damage.stream().map<NT_Battle_Damage>(Function<AttackDamage?, NT_Battle_Damage> { nt() }).toArray<NT_Battle_Damage> { _Dummy_.__Array__() }
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
            if (context.getAttackingPlayer() != null) {
                if (!context.getAttackingPlayer().serverPlayer.isOnline) {
                    //retreat cause offline
                    Log.info("Retreat from battle because attacking player is offline")
                    battleFinished(true)
                }
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
        Log.info("Battle over! Surviving Attackers: (" + result.aliveAttackers.stream().map { it: Unit -> it.id.toString() + "| " + it.name + " " + it.power + " " + it.health }.collect(Collectors.joining(", ")) + ")" +
                " Killed Attackers: (" + result.killedAttackers.stream().map { it: Unit -> it.id.toString() + "| " + it.name + " " + it.power + " " + it.health }.collect(Collectors.joining(", ")) + ")" +
                " Surviving Defenders: (" + result.aliveDefenders.stream().map { it: Unit -> it.id.toString() + "| " + it.name + " " + it.power + " " + it.health }.collect(Collectors.joining(", ")) + ")" +
                " Killed Defenders: (" + result.killedDefenders.stream().map { it: Unit -> it.id.toString() + "| " + it.name + " " + it.power + " " + it.health }.collect(Collectors.joining(", ")) + ")")
        isBattleActive = false
        val updateReceiver = game.updateReceiver
        val fallenUnits: MutableList<Unit?> = ArrayList()
        fallenUnits.addAll(result.killedAttackers)
        fallenUnits.addAll(result.killedDefenders)
        fallenUnits.forEach(Consumer { unit: Unit? -> GameUtils.remove(game, unit) })
        fallenUnits.forEach(Consumer { unit: Unit? -> unit!!.onDeath(result) })
        fallenUnits.forEach(Consumer { unit: Unit? -> updateReceiver!!.killed(unit, result) })
        updateReceiver!!.battleResult(result)
        //if defenders lost, move surviving attackers to location
        if (result.attackersWon()) {
            UnitControl.move(game, result.aliveAttackers, result.getLocation())
        }
        GameUtils.sendUpdate(game, game.nt())
        //find dead monsters to give killing player rewards
        rewardKilledMonsters(result.getAttackingPlayer(), result.killedDefenders)
        result.getDefendingPlayers().forEach(Consumer { defendingPlayer: Player? -> rewardKilledMonsters(defendingPlayer, result.killedAttackers) })
    }

    private fun rewardKilledMonsters(killer: Player?, units: List<Unit?>?) {
        if (killer != null) {
            val fraction = killer.fraction
            units!!.stream().filter { it: Unit? -> it is Monster }.map { it: Unit? -> it as Monster? }.forEach { monster: Monster? ->
                killer.goalHandler.addStars(monster.getStars())
                if (monster.getOwner() == null) {
                    fraction!!.killedMonster(monster)
                }
            }
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
