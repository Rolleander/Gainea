package com.broll.gainea.client.sites;

import com.broll.gainea.client.game.PlayerPerformAction;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_PlayerAction;
import com.broll.gainea.net.NT_Reaction;
import com.broll.networklib.PackageReceiver;

public class GameActionSite extends AbstractGameSite {

    private PlayerPerformAction playerPerformAction = this::performAction;

    //player has to perform this action
    @PackageReceiver
    public void received(NT_PlayerAction action) {
        if (action.text != null) {
            //show instruction message to player

        }
        state.performAction(action.action, playerPerformAction);
    }

    public void performAction(NT_Action action, int option, int[] options) {
        NT_Reaction reaction = new NT_Reaction();
        reaction.actionId = action.actionId;
        reaction.option = option;
        reaction.options = options;
        client.sendTCP(reaction);
    }
}
