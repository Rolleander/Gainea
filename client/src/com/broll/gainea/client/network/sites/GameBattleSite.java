package com.broll.gainea.client.network.sites;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.ingame.map.ActionTrail;
import com.broll.gainea.client.ui.ingame.map.MapAction;
import com.broll.gainea.client.ui.ingame.map.MapScrollUtils;
import com.broll.gainea.net.NT_Battle_Damage;
import com.broll.gainea.net.NT_Battle_Intention;
import com.broll.gainea.net.NT_Battle_Reaction;
import com.broll.gainea.net.NT_Battle_Roll;
import com.broll.gainea.net.NT_Battle_Start;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Coordinates;
import com.broll.gainea.server.core.map.Location;
import com.broll.networklib.PackageReceiver;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Collectors;

public class GameBattleSite extends AbstractGameSite {

    private final static Logger Log = LoggerFactory.getLogger(GameBattleSite.class);
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
        Coordinates toC = to.getCoordinates();
        Coordinates fromC = from.getCoordinates();
        battleIntention.setPosition(toC.getDisplayX(), toC.getDisplayY());
        float angle = MathUtils.atan2(toC.getDisplayY() - fromC.getDisplayY(), toC.getDisplayX() - fromC.getDisplayX());
        battleIntention.setRotation((float) Math.toDegrees(angle - Math.PI / 2));
        battleIntention.setFromTo(from.getNumber(), to.getNumber());
        battleIntentionTrail = new ActionTrail(game, 1, toC, fromC);
        battleIntentionTrail.hover = true;
        game.gameStage.addActor(battleIntentionTrail);
        battleIntention.setTrail(battleIntentionTrail);
        battleIntention.setVisible(true);
        game.gameStage.addActor(battleIntention);
        AudioPlayer.playSound("attack_intent.ogg");
        AudioPlayer.playSound("battle.ogg");
    }

    @PackageReceiver
    public void received(NT_Battle_Start battle) {
        if (battleIntention != null) {
            battleIntention.addAction(Actions.sequence(Actions.fadeOut(0.3f), Actions.removeActor()));
        }
        if (battleIntentionTrail != null) {
            battleIntentionTrail.addAction(Actions.sequence(Actions.fadeOut(0.3f), Actions.removeActor()));
        }
        game.state.updateIdleState(false);
        ArrayList<NT_Unit> attackers = Lists.newArrayList(battle.attackers);
        ArrayList<NT_Unit> defenders = Lists.newArrayList(battle.defenders);
        int location = defenders.get(0).location;
        Log.info("Start fight: Attackers (" + attackers.stream().map(it -> it.id + "| " + it.name + " " + it.power + " " + it.health).collect(Collectors.joining(", ")) + ") Defenders (" + defenders.stream().map(it -> it.id + "| " + it.name + " " + it.power + " " + it.health).collect(Collectors.joining(", ")) + ")");
        boolean allowRetreat = battle.allowRetreat && getPlayer().getId() == battle.attacker;
        MapScrollUtils.showLocations(game, battle.location);
        game.ui.inGameUI.startBattle(attackers, defenders, game.state.getMap().getLocation(location), allowRetreat);
    }

    @PackageReceiver
    public void received(NT_Battle_Update battle) {
        NT_Battle_Roll[] attackRolls = battle.attackerRolls;
        NT_Battle_Roll[] defenderRolls = battle.defenderRolls;
        Stack<NT_Battle_Damage> damage = new Stack<>();
        damage.addAll(Arrays.asList(battle.damage));
        game.ui.inGameUI.updateBattle(attackRolls, defenderRolls, damage, battle.state);
    }

    @PackageReceiver
    public void received(NT_Battle_Reaction nt) {
        if (!nt.keepAttacking) {
            game.ui.inGameUI.getBattleHandler().retreated();
        }
    }
}
