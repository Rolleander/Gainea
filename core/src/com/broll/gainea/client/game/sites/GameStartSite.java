package com.broll.gainea.client.game.sites;

import com.broll.gainea.client.ui.screens.GameScreen;
import com.broll.gainea.net.NT_LoadedGame;
import com.broll.gainea.net.NT_ReconnectGame;
import com.broll.gainea.net.NT_StartGame;
import com.broll.gainea.server.init.ExpansionSetting;
import com.broll.networklib.PackageReceiver;

public class GameStartSite extends AbstractGameSite {

    @PackageReceiver
    public void received(NT_StartGame start) {
        //start init
        start(start);
    }

    @PackageReceiver
    public void received(NT_ReconnectGame reconnectGame) {
        start(reconnectGame);
        //add goals and cards
        //TODO
        //   reconnectGame.goals;
        //   reconnectGame.cards;
    }

    private void start(NT_StartGame start) {
        ExpansionSetting setting = ExpansionSetting.values()[start.expansionsSetting];
        game.state.init(setting);
        game.state.update(start);
        //switch to game screen
        game.ui.showScreen(new GameScreen());
        //player has loaded
        finishedLoading();
    }

    private void finishedLoading() {
        client.sendTCP(new NT_LoadedGame());
    }

}
