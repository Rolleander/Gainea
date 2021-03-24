package com.broll.gainea.server.core.battle;

import com.broll.gainea.net.NT_Battle_Reaction;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.net.NT_Battle_Start;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ReactionActions;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.processing.GameUpdateReceiverProxy;
import com.broll.gainea.server.core.utils.GameUtils;
import com.broll.gainea.server.core.utils.ProcessingUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class BattleHandler {

    private final static Logger Log = LoggerFactory.getLogger(BattleHandler.class);
    private GameContainer game;
    private boolean battleActive = false;
    public final static int BATTLE_ANIMATION_DELAY = 3000;
    private ReactionActions reactionResult;
    private List<BattleObject> attackers;
    private List<BattleObject> defenders;
    private List<BattleObject> aliveAttackers;
    private List<BattleObject> aliveDefenders;
    private Player attackerOwner, defenderOwner;
    private List<BattleObject> killedAttackers = new ArrayList<>();
    private List<BattleObject> killedDefenders = new ArrayList<>();
    private CompletableFuture<Boolean> keepAttacking;
    private List<BattleObject> defendingArmy;
    private Location battleLocation;
    private boolean allowRetreat = true;

    public BattleHandler(GameContainer gameContainer, ReactionActions reactionResult) {
        this.game = gameContainer;
        this.reactionResult = reactionResult;
    }

    public void startBattle(List<? extends BattleObject> attackers, List<? extends BattleObject> defenders) {
        startBattle(attackers, defenders, true);
    }

    public void startBattle(List<? extends BattleObject> attackers, List<? extends BattleObject> defenders, boolean allowRetreat) {
        if (battleActive == false) {
            this.allowRetreat = allowRetreat;
            this.attackers = new ArrayList<>(attackers);
            this.defenders = new ArrayList<>(defenders);
            killedDefenders.clear();
            killedAttackers.clear();
            battleActive = true;
            prepareFight();
            sendFightStart();
            ProcessingUtils.pause(BATTLE_ANIMATION_DELAY);
            fight();
        } else {
            Log.warn("Could not start battle because a battle is still going on");
        }
    }

    private void sendFightStart() {
        Log.trace("Start fight");
        NT_Battle_Start start = new NT_Battle_Start();
        start.attackers = aliveAttackers.stream().sorted(sortById()).map(BattleObject::nt).toArray(NT_Unit[]::new);
        start.defenders = defendingArmy.stream().sorted(sortById()).map(BattleObject::nt).toArray(NT_Unit[]::new);
        start.allowRetreat = allowRetreat;
        start.location = battleLocation.getNumber();
        if (attackerOwner != null) {
            start.attacker = attackerOwner.getServerPlayer().getId();
        }
        reactionResult.sendGameUpdate(start);
    }

    private Comparator<BattleObject> sortById() {
        return (o1, o2) -> Integer.compare(o1.getId(), o2.getId());
    }

    public void playerReaction(Player player, NT_Battle_Reaction battle_reaction) {
        if (attackerOwner == player && keepAttacking != null) {
            Log.trace("Handle battle reaction");
            keepAttacking.complete(battle_reaction.keepAttacking);
        } else {
            Log.warn("Battle reaction ignored because player is not attacker");
        }
    }

    private void prepareFight() {
        Log.trace("Prepare fight");
        this.aliveAttackers = attackers.stream().filter(BattleObject::isAlive).collect(Collectors.toList());
        this.aliveDefenders = defenders.stream().filter(BattleObject::isAlive).collect(Collectors.toList());
        //attackers are always of one owner
        attackerOwner = aliveAttackers.get(0).getOwner();
        if (attackerOwner == null) {
            //wild monsters are attackers, will always keep attacking
            allowRetreat = false;
        }
        //there can be defenders of multiple owners, so attack the army of a random owner first
        Collections.shuffle(aliveDefenders);
        defenderOwner = aliveDefenders.get(0).getOwner();
        battleLocation = aliveDefenders.get(0).getLocation();
        defendingArmy = aliveDefenders.stream().filter(defender -> defender.getOwner() == defenderOwner).collect(Collectors.toList());
    }

    private void fight() {
        Battle battle;
        Log.info("Fight!  Attackers: (" + aliveAttackers.stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower().getValue() + " " + it.getHealth().getValue()).collect(Collectors.joining(", ")) + ")   Defenders: (" + defendingArmy.stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower().getValue() + " " + it.getHealth().getValue()).collect(Collectors.joining(", ")) + ")");
        battle = new Battle(battleLocation, attackerOwner, aliveAttackers, defenderOwner, defendingArmy);
        FightResult result = battle.fight();
        NT_Battle_Update update = new NT_Battle_Update();
        update.attackerRolls = result.getAttackRolls().stream().mapToInt(i -> i).toArray();
        update.defenderRolls = result.getDefenderRolls().stream().mapToInt(i -> i).toArray();
        update.attackers = aliveAttackers.stream().sorted(sortById()).map(BattleObject::nt).toArray(NT_Unit[]::new);
        update.defenders = defendingArmy.stream().sorted(sortById()).map(BattleObject::nt).toArray(NT_Unit[]::new);
        Log.info("Fight result:  Attackers: (" + aliveAttackers.stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower().getValue() + " " + it.getHealth().getValue()).collect(Collectors.joining(", ")) + ")   Defenders: (" + defendingArmy.stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower().getValue() + " " + it.getHealth().getValue()).collect(Collectors.joining(", ")) + ")");
        result.getDeadAttackers().forEach(unit -> {
            aliveAttackers.remove(unit);
            killedAttackers.add(unit);
        });
        result.getDeadDefenders().forEach(unit -> {
            aliveDefenders.remove(unit);
            defendingArmy.remove(unit);
            killedDefenders.add(unit);
        });
        int state = NT_Battle_Update.STATE_FIGHTING;
        boolean attackersDead = aliveAttackers.isEmpty();
        boolean defendersDead = aliveDefenders.isEmpty();
        if (attackersDead && defendersDead) {
            state = NT_Battle_Update.STATE_DRAW;
        } else if (attackersDead) {
            state = NT_Battle_Update.STATE_DEFENDER_WON;
        } else if (defendersDead) {
            state = NT_Battle_Update.STATE_ATTACKER_WON;
        }
        update.state = state;
        //send update
        reactionResult.sendGameUpdate(update);
        int delay = getAnimationDelay(result.getAttackRolls().size(), result.getDefenderRolls().size());
        if (state == NT_Battle_Update.STATE_FIGHTING) {
            //wait for player if he wants to keep attacking
            if (allowRetreat) {
                keepAttacking = new CompletableFuture<>();
            }
            ProcessingUtils.pause(delay);
            prepareNextRound();
        } else {
            //battle finished, all attackers or defenders died
            ProcessingUtils.pause(delay);
            battleFinished();
        }
    }

    public static int getAnimationDelay(int atkRolls, int defRolls) {
        return BATTLE_ANIMATION_DELAY + 1500 * Math.min(atkRolls, defRolls);
    }

    private void fightRound() {
        Player previousEnemy = defenderOwner;
        prepareFight();
        if (defenderOwner != previousEnemy) {
            //next round is against other owner, send new battle start to clients before this round
            sendFightStart();
            //start next round after wait
            ProcessingUtils.pause(BATTLE_ANIMATION_DELAY);
        }
        fight();
    }

    private void prepareNextRound() {
        //wait for player if he wants to keep attacking
        boolean startNextRound = true;
        if (allowRetreat) {
            //disconnect check
            if (attackerOwner != null) {
                if (!attackerOwner.getServerPlayer().isOnline()) {
                    //retreat cause offline
                    Log.info("Retreat from battle because attacking player is offline");
                    battleFinished();
                }
            }
            try {
                Log.trace("Wait for battle reaction");
                startNextRound = keepAttacking.get().booleanValue();
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
            battleFinished();
        }
    }

    private void battleFinished() {
        BattleResult result = new BattleResult(attackers, defenders, battleLocation);
        Log.info("Battle over! Surviving Attackers: (" + aliveAttackers.stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower() + " " + it.getHealth()).collect(Collectors.joining(", ")) + ")  Surviving Defenders: (" + aliveDefenders.stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower() + " " + it.getHealth()).collect(Collectors.joining(", ")) + ")");
        //if defenders lost, move surviving attackers to location (unless attackers are wild monsters)
        if (result.attackersWon()) {
            UnitControl.move(game, attackers.stream().filter(BattleObject::isAlive).collect(Collectors.toList()), battleLocation);
        }
        battleActive = false;
        GameUpdateReceiverProxy updateReceiver = game.getUpdateReceiver();
        List<BattleObject> fallenUnits = new ArrayList<>();
        fallenUnits.addAll(killedAttackers);
        fallenUnits.addAll(killedDefenders);
        fallenUnits.forEach(unit -> GameUtils.remove(game, unit));
        fallenUnits.forEach(unit -> updateReceiver.killed(unit, result));
        updateReceiver.battleResult(result);
        GameUtils.sendUpdate(game, game.nt());
        //find dead monsters to give killing player rewards
        rewardKilledMonsters(attackerOwner, killedDefenders);
        rewardKilledMonsters(defenderOwner, killedAttackers);
    }

    private void rewardKilledMonsters(Player killer, List<BattleObject> units) {
        if (killer != null) {
            Fraction fraction = killer.getFraction();
            units.stream().filter(it -> it instanceof Monster && it.getOwner() == null).map(it -> (Monster) it).forEach(fraction::killedMonster);
        }
    }

    public boolean isBattleActive() {
        return battleActive;
    }

}
