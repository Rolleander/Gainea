package com.broll.gainea.client.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.net.NT_PlayerSettings;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.networklib.client.impl.ChatMessageListener;
import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.client.impl.LobbyPlayer;
import com.broll.networklib.client.impl.LobbyUpdateListener;

public class LobbyScreen extends AbstractScreen {

    private GameLobby lobby;
    private Table lobbyTable;

    public LobbyScreen(GameLobby lobby) {
        this.lobby = lobby;
        lobby.setLobbyUpdateListener(new LobbyUpdateListener() {
            @Override
            public void lobbyUpdated() {
                updateLobby();
            }

            @Override
            public void playerJoined(LobbyPlayer player) {
                updateLobby();
            }

            @Override
            public void playerLeft(LobbyPlayer player) {
                updateLobby();
            }

            @Override
            public void kickedFromLobby() {
                backToTitle();
            }

            @Override
            public void closed() {
                backToTitle();
            }
        });
        lobby.setChatMessageListener(new ChatMessageListener() {
            @Override
            public void fromPlayer(String msg, LobbyPlayer from) {
            }

            @Override
            public void fromGame(String msg) {

            }
        });
    }


    private void updateLobby() {
        lobbyTable.clear();
        lobby.getPlayers().forEach(player -> {
            Table table = new Table(skin);
            table.setFillParent(true);
            table.setBackground("highlight");
            table.pad(10);
            NT_PlayerSettings settings = (NT_PlayerSettings) player.getSettings();
            table.add(info(player.getName())).padRight(50);
            table.add(info(FractionType.values()[settings.fraction].getName())).padRight(50);
            table.add(info(settings.ready + ""));
            lobbyTable.add(table).row();
        });
        lobbyTable.pack();
    }

    @Override
    public Actor build() {
        Table vg = new Table();
        vg.setFillParent(true);
        vg.center();
        vg.setBackground(new TextureRegionDrawable(new Texture("textures/title.png")));

        Table window = new Table(skin);
        window.setFillParent(true);
        window.setBackground("window");
        window.pad(25, 10, 10, 5);
        window.add(title(lobby.getName())).row();

        lobbyTable = new Table(skin);
        lobbyTable.center();


        window.add(lobbyTable).fill().expandX().row();

        window.center();
        window.pack();
        vg.add(window);
        vg.pack();
        updateLobby();
        return vg;
    }
}
