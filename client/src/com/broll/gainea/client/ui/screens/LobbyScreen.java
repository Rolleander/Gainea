package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.Screen;
import com.broll.gainea.client.ui.components.GameChat;
import com.broll.gainea.client.ui.components.IconLabel;
import com.broll.gainea.client.ui.ingame.windows.FractionWindow;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_AddBot;
import com.broll.gainea.net.NT_LobbySettings;
import com.broll.gainea.net.NT_PlayerChangeFraction;
import com.broll.gainea.net.NT_PlayerReady;
import com.broll.gainea.net.NT_PlayerSettings;
import com.broll.gainea.net.NT_UpdateLobbySettings;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.init.ExpansionSetting;
import com.broll.gainea.server.init.GoalTypes;
import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.client.impl.LobbyPlayer;
import com.broll.networklib.network.nt.NT_LobbyKick;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.IntFunction;

public class LobbyScreen extends Screen {
    private final static Logger Log = LoggerFactory.getLogger(LobbyScreen.class);

    private GameLobby lobby;
    private Table lobbyTable;
    private CheckBox ready;
    private SelectBox fratcionBox, expansions, goals, points, rounds, startGoals, startLocations, monsters;
    private Fraction playerFraction;
    private boolean updatesRunning = false;
    private int[] roundOptions = new int[]{0, 15, 20, 25, 30, 35, 40, 50, 60};

    public LobbyScreen(GameLobby lobby) {
        this.lobby = lobby;
    }

    private void updateSelection(SelectBox box, int index) {
        if (box.getSelectedIndex() != index) {
            box.setSelectedIndex(index);
        }
    }

    private void leaveLobby() {
        lobby.leave();
        game.ui.showStartScreen();
    }

