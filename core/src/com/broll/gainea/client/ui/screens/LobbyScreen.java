package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.client.ui.AbstractScreen;
import com.broll.gainea.client.ui.components.GameChat;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.client.ui.ingame.windows.FractionWindow;
import com.broll.gainea.net.NT_PlayerChangeFraction;
import com.broll.gainea.net.NT_PlayerReady;
import com.broll.gainea.net.NT_PlayerSettings;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionFactory;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.networklib.client.auth.LastConnection;
import com.broll.networklib.client.impl.ChatMessageListener;
import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.client.impl.LobbyPlayer;
import com.broll.networklib.client.impl.LobbyUpdateListener;
import com.esotericsoftware.minlog.Log;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

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
                LastConnection.clear();
                backToTitle();
                game.ui.showErrorDialog("Kicked from lobby");
            }

            @Override
            public void closed() {
                LastConnection.clear();
                backToTitle();
                game.ui.showErrorDialog("Lobby closed");
            }

            @Override
            public void disconnected() {
                //disconnected is called on shutdown as well
                if (!game.shutdown) {
                    backToTitle();
                    game.ui.showErrorDialog("Connection problems");
                }
            }
        });
    }

    private void updateLobby() {
        lobbyTable.clear();
        lobby.getPlayers().forEach(player -> {
            lobbyTable.add(label(player.getName()));
            NT_PlayerSettings settings = (NT_PlayerSettings) player.getSettings();
            Fraction fraction = FractionFactory.create(FractionType.values()[settings.fraction]);
            lobbyTable.add(new Image(TextureUtils.unitIcon(game, fraction.createCommander().getIcon()))).size(41).spaceRight(10);
            CheckBox playerReady = new CheckBox("Bereit", skin);
            playerReady.setChecked(settings.ready);
            if (player == lobby.getMyPlayer()) {
                lobbyTable.add(fractionSelectBox(fraction));
                playerReady.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        NT_PlayerReady ready = new NT_PlayerReady();
                        ready.ready = playerReady.isChecked();
                        lobby.sendTCP(ready);
                    }
                });
            } else {
                playerReady.setDisabled(true);
                lobbyTable.add(info(fraction.getType().getName()));
            }
            lobbyTable.add(playerReady).right().expandX();
            lobbyTable.row();
        });
        lobbyTable.pack();
    }

    private SelectBox fractionSelectBox(Fraction fraction) {
        int[] takenFractions = lobby.getPlayers().stream().mapToInt(p -> ((NT_PlayerSettings) p.getSettings()).fraction).toArray();
        SelectBox selectBox = new SelectBox(skin);
        selectBox.setItems(Arrays.stream(FractionType.values()).map(FractionType::getName).toArray());
        selectBox.setSelectedIndex(fraction.getType().ordinal());
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (ArrayUtils.contains(takenFractions, selectBox.getSelectedIndex())) {
                    //fraction taken, reset selection
                    selectBox.setSelectedIndex(fraction.getType().ordinal());
                } else {
                    NT_PlayerChangeFraction changeFraction = new NT_PlayerChangeFraction();
                    changeFraction.fraction = selectBox.getSelectedIndex();
                    lobby.sendTCP(changeFraction);
                }
            }
        });
        return selectBox;
    }

    @Override
    public Actor build() {
        Table vg = new Table();
        vg.setFillParent(true);
        vg.center();
        vg.setBackground(new TextureRegionDrawable(new Texture("textures/title.png")));
        vg.pad(50, 50, 50, 50);
        Table window = new Table(skin);
        window.pad(30, 20, 10, 20);
        window.defaults().space(15);
        window.setBackground("window");
        window.top();
        window.add(title(lobby.getServerIp() + ": " + lobby.getName())).left();
        FractionWindow fractionWindow =new FractionWindow(game, skin);
        TextButton showFractions = new TextButton("Fraktionen",skin);
        showFractions.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Log.info("show");
                fractionWindow.toFront();
                fractionWindow.setVisible(!fractionWindow.isVisible());
                return true;
            }
        });
        Table pop = new Table();
        pop.setFillParent(true);
        pop.add(fractionWindow).expand().fill().center();
        fractionWindow.setVisible(false);
        game.uiStage.addActor(fractionWindow);
        window.add(showFractions).right();
        window.row();
        lobbyTable = new Table(skin);
        lobbyTable.top();
        lobbyTable.left();
        lobbyTable.setBackground("menu-bg");
        lobbyTable.pad(15);
        lobbyTable.defaults().space(5);
        lobbyTable.defaults().left();
        lobbyTable.defaults().spaceRight(100);
        updateLobby();
        window.add(lobbyTable).fill().expand().colspan(2).row();
        window.add(new GameChat(skin, lobby)).expandX().fillX();
        vg.add(window).expand().fill();
        return vg;
    }

}
