package com.broll.gainea.client.game.sites;

import com.broll.gainea.client.game.PlayerPerformAction;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.client.ui.elements.MessageUtils;
import com.broll.gainea.net.NT_Action;
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
            MessageUtils.showActionMessage(game, action.text);
        }
        game.state.performAction(action.action, playerPerformAction);
    }

    public void performAction(NT_Action action, int option, int[] options) {
        NT_Reaction reaction = new NT_Reaction();
        reaction.actionId = action.actionId;
        reaction.option = option;
        reaction.options = options;
        client.sendTCP(reaction);
    }
}
