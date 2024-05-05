package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.Screen;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.client.tasks.ServerInformation;

public class StartScreen extends Screen {

    public static String PLAYER_NAME =
            "";
    public static String SERVER =
            "gainea.de";
    private TextField serverIp;
    private TextField name;
    private Table lobbies;
    private Button discover, connect;
    private boolean connecting = false;
    private Label loadingInfo;

    public StartScreen() {

    }

    private void joinLobby(GameLobby lobby) {
        String name = this.name.getText();
        if (name != null && name.trim().length() > 0) {
            PLAYER_NAME = name;
            SERVER = serverIp.getText();
            game.client.joinLobby(name, lobby);
        }
    }

    private void createLobby() {
        String name = this.name.getText();
        if (name != null && name.trim().length() > 0) {
            PLAYER_NAME = name;
            SERVER = serverIp.getText();
            game.client.createLobby(name);
        }
    }

    public void showLobbies(ServerInformation info) {
        this.lobbies.clear();
        info.getLobbies().forEach(lobby -> {
            Table table = new Table(skin);
            table.setFillParent(true);
            table.setBackground("highlight");
            table.pad(10);
            table.add(info(lobby.getName())).padRight(50);
            table.add(info(info.getServerName() + " [" + lobby.getServerIp() + "]")).padRight(50);
            table.add(info(lobby.getPlayerCount() + " Players"));
            table.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    AudioPlayer.playSound("button.ogg");
                    joinLobby(lobby);
                }
            });
            StartScreen.this.lobbies.add(table);
            StartScreen.this.lobbies.row();
        });
        if (info.getLobbies().isEmpty()) {
            this.lobbies.add(LabelUtils.label(skin, "Keine offenen Lobbies auf dem Server gefunden!")).row();
        }
        this.lobbies.add(TableUtils.textButton(skin, "Lobby erstellen", this::createLobby));
    }

    public void showInfo(String text) {
        loadingInfo.setText(text);
        loadingInfo.setVisible(true);
    }

    @Override
    public Actor build() {
        loadingInfo = info("");
        loadingInfo.setAlignment(Align.center);
        loadingInfo.setVisible(false);
        //"192.168.0.137"
        serverIp = new TextField(SERVER, skin);
        name = new TextField(PLAYER_NAME, skin);
        Table vg = new Table();
        vg.setFillParent(true);
        vg.setBackground(new TextureRegionDrawable(game.assets.get("textures/title.jpg", Texture.class)));
        vg.center();
        Table table = new Table(skin);
        table.setBackground("window");
        table.pad(30, 30, 10, 30);
        table.add(label("Playername")).padRight(20);
        table.add(name).padRight(100);
        table.add(label("Server")).padRight(20);
        table.add(serverIp);
        connect = TableUtils.textButton(skin, "Connect", () -> {
            game.uiStage.setKeyboardFocus(null);
            Gdx.input.setOnscreenKeyboardVisible(false);
            lobbies.clear();
            game.client.listLobbies(serverIp.getText());
        });
        table.add(connect).align(Align.center).colspan(2);
        table.row().padTop(20);
        table.add(loadingInfo).center();
        table.row();
        lobbies = new Table(skin);
        lobbies.center();
        table.add(lobbies).colspan(5);
        table.row().padTop(20);
    /*    discover = new TextButton("Discover servers", skin);
        discover.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                lobbies.clear();
                setConnecting(true);
                client.discoverLobbies(lobbyListener);
            }
        });
       table.add(discover).colspan(2).align(Align.left); */
        vg.add(new Image(game.assets.get("textures/logo.png", Texture.class))).padTop(-300);
        vg.row();
        vg.add(LabelUtils.markup(skin, "[WHITE]Version: " + Gainea.VERSION + "[]")).right().row();
        vg.add(table);
        vg.row();
        return vg;
    }


}
