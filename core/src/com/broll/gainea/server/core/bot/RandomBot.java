package com.broll.gainea.server.core.bot;

import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.net.NT_Action_SelectChoice;
import com.broll.gainea.net.NT_EndTurn;
import com.broll.gainea.net.NT_LoadedGame;
import com.broll.gainea.net.NT_PlayerAction;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.net.NT_StartGame;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.PackageReceiver;
import com.broll.networklib.server.impl.BotSite;

public class RandomBot extends BotSite<PlayerData> {

    @PackageReceiver
    public void gameStart(NT_StartGame start) {
        receive(new NT_LoadedGame());
    }

    @PackageReceiver
    public void turnOptions(NT_PlayerTurnActions actions) {
        //just ends his turn
        receive(new NT_EndTurn());
    }

    @PackageReceiver
    public void handleAction(NT_PlayerAction requiredAction) {
        //picks random option
        NT_Action action = requiredAction.action;
        NT_Reaction reaction = new NT_Reaction();
        reaction.actionId = action.actionId;
        if (action instanceof NT_Action_PlaceUnit) {
            int options = ((NT_Action_PlaceUnit) action).possibleLocations.length;
            reaction.option = MathUtils.random(0, options - 1);
        } else if (action instanceof NT_Action_SelectChoice) {
            int options = 1;
            if (((NT_Action_SelectChoice) action).objectChoices != null) {
                options = ((NT_Action_SelectChoice) action).objectChoices.length;
            } else {
                options = ((NT_Action_SelectChoice) action).choices.length;
            }
            reaction.option = MathUtils.random(0, options - 1);
        }
        receive(reaction);
    }

}
