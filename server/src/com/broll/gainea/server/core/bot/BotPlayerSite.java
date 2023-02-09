package com.broll.gainea.server.core.bot;

import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_PlayerAction;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.PackageReceiver;
import com.broll.networklib.server.impl.BotSite;

import org.apache.commons.lang3.tuple.Pair;

public class BotPlayerSite extends BotSite<PlayerData> {

    private BotActionHandler botActionHandler;
    private NT_PlayerTurnActions currentTurn;

    public BotPlayerSite(BotActionHandler botActionHandler) {
        this.botActionHandler = botActionHandler;
    }

    @PackageReceiver
    public void turnStart(NT_PlayerTurnActions turn) {
        this.currentTurn = turn;
    }

    @PackageReceiver
    public void turnContinues(NT_PlayerTurnActions turn) {
        pickTurnAction();
    }

    @PackageReceiver
    public void handleAction(NT_PlayerAction action) {
        sendServer(botActionHandler.react(action.action));
    }

    private void pickTurnAction() {
        Pair<BotDecision, NT_Action> decision = botActionHandler.bestScore(currentTurn);
        NT_Action pickedAction = decision.getRight();
        //perform turn action
        sendServer(decision.getLeft().perform(pickedAction));
    }

}