    public void updateLobby() {
        updatesRunning = true;
        NT_LobbySettings lobbySettings = (NT_LobbySettings) lobby.getSettings();
        updateSelection(expansions, lobbySettings.expansionSetting);
        updateSelection(goals, lobbySettings.goalTypes);
        updateSelection(points, lobbySettings.pointLimit);
        updateSelection(startGoals, lobbySettings.startGoals - 1);
        updateSelection(startLocations, lobbySettings.startLocations - 1);
        updateSelection(monsters, lobbySettings.monsters);
        updateSelection(rounds, ArrayUtils.indexOf(roundOptions, lobbySettings.roundLimit));
        lobbyTable.clear();
        lobby.getPlayers().forEach(player -> {
            NT_PlayerSettings settings = (NT_PlayerSettings) player.getSettings();
            Fraction fraction = FractionType.values()[settings.fraction].create();
            if (player == lobby.getOwner()) {
                lobbyTable.add(new IconLabel(game, 6, ""));
            } else {
                lobbyTable.add(info(" "));
            }
            lobbyTable.add(label(player.getName()));
            lobbyTable.add(new Image(TextureUtils.unitIcon(game, fraction.getDescription().getCommander().getIcon()))).size(41).spaceRight(10);
            if (player == lobby.getMyPlayer()) {
                this.playerFraction = fraction;
                if (fratcionBox.getSelectedIndex() != settings.fraction) {
                    fratcionBox.setSelectedIndex(settings.fraction);
                }
                if (ready.isChecked() != settings.ready) {
                    ready.setChecked(settings.ready);
                }
                lobbyTable.add(fratcionBox);
                lobbyTable.add(ready).right().expandX();
            } else {
                CheckBox playerReady = new CheckBox("Bereit", skin);
                playerReady.setChecked(settings.ready);
                playerReady.setDisabled(true);
                lobbyTable.add(info(fraction.getType().getDisplayName()));
                lobbyTable.add(playerReady).right().expandX();
                if (lobby.getOwner() == lobby.getMyPlayer()) {
                    ImageButton button = new ImageButton(new TextureRegionDrawable(TextureUtils.icon(game, 3)));
                    button.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            kickPlayer(player);
                            button.remove();
                        }
                    });
                    lobbyTable.add(button).padLeft(20);
                }
            }
            lobbyTable.row();
        });
        lobbyTable.pack();
        updatesRunning = false;
    }

    private void kickPlayer(LobbyPlayer player) {
        NT_LobbyKick nt = new NT_LobbyKick();
        nt.player = player.getId();
        lobby.sendTCP(nt);
    }

    private CheckBox playerReady() {
        CheckBox ready = new CheckBox("Bereit", skin);
        ready.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (updatesRunning) return;
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
        selectBox.setItems(Arrays.stream(FractionType.values()).map(FractionType::getDisplayName).toArray());
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (updatesRunning) return;
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

    private SelectBox lobbySettingsBox(int setting, IntFunction<Integer> mapping, String[] values) {
        SelectBox selectBox = new SelectBox(skin);
        selectBox.setItems((Object[]) values);
        if (lobby.getOwner() == lobby.getMyPlayer()) {
            selectBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (updatesRunning) return;
                    AudioPlayer.playSound("button.ogg");
                    NT_UpdateLobbySettings nt = new NT_UpdateLobbySettings();
                    nt.setting = setting;
                    nt.value = mapping.apply(selectBox.getSelectedIndex());
                    lobby.sendTCP(nt);
                }
            });
        } else {
            selectBox.setDisabled(true);
        }
        return selectBox;
    }

    private Button addFractionWindowButton() {
        FractionWindow fractionWindow = new FractionWindow(game);
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

    private String[] zeroNumbers(int to) {
        String[] numbers = new String[to + 1];
        for (int i = 0; i <= to; i++) {
            numbers[i] = "" + i;
        }
        return numbers;
    }


    @Override
    public Actor build() {
        AudioPlayer.stopMusic();
        ready = playerReady();
        fratcionBox = fractionSelectBox();
        expansions = lobbySettingsBox(NT_UpdateLobbySettings.SETTING_EXPANSIONS, i -> i, Arrays.stream(ExpansionSetting.values()).map(ExpansionSetting::getName).toArray(String[]::new));
        goals = lobbySettingsBox(NT_UpdateLobbySettings.SETTING_GOAL_TYPES, i -> i, Arrays.stream(GoalTypes.values()).map(GoalTypes::getGoalName).toArray(String[]::new));
        points = lobbySettingsBox(NT_UpdateLobbySettings.SETTING_POINT_LIMIT, i -> i, zeroNumbers(20));
        startGoals = lobbySettingsBox(NT_UpdateLobbySettings.SETTING_START_GOALS, i -> i + 1, numbers(6));
        startLocations = lobbySettingsBox(NT_UpdateLobbySettings.SETTING_START_LOCATIONS, i -> i + 1, numbers(10));
        monsters = lobbySettingsBox(NT_UpdateLobbySettings.SETTING_MONSTERS, i -> i, zeroNumbers(20));
        rounds = lobbySettingsBox(NT_UpdateLobbySettings.SETTING_ROUND_LIMIT, i -> roundOptions[i], Arrays.stream(roundOptions).mapToObj(Integer::toString).toArray(String[]::new));
        Table vg = new Table();
        vg.setFillParent(true);
        vg.center();
        vg.setBackground(new TextureRegionDrawable(new Texture("textures/title.jpg")));
        vg.pad(50, 50, 50, 50);
        Table window = new Table(skin);
        window.pad(30, 20, 10, 20);
        window.defaults().space(15);
        window.setBackground("window");
        window.top();
        Table title = new Table();
        title.add(title(lobby.getServerIp() + ": " + lobby.getName()));
        title.add(TableUtils.textButton(skin, "Verlassen", this::leaveLobby)).padLeft(30);
        window.add(title).left();
        window.add(addFractionWindowButton()).right().row();
        Table settings = new Table(skin);
        settings.defaults().padRight(20).left();
        settings.add(info("Karten:"));
        settings.add(expansions);
        settings.add(info("Zielarten:"));
        settings.add(goals);
        settings.add(info("Zielpunkte:"));
        settings.add(points);
        settings.add(info("Rundenziel:"));
        settings.add(rounds);
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
        if (lobby.getMyPlayer() == lobby.getOwner()) {
            settings.add(TableUtils.textButton(skin, "+Bot", () -> lobby.sendTCP(new NT_AddBot()))).padLeft(100);
        }
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
