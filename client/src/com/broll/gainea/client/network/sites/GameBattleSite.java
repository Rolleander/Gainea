package com.broll.gainea.client.network.sites;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.network.ProtoMapper;
import com.broll.gainea.client.game.map.Coordinates;
import com.broll.gainea.client.ui.ingame.map.ActionTrail;
import com.broll.gainea.client.ui.ingame.map.MapAction;
import com.broll.gainea.client.ui.ingame.map.MapScrollUtils;
import com.broll.gainea.net.*;
import com.broll.gainea.client.game.map.Location;
import com.broll.mpnll.client.site.MessageReceiverRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class GameBattleSite extends AbstractGameSite {
    private static final Logger Log = LoggerFactory.getLogger(GameBattleSite.class);
    private MapAction battleIntention;
    private ActionTrail battleIntentionTrail;

    @Override
    protected void registerReceivers(MessageReceiverRegistry registry) {
        on(registry, com.broll.gainea.net.proto.NT_Battle_Intention.class, this::received);
        on(registry, com.broll.gainea.net.proto.NT_Battle_Start.class, this::received);
        on(registry, com.broll.gainea.net.proto.NT_Battle_Update.class, this::received);
        on(registry, com.broll.gainea.net.proto.NT_Battle_Reaction.class, this::received);
    }

    private void received(com.broll.gainea.net.proto.NT_Battle_Intention battle) {
        game.state.updateIdleState(false);
        MapScrollUtils.showLocations(game, battle.getFromLocation(), battle.getToLocation());
        game.ui.inGameUI.clearSelection();
        battleIntention = new MapAction(game, 1, battle.getToLocation(), null);
        Location from = game.state.getMap().getLocation(battle.getFromLocation()), to = game.state.getMap().getLocation(battle.getToLocation());
        Coordinates toC = to.getCoordinates(), fromC = from.getCoordinates();
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

    private void received(com.broll.gainea.net.proto.NT_Battle_Start battle) {
        if (battleIntention != null)
            battleIntention.addAction(Actions.sequence(Actions.fadeOut(.3f), Actions.removeActor()));
        if (battleIntentionTrail != null)
            battleIntentionTrail.addAction(Actions.sequence(Actions.fadeOut(.3f), Actions.removeActor()));
        game.state.updateIdleState(false);
        ArrayList<NT_Unit> attackers = new ArrayList<>(Arrays.asList(ProtoMapper.units(battle.getAttackersList())));
        ArrayList<NT_Unit> defenders = new ArrayList<>(Arrays.asList(ProtoMapper.units(battle.getDefendersList())));
        int location = defenders.get(0).location;
        Log.info("Start fight: Attackers (" + attackers.stream().map(it -> it.id + "| " + it.name + " " + it.power + " " + it.health).collect(Collectors.joining(", ")) + ") Defenders (" + defenders.stream().map(it -> it.id + "| " + it.name + " " + it.power + " " + it.health).collect(Collectors.joining(", ")) + ")");
        boolean allowRetreat = battle.getAllowRetreat() && getPlayer().getId() == battle.getAttacker();
        MapScrollUtils.showLocations(game, battle.getLocation());
        game.ui.inGameUI.startBattle(attackers, defenders, game.state.getMap().getLocation(location), allowRetreat);
    }

    private void received(com.broll.gainea.net.proto.NT_Battle_Update battle) {
        Stack<NT_Battle_Damage> damage = new Stack<>();
        damage.addAll(Arrays.asList(ProtoMapper.battleDamage(battle.getDamageList())));
        game.ui.inGameUI.updateBattle(ProtoMapper.battleRolls(battle.getAttackerRollsList()), ProtoMapper.battleRolls(battle.getDefenderRollsList()), damage, battle.getState());
    }

    private void received(com.broll.gainea.net.proto.NT_Battle_Reaction nt) {
        if (!nt.getKeepAttacking()) game.ui.inGameUI.getBattleHandler().retreated();
    }
}
