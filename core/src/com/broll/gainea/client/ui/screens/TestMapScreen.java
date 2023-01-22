package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.ui.Screen;
import com.broll.gainea.server.init.ExpansionSetting;

public class TestMapScreen extends Screen {

    public TestMapScreen() {
    }


    @Override
    public Actor build() {
//        ClientMapContainer.RENDER_DEBUG = true;
        game.state.init(ExpansionSetting.PLUS_ICELANDS, null);
        game.ui.initInGameUi();
        game.ui.inGameUI.show();
        game.state.getMap().displayRenders();
        return new Table();
    }
}
