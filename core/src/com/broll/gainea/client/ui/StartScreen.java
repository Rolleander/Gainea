package com.broll.gainea.client.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.broll.networklib.client.LobbyGameClient;
import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.client.impl.ILobbyDiscovery;
import com.broll.networklib.network.INetworkRequestAttempt;

import java.util.List;

public class StartScreen extends AbstractScreen {

    private TextField serverIp;
    private TextField name;
    private Table lobbies;
    private LobbyGameClient client;
    private LobbyListener lobbyListener = new LobbyListener();

    private TextButton discover, connect;
    private boolean connecting = false;
    private Widget loadingInfo;


    public StartScreen(LobbyGameClient client) {
        this.client = client;
    }

    private void joinLobby(GameLobby lobby) {
        String name = this.name.getText();
        if (name != null && name.trim().length() > 0) {
            client.connectToLobby(lobby, name, new INetworkRequestAttempt<GameLobby>() {
                @Override
                public void failure(String reason) {

                }

                @Override
                public void receive(GameLobby lobby) {
                    joinLobby(lobby);
                }
            });
        }
    }

    private void joinedLobby(GameLobby lobby) {

    }

    private class LobbyListener implements ILobbyDiscovery {

        @Override
        public void discovered(String serverIp, String serverName, List<GameLobby> lobbies) {
            lobbies.forEach(lobby -> {
                Table table = new Table(skin);
                table.setFillParent(true);
                table.setBackground("highlight");
                table.pad(10);
                table.add(info(lobby.getName())).padRight(50);
                table.add(info(serverName + " [" + serverIp + "]")).padRight(50);
                table.add(info(lobby.getPlayerCount() + " Players"));
                table.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        joinLobby(lobby);
                    }
                });
                StartScreen.this.lobbies.add(table);
                StartScreen.this.lobbies.row();
            });
        }

        @Override
        public void noLobbiesDiscovered() {
            StartScreen.this.lobbies.addActor(info("No Game found"));
            setConnecting(false);
        }

        @Override
        public void discoveryDone() {
            setConnecting(false);
        }
    }

    public void setConnecting(boolean connecting) {
        this.connecting = connecting;
        connect.setDisabled(connecting);
        //   discover.setDisabled(connecting);
        if (connecting) {
            lobbies.addActor(loadingInfo);
        } else {
            lobbies.removeActor(loadingInfo);
        }
    }

    @Override
    public Actor build() {
        loadingInfo = info("Connecting...");
        serverIp = new TextField("localhost", skin);
        name = new TextField("", skin);
        Table vg = new Table();
        vg.setFillParent(true);
        vg.setBackground(new TextureRegionDrawable(new Texture("textures/title.png")));
        vg.center();
        Table table = new Table(skin);
        table.setBackground("window");
        table.pad(30, 30, 10, 30);
        table.add(label("Playername")).padRight(20);
        table.add(name).padRight(100);
        table.add(label("Server IP")).padRight(20);
        table.add(serverIp);
        connect = new TextButton("Connect", skin);
        connect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                lobbies.clear();
                setConnecting(true);
                client.listLobbies(serverIp.getText(), lobbyListener);
                client.checkForReconnection(serverIp.getText(), StartScreen.this::joinedLobby);
            }
        });
        table.add(connect).align(Align.center).colspan(2);
        table.row().padTop(20);

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
        vg.add(new Image(new Texture("textures/logo.png"))).padTop(-300);
        vg.row();
        vg.add(table);

        vg.row();
        return vg;
    }


}
