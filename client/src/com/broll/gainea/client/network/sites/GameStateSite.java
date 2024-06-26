package com.broll.gainea.client.network.sites;

import com.broll.gainea.client.ui.screens.GameScreen;
import com.broll.gainea.client.ui.screens.LobbyScreen;
import com.broll.gainea.net.NT_GameOver;
import com.broll.gainea.net.NT_GameStatistic;
import com.broll.gainea.net.NT_Lib;
import com.broll.gainea.net.NT_LoadedGame;
import com.broll.gainea.net.NT_ReconnectGame;
import com.broll.gainea.net.NT_StartGame;
import com.broll.gainea.server.init.ExpansionSetting;
import com.broll.networklib.PackageReceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class GameStateSite extends AbstractGameSite {

    private final static Logger Log = LoggerFactory.getLogger(GameStateSite.class);

    @PackageReceiver
    public void received(NT_Lib lib) {
        game.state.library = lib;
        if (game.ui.inGameUI != null) {
            game.ui.inGameUI.updateWindows();
        }
        if (game.ui.getCurrentScreen() instanceof LobbyScreen) {
            ((LobbyScreen) game.ui.getCurrentScreen()).updateLobby();
        }
    }

    @PackageReceiver
    public void received(NT_StartGame start) {
        //start init
        start(start);
    }

    @PackageReceiver
    public void received(NT_ReconnectGame reconnectGame) {
        Log.info("player reconnected!");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        start(reconnectGame);
        //add goals
        game.state.getGoals().addAll(Arrays.asList(reconnectGame.goals));
        game.ui.inGameUI.updateWindows();
    }

    @PackageReceiver
    public void received(NT_GameOver end) {
        game.ui.inGameUI.hideWindows();
        game.state.update(end);
        game.state.playerTurnEnded();
        game.ui.inGameUI.gameOver(end);
    }

    @PackageReceiver
    public void received(NT_GameStatistic nt) {
        game.state.setStatistic(nt);
    }

    private void start(NT_StartGame start) {
        ExpansionSetting setting = ExpansionSetting.values()[start.expansionsSetting];
        game.state.init(setting, start.pointLimit, start.roundLimit, getPlayer());
        game.state.getCards().addAll(Arrays.asList(start.cards));
        game.state.shop = start.shop;
        //switch to game screen
        game.state.update(start);
        game.ui.showScreen(new GameScreen());
        game.ui.inGameUI.updateWindows();
        //player has loaded
        finishedLoading();
    }

    private void finishedLoading() {
        client.sendTCP(new NT_LoadedGame());
    }

}
