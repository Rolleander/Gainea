package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.game.GameState;
import com.broll.gainea.client.ui.AbstractScreen;

public class GameScreen extends AbstractScreen {

    @Override
    public Actor build() {
        GameState state = game.state;
        game.ui.initInGameUi();
        game.ui.inGameUI.show();
        state.getMap().getRenders().forEach(render -> game.gameStage.addActor(render));
        game.ui.inGameUI.updateWindows();
        return new Table();
    }
}
