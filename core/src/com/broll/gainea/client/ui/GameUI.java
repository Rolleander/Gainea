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

public class GameUI {

    private Stage stage;
    private Skin skin;

    public GameUI(Stage stage, LobbyGameClient client) {
        this.stage = stage;
        skin = new Skin(Gdx.files.internal("ui/cloud-form-ui.json"));
        showScreen(new StartScreen(client));
    }

    public void showScreen(AbstractScreen screen) {
        stage.clear();
        screen.init(skin);
        stage.addActor(screen.build());
    }

}
