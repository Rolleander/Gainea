package com.broll.gainea.client.network.sites;

import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.networklib.PackageReceiver;

public class GameBoardSite extends AbstractGameSite {

    @PackageReceiver
    public void received(NT_BoardUpdate update) {
        game.ui.inGameUI.getBattleHandler().clearBattleScreen();
        game.state.update(update);
        game.ui.inGameUI.updateWindows();
    }

}
