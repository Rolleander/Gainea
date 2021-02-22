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
import com.broll.gainea.server.core.utils.GameUtils;
import com.broll.gainea.server.core.utils.ProcessingUtils;
import com.broll.gainea.server.core.utils.UnitControl;
import com.esotericsoftware.minlog.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        }
    }

    private void sendFightStart() {
        NT_Battle_Start start = new NT_Battle_Start();
        start.attackers = aliveAttackers.stream().sorted(sortById()).map(BattleObject::nt).toArray(NT_Unit[]::new);
        start.defenders = defendingArmy.stream().sorted(sortById()).map(BattleObject::nt).toArray(NT_Unit[]::new);
        reactionResult.sendGameUpdate(start);
    }

    private Comparator<BattleObject> sortById() {
        return (o1, o2) -> Integer.compare(o1.getId(), o2.getId());
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
        Log.info("Fight!  Attackers: (" + aliveAttackers.stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower() + " " + it.getHealth()).collect(Collectors.joining(", ")) + ")   Defenders: (" + defendingArmy.stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower() + " " + it.getHealth()).collect(Collectors.joining(", ")) + ")");
        battle = new Battle(battleLocation, attackerOwner, aliveAttackers, defenderOwner, defendingArmy);
        FightResult result = battle.fight();
        NT_Battle_Update update = new NT_Battle_Update();
        update.attackerRolls = result.getAttackRolls().stream().mapToInt(i -> i).toArray();
        update.defenderRolls = result.getDefenderRolls().stream().mapToInt(i -> i).toArray();
        update.attackers = aliveAttackers.stream().sorted(sortById()).map(BattleObject::nt).toArray(NT_Unit[]::new);
        update.defenders = defendingArmy.stream().sorted(sortById()).map(BattleObject::nt).toArray(NT_Unit[]::new);
        Log.info("Fight result:  Attackers: (" + aliveAttackers.stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower() + " " + it.getHealth()).collect(Collectors.joining(", ")) + ")   Defenders: (" + defendingArmy.stream().map(it -> it.getId() + "| " + it.getName() + " " + it.getPower() + " " + it.getHealth()).collect(Collectors.joining(", ")) + ")");
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
            attackers.forEach(attacker -> attacker.setLocation(battleLocation));
        }
        update.state = state;
        //send update
        reactionResult.sendGameUpdate(update);
        int delay = BATTLE_ANIMATION_DELAY + 1500 * Math.min(result.getAttackRolls().size(), result.getDefenderRolls().size());
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
            try {
                startNextRound = keepAttacking.get().booleanValue();
            } catch (InterruptedException | ExecutionException e) {
                Log.error("Failed getting future", e);
            }
        }
        if (startNextRound) {
            //schedule next fight round
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
        game.getUpdateReceiver().battleResult(result);
        GameUtils.sendUpdate(game, game.nt());
        //find dead monsters to give killing player rewards
        if (attackerOwner != null) {
            Fraction fraction = attackerOwner.getFraction();
            killedDefenders.stream().filter(it -> it instanceof Monster && it.getOwner() == null).map(it -> (Monster) it).forEach(fraction::killedMonster);
        }
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
