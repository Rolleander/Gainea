package com.broll.gainea.client.network.sites;

import com.broll.gainea.client.game.PlayerPerformAction;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_PlayerAction;
import com.broll.gainea.net.NT_Reaction;
import com.broll.networklib.PackageReceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameActionSite extends AbstractGameSite {
    private final static Logger Log = LoggerFactory.getLogger(GameActionSite.class);
    public static NT_PlayerAction CURRENT_PLAYER_ACTION;
    private PlayerPerformAction playerPerformAction = this::performAction;

    //player has to perform this action
    @PackageReceiver
    public void received(NT_PlayerAction action) {
        Log.info("received action " + action);
        CURRENT_PLAYER_ACTION = action;
        game.state.performAction(action.action, playerPerformAction);
    }

    public void performAction(NT_Action action, int option, int[] options) {
        NT_Reaction reaction = new NT_Reaction();
        reaction.actionId = action.actionId;
        reaction.option = option;
        reaction.options = options;
        client.sendTCP(reaction);
        Log.info("sent reaction for action " + reaction.actionId);
    }
}
