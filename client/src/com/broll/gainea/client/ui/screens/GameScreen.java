package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.game.GameState;
import com.broll.gainea.client.ui.Screen;

public class GameScreen extends Screen {

    @Override
    public Actor build() {
        AudioPlayer.playSong("celtic.mp3");
        GameState state = game.state;
        game.ui.initInGameUi();
        game.ui.inGameUI.show();
        state.getMap().displayRenders();
        return new Table();
    }
}
