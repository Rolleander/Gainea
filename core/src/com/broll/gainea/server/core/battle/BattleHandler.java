package com.broll.gainea.server.core.battle;

import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.net.NT_Battle_Start;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ReactionActions;
import com.broll.gainea.server.core.map.Location;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BattleHandler {

    private GameContainer game;
    private boolean battleActive = false;
    private final static int BATTLE_ANIMATION_DELAY = 3000;
    private ReactionActions reactionResult;
    private List<BattleObject> attackers;
    private List<BattleObject> defenders;

    public BattleHandler(GameContainer gameContainer, ReactionActions reactionResult) {
        this.game = gameContainer;
        this.reactionResult = reactionResult;
    }

    public void startBattle(List<BattleObject> attackers, List<BattleObject> defenders) {
        if (battleActive == false) {
            game.getReactionHandler().incActionStack();
            this.attackers = attackers;
            this.defenders = defenders;
            battleActive = true;
            NT_Battle_Start start = new NT_Battle_Start();
            start.attackers = attackers.stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
            start.defenders = defenders.stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
            reactionResult.sendGameUpdate(start);
            game.schedule(BATTLE_ANIMATION_DELAY, this::fight);
        }
    }

    private void fight() {
        //attackers are always of one owner
        Player attackerOwner = attackers.get(0).getOwner();
        //there can be defenders of multiple owners, so attack the army of a random owner first
        Collections.shuffle(defenders);
        Player defenderOwner = defenders.get(0).getOwner();
        Location battleLocation = defenders.get(0).getLocation();
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
        });
        result.getDeadDefenders().forEach(unit -> {
            defenders.remove(unit);
            defendingArmy.remove(unit);
            unitDied(unit);
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
            //schedule next fight round
            game.schedule(BATTLE_ANIMATION_DELAY, this::fight);
        } else {
            //battle finished, all attackers or defenders died
            battleActive = false;
            game.getReactionHandler().decActionStack();
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
