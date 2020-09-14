package com.broll.gainea.server.core.battle;

import com.broll.gainea.net.NT_Battle_Reaction;
import com.broll.gainea.server.core.actions.impl.SelectChoiceAction;
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
import com.esotericsoftware.minlog.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class BattleHandler {

    private GameContainer game;
    private boolean battleActive = false;
    private final static int BATTLE_ANIMATION_DELAY = 3000;
    private ReactionActions reactionResult;
    private List<BattleObject> attackers;
    private List<BattleObject> defenders;
    private Player attackerOwner, defenderOwner;
    private List<BattleObject> killedAttackers = new ArrayList<>();
    private List<BattleObject> killedDefenders = new ArrayList<>();
    private CompletableFuture<Boolean> keepAttacking;
    private Location battleLocation;

    public BattleHandler(GameContainer gameContainer, ReactionActions reactionResult) {
        this.game = gameContainer;
        this.reactionResult = reactionResult;
    }

    public void startBattle(List<BattleObject> attackers, List<BattleObject> defenders) {
        if (battleActive == false) {
            this.attackers = attackers;
            this.defenders = defenders;
            battleActive = true;
            NT_Battle_Start start = new NT_Battle_Start();
            start.attackers = attackers.stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
            start.defenders = defenders.stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
            reactionResult.sendGameUpdate(start);
            game.getProcessingCore().execute(this::fight, BATTLE_ANIMATION_DELAY);
        }
    }

    public void playerReaction(Player player, NT_Battle_Reaction battle_reaction) {
        if (attackerOwner == player && keepAttacking != null) {
            keepAttacking.complete(battle_reaction.keepAttacking);
        }
    }

    private void fight() {
        //attackers are always of one owner
        attackerOwner = attackers.get(0).getOwner();
        //there can be defenders of multiple owners, so attack the army of a random owner first
        Collections.shuffle(defenders);
        defenderOwner = defenders.get(0).getOwner();
        battleLocation = defenders.get(0).getLocation();
        List<BattleObject> defendingArmy = defenders.stream().filter(defender -> defender.getOwner() == defenderOwner).collect(Collectors.toList());
        Battle battle;
        if (defenderOwner == null) {
            //fight against neutral enemies (monsters)
            battle = new Battle(battleLocation, attackerOwner, attackers, defendingArmy);
        } else {
            //fight against player
            battle = new Battle(battleLocation, attackerOwner, attackers, defenderOwner, defendingArmy);
        }
        FightResult result = battle.fight();
        result.getDeadAttackers().forEach(unit -> {
            attackers.remove(unit);
            unitDied(unit);
            killedAttackers.add(unit);
        });
        result.getDeadDefenders().forEach(unit -> {
            defenders.remove(unit);
            defendingArmy.remove(unit);
            unitDied(unit);
            killedDefenders.add(unit);
        });
        NT_Battle_Update update = new NT_Battle_Update();
        update.attackerRolls = result.getAttackRolls().stream().mapToInt(i -> i).toArray();
        update.defenderRolls = result.getDefenderRolls().stream().mapToInt(i -> i).toArray();
        update.remainingAttackers = attackers.stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
        update.remainingDefenders = defendingArmy.stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
        update.killedAttacker = result.getDeadAttackers().stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
        update.killedDefender = result.getDeadDefenders().stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
        int state = NT_Battle_Update.STATE_FIGHTING;
        if (attackers.isEmpty()) {
            state = NT_Battle_Update.STATE_DEFENDER_WON;
        } else if (defenders.isEmpty()) {
            state = NT_Battle_Update.STATE_ATTACKER_WON;
            //attackers won, move them to the fight location
            attackers.forEach(attacker -> game.moveObject(attacker, battleLocation));
        }
        update.state = state;
        //send update
        reactionResult.sendGameUpdate(update);
        if (state == NT_Battle_Update.STATE_FIGHTING) {
            prepareNextRound();
        } else {
            //battle finished, all attackers or defenders died
            battleFinished();
        }
    }

    private void prepareNextRound() {
        //wait for player if he wants to keep attacking
        keepAttacking = new CompletableFuture<>();
        boolean startNextRound = false;
        try {
            startNextRound = keepAttacking.get().booleanValue();
        } catch (InterruptedException | ExecutionException e) {
            Log.error("Failed getting future", e);
        }
        if (startNextRound) {
            //schedule next fight round
            game.getProcessingCore().execute(this::fight, BATTLE_ANIMATION_DELAY);
        } else {
            //end battle, attackers retreat
            battleFinished();
        }
    }

    private void battleFinished() {
        //if defenders lost, move surviving attackers to location
        if (defenders.size() == killedDefenders.size()) {
            attackers.stream().filter(BattleObject::isAlive).forEach(it -> it.setLocation(battleLocation));
        }
        //find monsters to give killing player rewards
        if (attackerOwner != null) {
            Fraction fraction = attackerOwner.getFraction();
            killedDefenders.stream().filter(it -> it instanceof Monster && it.getOwner() == null).map(it -> (Monster) it).forEach(fraction::killedMonster);
        }
        battleActive = false;
    }

    private void unitDied(BattleObject unit) {
        Player owner = unit.getOwner();
        unit.getLocation().getInhabitants().remove(unit);
        if (owner == null) {
            game.getObjects().remove(unit);
        } else {
            owner.getUnits().remove(unit);
        }
    }

    public boolean isBattleActive() {
        return battleActive;
    }

}
