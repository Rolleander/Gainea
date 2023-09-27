package com.broll.gainea.client.network.sites;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.broll.gainea.client.ui.ingame.map.ActionTrail;
import com.broll.gainea.client.ui.ingame.map.MapAction;
import com.broll.gainea.client.ui.ingame.map.MapScrollUtils;
import com.broll.gainea.net.NT_Battle_Damage;
import com.broll.gainea.net.NT_Battle_Intention;
import com.broll.gainea.net.NT_Battle_Start;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Coordinates;
import com.broll.gainea.server.core.map.Location;
import com.broll.networklib.PackageReceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class GameBattleSite extends AbstractGameSite {

    private final static Logger Log = LoggerFactory.getLogger(GameBattleSite.class);
    private List<NT_Unit> attackers;
    private List<NT_Unit> defenders;

    private MapAction battleIntention;
    private ActionTrail battleIntentionTrail;

    @PackageReceiver
    public void received(NT_Battle_Intention battle) {
        game.state.updateIdleState(false);
        MapScrollUtils.showLocations(game, battle.fromLocation, battle.toLocation);
        game.ui.inGameUI.clearSelection();
        battleIntention = new MapAction(game, 1, battle.toLocation, null);
        Location from = game.state.getMap().getLocation(battle.fromLocation);
        Location to = game.state.getMap().getLocation(battle.toLocation);
        Coordinates toC = to.coordinates;
        Coordinates fromC = from.coordinates;
        battleIntention.setPosition(toC.getDisplayX(), toC.getDisplayY());
        float angle = MathUtils.atan2(toC.getDisplayY() - fromC.getDisplayY(), toC.getDisplayX() - fromC.getDisplayX());
        battleIntention.setRotation((float) Math.toDegrees(angle - Math.PI / 2));
        battleIntention.setFromTo(from.number, to.number);
        battleIntentionTrail = new ActionTrail(game, 1, toC, fromC);
        game.gameStage.addActor(battleIntentionTrail);
        battleIntention.setTrail(battleIntentionTrail);
        battleIntention.setVisible(true);
        game.gameStage.addActor(battleIntention);
    }

    @PackageReceiver
    public void received(NT_Battle_Start battle) {
        if(battleIntention!= null){
            battleIntention.addAction(Actions.sequence(Actions.fadeOut(0.3f), Actions.removeActor()));
        }
        if(battleIntentionTrail!= null){
            battleIntentionTrail.addAction(Actions.sequence(Actions.fadeOut(0.3f), Actions.removeActor()));
        }
        game.state.updateIdleState(false);
        this.attackers = Lists.newArrayList(battle.attackers);
        this.defenders = Lists.newArrayList(battle.defenders);
        int location = defenders.get(0).location;
        Log.info("Start fight: Attackers (" + attackers.stream().map(it -> it.id + "| " + it.name + " " + it.power + " " + it.health).collect(Collectors.joining(", ")) + ") Defenders (" + defenders.stream().map(it -> it.id + "| " + it.name + " " + it.power + " " + it.health).collect(Collectors.joining(", ")) + ")");
        boolean allowRetreat = battle.allowRetreat && getPlayer().getId() == battle.attacker;
        MapScrollUtils.showLocations(game, battle.location);
        game.ui.inGameUI.startBattle(attackers, defenders, game.state.getMap().getLocation(location), allowRetreat);
    }

    @PackageReceiver
    public void received(NT_Battle_Update battle) {
        int[] attackRolls = battle.attackerRolls;
        int[] defenderRolls = battle.defenderRolls;
        List<NT_Unit> attackers = Lists.newArrayList(battle.attackers);
        List<NT_Unit> defenders = Lists.newArrayList(battle.defenders);
        Log.info("Update fight: Attackers (" + attackers.stream().map(it -> it.id + "| " + it.name + " " + it.power + " " + it.health).collect(Collectors.joining(", ")) + ") Defenders (" + defenders.stream().map(it -> it.id + "| " + it.name + " " + it.power + " " + it.health).collect(Collectors.joining(", ")) + ")");
        this.attackers = attackers;
        this.defenders = defenders;
        this.attackers.removeIf(it -> it.health <= 0);
        this.defenders.removeIf(it -> it.health <= 0);
        Stack<NT_Battle_Damage> damage = new Stack<>();
        damage.addAll(Arrays.asList(battle.damage));
        game.ui.inGameUI.updateBattle(attackRolls, defenderRolls, damage,  battle.state);
    }
}
