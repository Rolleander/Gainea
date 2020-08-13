package com.broll.gainea.client.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.broll.networklib.client.LobbyGameClient;
import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.client.impl.LobbyPlayer;

import java.util.List;

public class GameUI {

    private Stage stage;
    private Skin skin;
    private LobbyGameClient client;

    public GameUI(Stage stage, LobbyGameClient client) {
        this.stage = stage;
        this.client = client;
        skin = new Skin(Gdx.files.internal("ui/cloud-form-ui.json"));
//        showScreen(new StartScreen());
        showScreen(new TestScreen());
    }

    public void showScreen(AbstractScreen screen) {
        stage.clear();
        screen.init(skin, client, this);
        stage.addActor(screen.build());
    }



}
