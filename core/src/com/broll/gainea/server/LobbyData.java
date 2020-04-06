package com.broll.gainea.server;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.net.NT_LobbySettings;
import com.broll.networklib.server.impl.LobbySettings;

public class LobbyData implements LobbySettings {

    private ExpansionSetting expansionSetting = ExpansionSetting.BASIC_GAME;

    private GameContainer game;

    public void setGame(GameContainer game) {
        this.game = game;
    }

    public GameContainer getGame() {
        return game;
    }

    public void setExpansionSetting(ExpansionSetting expansionSetting) {
        this.expansionSetting = expansionSetting;
    }

    public ExpansionSetting getExpansionSetting() {
        return expansionSetting;
    }

    @Override
    public Object getSettings() {
        NT_LobbySettings settings =new NT_LobbySettings();
        settings.expansionSetting = expansionSetting.ordinal();
        return settings;
    }
}
