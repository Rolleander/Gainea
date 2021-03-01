package com.broll.gainea.client.network.sites;

import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.ui.utils.MessageUtils;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_EndTurn;
import com.broll.gainea.net.NT_PlayerTurnStart;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_PlayerWait;
import com.broll.networklib.PackageReceiver;
import com.broll.networklib.client.impl.LobbyPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameTurnSite extends AbstractGameSite {

    private GameActionSite gameActionSite;
    private List<NT_Action> actions = new ArrayList<>();
    private PlayerPerformOptionalAction performedAction = new PlayerPerformOptionalAction() {
        @Override
        public void none() {
            //player wants to end turn
            client.sendTCP(new NT_EndTurn());
        }

        @Override
        public void perform(NT_Action action, int option, int[] options) {
            //player selected action, send reaction
            gameActionSite.performAction(action, option, options);
        }
    };

    public GameTurnSite(GameActionSite gameActionSite) {
        this.gameActionSite = gameActionSite;
    }

    @PackageReceiver
    public void received(NT_PlayerTurnStart turnStart) {
        game.state.playerTurnStart();
        game.ui.inGameUI.getRoundInformation().setPlayer(getPlayer().getName());
        turnStartMessage(getPlayer());
    }

    @PackageReceiver
    public void received(NT_PlayerTurnActions turnContinue) {
        actions.clear();
        game.state.turnIdle();
        game.ui.inGameUI.getRoundInformation().setPlayer(getPlayer().getName());
        actions.addAll(Arrays.asList(turnContinue.actions));
        playerPickAction();
        game.ui.inGameUI.getBattleHandler().clearBattleScreen();
    }

    @PackageReceiver
    public void received(NT_EndTurn endTurn) {
        //no more actions, player can only end turn now
        actions.clear();
        game.state.turnIdle();
        playerPickAction();
    }

    @PackageReceiver
    public void received(NT_PlayerWait wait) {
        //other players turn
        game.state.playerTurnEnded();
        LobbyPlayer otherPlayer = getLobby().getPlayer(wait.playersTurn);
        game.ui.inGameUI.getRoundInformation().setPlayer(otherPlayer.getName());
        actions.clear();
        turnStartMessage(otherPlayer);

    }

    private void turnStartMessage(LobbyPlayer turnPlayer) {
        String message = "";
        if (turnPlayer == getPlayer()) {
            //your turn
            message = "Du bist dran!";
        } else {
            //other players turn
            message = turnPlayer.getName() + "'s Zug!";
        }
        game.ui.inGameUI.hideWindows();
        MessageUtils.showCenterMessage(game, message);
    }

    private void playerPickAction() {
        game.state.performOptionalAction(actions, performedAction);
    }

}
