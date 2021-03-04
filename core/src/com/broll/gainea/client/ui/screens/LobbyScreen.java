package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.AbstractScreen;
import com.broll.gainea.client.ui.components.GameChat;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.client.ui.ingame.windows.FractionWindow;
import com.broll.gainea.net.NT_LobbySettings;
import com.broll.gainea.net.NT_PlayerChangeFraction;
import com.broll.gainea.net.NT_PlayerReady;
import com.broll.gainea.net.NT_PlayerSettings;
import com.broll.gainea.net.NT_UpdateLobbySettings;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionFactory;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.init.ExpansionSetting;
import com.broll.gainea.server.init.GoalTypes;
import com.broll.networklib.client.impl.GameLobby;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

public class LobbyScreen extends AbstractScreen {
    private final static Logger Log = LoggerFactory.getLogger(LobbyScreen.class);

    private GameLobby lobby;
    private Table lobbyTable;
    private CheckBox ready;
    private SelectBox fratcionBox, expansions, goals, points, startGoals, startLocations, monsters;
    private Fraction playerFraction;

    public LobbyScreen(GameLobby lobby) {
        this.lobby = lobby;
    }

    private void updateSelection(SelectBox box, int index) {
        if (box.getSelectedIndex() != index) {
            box.setSelectedIndex(index);
        }
    }

    public void updateLobby() {
        NT_LobbySettings lobbySettings = (NT_LobbySettings) lobby.getSettings();
        updateSelection(expansions, lobbySettings.expansionSetting);
        updateSelection(goals, lobbySettings.goalTypes);
        updateSelection(points, lobbySettings.pointLimit - 1);
        updateSelection(startGoals, lobbySettings.startGoals - 1);
        updateSelection(startLocations, lobbySettings.startLocations - 1);
        updateSelection(monsters, lobbySettings.monsters);
        lobbyTable.clear();
        lobby.getPlayers().forEach(player -> {
            NT_PlayerSettings settings = (NT_PlayerSettings) player.getSettings();
            Fraction fraction = FractionFactory.create(FractionType.values()[settings.fraction]);
            lobbyTable.add(label(player.getName()));
            lobbyTable.add(new Image(TextureUtils.unitIcon(game, fraction.createCommander().getIcon()))).size(41).spaceRight(10);
            if (player == lobby.getMyPlayer()) {
                this.playerFraction = fraction;
                lobbyTable.add(fratcionBox);
                lobbyTable.add(ready).right().expandX();
            } else {
                CheckBox playerReady = new CheckBox("Bereit", skin);
                playerReady.setChecked(settings.ready);
                playerReady.setDisabled(true);
                lobbyTable.add(info(fraction.getType().getName()));
                lobbyTable.add(playerReady).right().expandX();
            }
            lobbyTable.row();
        });
        lobbyTable.pack();
    }

    private CheckBox playerReady() {
        CheckBox ready = new CheckBox("Bereit", skin);
        ready.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioPlayer.playSound("button.ogg");
                NT_PlayerReady nt = new NT_PlayerReady();
                nt.ready = ready.isChecked();
                lobby.sendTCP(nt);
            }
        });
        return ready;
    }

    private SelectBox fractionSelectBox() {
        int[] takenFractions = lobby.getPlayers().stream().mapToInt(p -> ((NT_PlayerSettings) p.getSettings()).fraction).toArray();
        SelectBox selectBox = new SelectBox(skin);
        selectBox.setItems(Arrays.stream(FractionType.values()).map(FractionType::getName).toArray());
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioPlayer.playSound("button.ogg");
                if (ArrayUtils.contains(takenFractions, selectBox.getSelectedIndex())) {
                    //fraction taken, reset selection
                    selectBox.setSelectedIndex(playerFraction.getType().ordinal());
                } else {
                    NT_PlayerChangeFraction changeFraction = new NT_PlayerChangeFraction();
                    changeFraction.fraction = selectBox.getSelectedIndex();
                    lobby.sendTCP(changeFraction);
                }
            }
        });
        return selectBox;
    }

    private SelectBox lobbySettingsBox(int setting, int indexDelta, String[] values) {
        SelectBox selectBox = new SelectBox(skin);
        selectBox.setItems(values);
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioPlayer.playSound("button.ogg");
                NT_UpdateLobbySettings nt = new NT_UpdateLobbySettings();
                nt.setting = setting;
                nt.value = selectBox.getSelectedIndex() + indexDelta;
                lobby.sendTCP(nt);
            }
        });
        return selectBox;
    }

    private Button addFractionWindowButton() {
        FractionWindow fractionWindow = new FractionWindow(game, skin);
        Button showFractions = TableUtils.textButton(skin, "Fraktionen", () -> {
            fractionWindow.toFront();
            fractionWindow.setVisible(!fractionWindow.isVisible());
        });
        Table pop = new Table();
        pop.setFillParent(true);
        pop.add(fractionWindow).expand().fill().center();
        fractionWindow.setVisible(false);
        game.uiStage.addActor(fractionWindow);
        return showFractions;
    }

    private String[] numbers(int to) {
        String[] numbers = new String[to];
        for (int i = 1; i <= to; i++) {
            numbers[i - 1] = "" + i;
        }
        return numbers;
    }

    @Override
    public Actor build() {
        ready = playerReady();
        fratcionBox = fractionSelectBox();
        expansions = lobbySettingsBox(NT_UpdateLobbySettings.SETTING_EXPANSIONS, 0, Arrays.stream(ExpansionSetting.values()).map(ExpansionSetting::getName).toArray(String[]::new));
        goals = lobbySettingsBox(NT_UpdateLobbySettings.SETTING_GOAL_TYPES, 0, Arrays.stream(GoalTypes.values()).map(GoalTypes::getName).toArray(String[]::new));
        points = lobbySettingsBox(NT_UpdateLobbySettings.SETTING_POINT_LIMIT, 1, numbers(20));
        startGoals = lobbySettingsBox(NT_UpdateLobbySettings.SETTING_START_GOALS, 1, numbers(6));
        startLocations = lobbySettingsBox(NT_UpdateLobbySettings.SETTING_START_LOCATIONS, 1, numbers(10));
        monsters = lobbySettingsBox(NT_UpdateLobbySettings.SETTING_MONSTERS, 0, ArrayUtils.add(numbers(20), 0, "0"));
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
        window.add(addFractionWindowButton()).right().row();
        Table settings = new Table(skin);
        settings.defaults().padRight(20).left();
        settings.add(info("Karten:"));
        settings.add(expansions);
        settings.add(info("Zielarten:"));
        settings.add(goals);
        settings.add(info("Punktelimit:"));
        settings.add(points);
        window.add(settings).left().spaceTop(2).padTop(2).spaceBottom(2).spaceBottom(2).row();
        settings = new Table(skin);
        settings.defaults().padRight(20).left();
        settings.add(info("Spielbeginn:"));
        settings.add(info("Ziele"));
        settings.add(startGoals);
        settings.add(info("Startflächen:"));
        settings.add(startLocations);
        settings.add(info("Monster pro Karte:"));
        settings.add(monsters);
        window.add(settings).left().spaceTop(2).padTop(2).spaceBottom(2).spaceBottom(2).row();
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
        window.add(new GameChat(skin, lobby)).colspan(2).expandX().fillX();
        vg.add(window).expand().fill();
        return vg;
    }

}
