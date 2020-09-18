package com.broll.gainea.server.core.battle;

import com.broll.gainea.net.NT_Battle_Reaction;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.net.NT_Battle_Start;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ReactionActions;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.UnitControl;
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
    private List<BattleObject> aliveAttackers;
    private List<BattleObject> aliveDefenders;
    private Player attackerOwner, defenderOwner;
    private List<BattleObject> killedAttackers = new ArrayList<>();
    private List<BattleObject> killedDefenders = new ArrayList<>();
    private CompletableFuture<Boolean> keepAttacking;
    private List<BattleObject> defendingArmy;
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
            prepareFight();
            sendFightStart();
            game.getProcessingCore().execute(this::fight, BATTLE_ANIMATION_DELAY);
        }
    }

    private void sendFightStart() {
        NT_Battle_Start start = new NT_Battle_Start();
        start.attackers = aliveAttackers.stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
        start.defenders = defendingArmy.stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
        reactionResult.sendGameUpdate(start);
    }

    public void playerReaction(Player player, NT_Battle_Reaction battle_reaction) {
        if (attackerOwner == player && keepAttacking != null) {
            keepAttacking.complete(battle_reaction.keepAttacking);
        }
    }

    private void prepareFight() {
        this.aliveAttackers = attackers.stream().filter(BattleObject::isAlive).collect(Collectors.toList());
        this.aliveDefenders = defenders.stream().filter(BattleObject::isAlive).collect(Collectors.toList());
        //attackers are always of one owner
        attackerOwner = aliveAttackers.get(0).getOwner();
        //there can be defenders of multiple owners, so attack the army of a random owner first
        Collections.shuffle(aliveDefenders);
        defenderOwner = aliveDefenders.get(0).getOwner();
        battleLocation = aliveDefenders.get(0).getLocation();
        defendingArmy = aliveDefenders.stream().filter(defender -> defender.getOwner() == defenderOwner).collect(Collectors.toList());
    }

    private void fight() {
        Battle battle;
        if (defenderOwner == null) {
            //fight against neutral enemies (monsters)
            battle = new Battle(battleLocation, attackerOwner, aliveAttackers, defendingArmy);
        } else {
            //fight against player
            battle = new Battle(battleLocation, attackerOwner, attackers, defenderOwner, defendingArmy);
        }
        FightResult result = battle.fight();
        NT_Battle_Update update = new NT_Battle_Update();
        update.attackerRolls = result.getAttackRolls().stream().mapToInt(i -> i).toArray();
        update.defenderRolls = result.getDefenderRolls().stream().mapToInt(i -> i).toArray();
        update.attackers = aliveAttackers.stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
        update.defenders = defendingArmy.stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
        result.getDeadAttackers().forEach(unit -> {
            aliveAttackers.remove(unit);
            unitDied(unit);
            killedAttackers.add(unit);
        });
        result.getDeadDefenders().forEach(unit -> {
            aliveDefenders.remove(unit);
            defendingArmy.remove(unit);
            unitDied(unit);
            killedDefenders.add(unit);
        });
        int state = NT_Battle_Update.STATE_FIGHTING;
        if (aliveAttackers.isEmpty()) {
            state = NT_Battle_Update.STATE_DEFENDER_WON;
        } else if (aliveDefenders.isEmpty()) {
            state = NT_Battle_Update.STATE_ATTACKER_WON;
            //attackers won, move them to the fight location
            attackers.forEach(attacker -> game.moveObject(attacker, battleLocation));
        }
        update.state = state;
        //send update
        reactionResult.sendGameUpdate(update);
        int delay = BATTLE_ANIMATION_DELAY + 1500 * Math.min(result.getAttackRolls().size(), result.getDefenderRolls().size());
        if (state == NT_Battle_Update.STATE_FIGHTING) {
            //wait for player if he wants to keep attacking
            keepAttacking = new CompletableFuture<>();
            game.getProcessingCore().execute(this::prepareNextRound, delay);
        } else {
            //battle finished, all attackers or defenders died
            game.getProcessingCore().execute(this::battleFinished, delay);
        }
    }

    private void fightRound() {
        Player previousEnemy = defenderOwner;
        prepareFight();
        if (defenderOwner != previousEnemy) {
            //next round is against other owner, send new battle start to clients before this round
            sendFightStart();
            //start next round after wait
            game.getProcessingCore().execute(this::fight, BATTLE_ANIMATION_DELAY);
        } else {
            //directly continue with next round
            fight();
        }
    }

    private void prepareNextRound() {
        //wait for player if he wants to keep attacking
        boolean startNextRound = false;
        try {
            startNextRound = keepAttacking.get().booleanValue();
        } catch (InterruptedException | ExecutionException e) {
            Log.error("Failed getting future", e);
        }
        if (startNextRound) {
            //schedule next fight round
            game.getProcessingCore().execute(this::fightRound);
        } else {
            //end battle, attackers retreat
            game.getProcessingCore().execute(this::battleFinished);
        }
    }

    private void battleFinished() {
        BattleResult result = new BattleResult(attackers, defenders, battleLocation);
        //if defenders lost, move surviving attackers to location
        if (result.attackersWon()) {
            UnitControl.move(game, attackers.stream().filter(BattleObject::isAlive).collect(Collectors.toList()), battleLocation);
        }
        //find monsters to give killing player rewards
        if (attackerOwner != null) {
            Fraction fraction = attackerOwner.getFraction();
            killedDefenders.stream().filter(it -> it instanceof Monster && it.getOwner() == null).map(it -> (Monster) it).forEach(fraction::killedMonster);
        }
        battleActive = false;
        game.getUpdateReceiver().battleResult(result);
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
