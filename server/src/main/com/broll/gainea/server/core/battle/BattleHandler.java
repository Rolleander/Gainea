package com.broll.gainea.server.core.battle;

import com.broll.gainea.net.NT_Battle_Damage;
import com.broll.gainea.net.NT_Battle_Intention;
import com.broll.gainea.net.NT_Battle_Reaction;
import com.broll.gainea.net.NT_Battle_Start;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ReactionActions;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.processing.GameUpdateReceiverProxy;
import com.broll.gainea.server.core.utils.GameUtils;
import com.broll.gainea.server.core.utils.ProcessingUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class BattleHandler {

    private final static Logger Log = LoggerFactory.getLogger(BattleHandler.class);
    private final GameContainer game;
    private boolean battleActive = false;
    public final static int BATTLE_ANIMATION_DELAY = 2000;
    private final ReactionActions reactionResult;
    private CompletableFuture<Boolean> keepAttacking;
    private RollManipulator rollManipulator;
    private BattleContext context;
    private boolean allowRetreat = true;

    public BattleHandler(GameContainer gameContainer, ReactionActions reactionResult) {
        this.game = gameContainer;
        this.reactionResult = reactionResult;
    }

    public void reset() {
        this.battleActive = false;
    }

    public void startBattle(List<? extends Unit> attackers, List<? extends Unit> defenders) {
        startBattle(attackers, defenders, true);
    }

    public void startBattle(List<? extends Unit> attackers, List<? extends Unit> defenders, boolean allowRetreat) {
        if (!battleActive) {
            this.allowRetreat = allowRetreat;
            this.context = new BattleContext(new ArrayList<>(attackers), new ArrayList<>(defenders));
            this.rollManipulator = new RollManipulator();
            if (context.hasSurvivingAttackers() && context.hasSurvivingDefenders()) {
                prepareFight();
            } else {
                Log.warn("Could not start battle because no alive attackers or defenders");
            }
        } else {
            Log.warn("Could not start battle because a battle is still going on");
        }
    }

    private void prepareFight() {
        Log.trace("Prepare fight");
        battleActive = true;
        //attackers are always of one owner
        if (context.isNeutralAttacker()) {
            //wild monsters are attackers, will always keep attacking
            allowRetreat = false;
        }
        sendFightIntention();
        sendFightStart();
        ProcessingUtils.pause(BATTLE_ANIMATION_DELAY);
        fightRound();
    }

    private void sendFightIntention() {
        NT_Battle_Intention intention = new NT_Battle_Intention();
        intention.fromLocation = context.getSourceLocation().getNumber();
        intention.toLocation = context.getLocation().getNumber();
        reactionResult.sendGameUpdate(intention);
        ProcessingUtils.pause(BATTLE_ANIMATION_DELAY);
    }

    private void sendFightStart() {
        Log.trace("Start fight");
        NT_Battle_Start start = new NT_Battle_Start();
        start.attackers = context.getAttackers().stream().sorted(sortById()).map(Unit::nt).toArray(NT_Unit[]::new);
        start.defenders = context.getDefenders().stream().sorted(sortById()).map(Unit::nt).toArray(NT_Unit[]::new);
        start.allowRetreat = allowRetreat;
        start.location = context.getLocation().getNumber();
        game.getUpdateReceiver().battleBegin(context, rollManipulator);
        if (!context.isNeutralAttacker()) {
            start.attacker = context.getAttackingPlayer().getServerPlayer().getId();
        }
        reactionResult.sendGameUpdate(start);
    }


    private Comparator<Unit> sortById() {
        return Comparator.comparingInt(MapObject::getId);
    }

    public void playerReaction(Player player, NT_Battle_Reaction battle_reaction) {
        if (context.isAttacker(player) && keepAttacking != null) {
            Log.trace("Handle battle reaction");
            keepAttacking.complete(battle_reaction.keepAttacking);
        } else {
            Log.warn("Battle reaction ignored because player is not attacker");
        }
    }

    private FightResult rollFight() {
        RollResult attackerRolls = new RollResult(context, context.getAliveAttackers());
        RollResult defenderRolls = new RollResult(context, context.getAliveDefenders());
        rollManipulator.roundStarts(attackerRolls, defenderRolls);
        return new Battle(context.getAliveAttackers(), context.getAliveDefenders(), attackerRolls, defenderRolls).fight();
    }

    private void logContext(String prefix) {
        Log.info(prefix + " Attackers: (" + context.getAliveAttackers().stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower().getValue() + " " + it.getHealth().getValue()).collect(Collectors.joining(", "))
                + ")   Defenders: (" + context.getAliveDefenders().stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower().getValue() + " " + it.getHealth().getValue()).collect(Collectors.joining(", ")) + ")");
    }

    private void fightRound() {
        logContext("Fight round begin:");
        FightResult result = rollFight();
        NT_Battle_Update update = new NT_Battle_Update();
        update.attackerRolls = result.getAttackRolls().stream().mapToInt(i -> i).toArray();
        update.defenderRolls = result.getDefenderRolls().stream().mapToInt(i -> i).toArray();
        update.attackers = context.getAliveAttackers().stream().sorted(sortById()).map(Unit::nt).toArray(NT_Unit[]::new);
        update.defenders = context.getAliveDefenders().stream().sorted(sortById()).map(Unit::nt).toArray(NT_Unit[]::new);
        logContext("Fight round result:");
        int state = NT_Battle_Update.STATE_FIGHTING;
        boolean attackersDead = context.getAliveAttackers().isEmpty();
        boolean defendersDead = context.getAliveDefenders().isEmpty();
        if (attackersDead && defendersDead) {
            state = NT_Battle_Update.STATE_DRAW;
        } else if (attackersDead) {
            state = NT_Battle_Update.STATE_DEFENDER_WON;
        } else if (defendersDead) {
            state = NT_Battle_Update.STATE_ATTACKER_WON;
        }
        update.state = state;
        update.damage = result.getDamage().stream().map(FightResult.AttackDamage::nt).toArray(NT_Battle_Damage[]::new);
        //send update
        reactionResult.sendGameUpdate(update);
        int delay = getAnimationDelay(result.getAttackRolls().size(), result.getDefenderRolls().size());
        ProcessingUtils.pause(delay);
        if (state == NT_Battle_Update.STATE_FIGHTING) {
            //wait for player if he wants to keep attacking
            if (allowRetreat) {
                keepAttacking = new CompletableFuture<>();
            }
            prepareNextRound();
        } else {
            //battle finished, all attackers or defenders died
            ProcessingUtils.pause(BATTLE_ANIMATION_DELAY);
            battleFinished(false);
        }
    }

    public static int getAnimationDelay(int atkRolls, int defRolls) {
        return BATTLE_ANIMATION_DELAY / 2 + 800 * Math.min(atkRolls, defRolls);
    }

    private void prepareNextRound() {
        //wait for player if he wants to keep attacking
        boolean startNextRound = true;
        if (allowRetreat) {
            //disconnect check
            if (context.getAttackingPlayer() != null) {
                if (!context.getAttackingPlayer().getServerPlayer().isOnline()) {
                    //retreat cause offline
                    Log.info("Retreat from battle because attacking player is offline");
                    battleFinished(true);
                }
            }
            try {
                Log.trace("Wait for battle reaction");
                startNextRound = keepAttacking.get();
            } catch (InterruptedException | ExecutionException e) {
                Log.error("Failed getting future", e);
            }
        }
        if (startNextRound) {
            //schedule next fight round
            Log.trace("Start next battle round");
            fightRound();
        } else {
            //end battle, attackers retreat
            battleFinished(true);
        }
    }

    private void battleFinished(boolean retreated) {
        BattleResult result = new BattleResult(retreated, context);
        Log.info("Battle over! Surviving Attackers: (" + result.getAliveAttackers().stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower() + " " + it.getHealth()).collect(Collectors.joining(", ")) + ")" +
                " Killed Attackers: (" + result.getKilledAttackers().stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower() + " " + it.getHealth()).collect(Collectors.joining(", ")) + ")" +
                " Surviving Defenders: (" + result.getAliveDefenders().stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower() + " " + it.getHealth()).collect(Collectors.joining(", ")) + ")" +
                " Killed Defenders: (" + result.getKilledDefenders().stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower() + " " + it.getHealth()).collect(Collectors.joining(", ")) + ")");
        battleActive = false;
        GameUpdateReceiverProxy updateReceiver = game.getUpdateReceiver();
        List<Unit> fallenUnits = new ArrayList<>();
        fallenUnits.addAll(result.getKilledAttackers());
        fallenUnits.addAll(result.getKilledDefenders());
        fallenUnits.forEach(unit -> GameUtils.remove(game, unit));
        fallenUnits.forEach(unit -> unit.onDeath(result));
        fallenUnits.forEach(unit -> updateReceiver.killed(unit, result));
        updateReceiver.battleResult(result);
        //if defenders lost, move surviving attackers to location
        if (result.attackersWon()) {
            UnitControl.move(game, result.getAliveAttackers(), result.getLocation());
        }
        GameUtils.sendUpdate(game, game.nt());
        //find dead monsters to give killing player rewards
        rewardKilledMonsters(result.getAttackingPlayer(), result.getKilledDefenders());
        result.getDefendingPlayers().forEach(defendingPlayer -> rewardKilledMonsters(defendingPlayer, result.getKilledDefenders()));
    }

    private void rewardKilledMonsters(Player killer, List<Unit> units) {
        if (killer != null) {
            Fraction fraction = killer.getFraction();
            units.stream().filter(it -> it instanceof Monster).map(it -> (Monster) it).forEach(monster -> {
                killer.getGoalHandler().addStars(monster.getStars());
                if (monster.getOwner() == null) {
                    fraction.killedMonster(monster);
                }
            });
        }
    }

    public boolean isBattleActive() {
        return battleActive;
    }

}
