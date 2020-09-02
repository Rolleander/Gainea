package com.broll.gainea.client.sites;

import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_LoadedGame;
import com.broll.gainea.net.NT_StartGame;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.init.ExpansionSetting;
import com.broll.networklib.PackageReceiver;

public class GameStartSite extends AbstractGameSite {

    @PackageReceiver
    public void received(NT_StartGame start) {
        //start init
        ExpansionSetting setting = ExpansionSetting.values()[start.expansionsSetting];
        state.init(setting);
        state.update(start);
    }

    private void finishedLoading() {
        client.sendTCP(new NT_LoadedGame());
    }

}
