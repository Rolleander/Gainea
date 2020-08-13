package com.broll.gainea.server.core.bot;

import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_PlayerAction;
import com.broll.gainea.net.NT_PlayerTurn;
import com.broll.gainea.net.NT_PlayerTurnContinue;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.PackageReceiver;
import com.broll.networklib.server.impl.BotSite;
import com.broll.networklib.server.impl.Player;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

public class BotPlayerSite extends BotSite<PlayerData> {

    private BotActionHandler botActionHandler;
    private NT_PlayerTurn currentTurn;

    public BotPlayerSite(BotActionHandler botActionHandler) {
        this.botActionHandler = botActionHandler;
    }

    @PackageReceiver
    void turnStart(NT_PlayerTurn turn) {
        this.currentTurn = turn;
        pickTurnAction();
    }

    @PackageReceiver
    void turnContinues(NT_PlayerTurnContinue turn) {
        pickTurnAction();
    }

    @PackageReceiver
    void handleAction(NT_PlayerAction action) {
        receive(botActionHandler.react(action.action));
    }

    private void pickTurnAction() {
        Pair<BotDecision, NT_Action> decision = botActionHandler.bestScore(currentTurn);
        NT_Action pickedAction = decision.getRight();
        if (pickedAction != null) {
            //remove picked action
            currentTurn.actions = ArrayUtils.removeElement(currentTurn.actions, pickedAction);
        }
        //perform turn action
        receive(decision.getLeft().perform(pickedAction));
    }

}
