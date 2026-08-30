package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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
import com.broll.gainea.client.ui.ingame.windows.lib.LibraryWindow;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.core.fractions.FractionType;
import com.broll.gainea.core.goals.GoalTypes;
import com.broll.gainea.core.map.ExpansionSetting;
import com.broll.gainea.core.protocol.LobbySetting;
import com.broll.gainea.net.proto.NT_Lib_Fraction;
import com.broll.mpnll.client.lobby.Lobby;
import com.broll.mpnll.client.lobby.User;
import com.broll.mpnll.nt.NT_LobbyKick;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.IntFunction;

public class LobbyScreen extends Screen {
    private final static Logger Log = LoggerFactory.getLogger(LobbyScreen.class);
    private int playerFraction;
    private Lobby lobby;
    private Table lobbyTable;
    private CheckBox ready;
    private SelectBox fratcionBox, expansions, goals, points, rounds, startGoals, startLocations, monsters;
    private boolean updatesRunning = false;
    private int[] roundOptions = new int[]{0, 15, 20, 25, 30, 35, 40, 50, 60};

    private LibraryWindow libraryWindow;

    public LobbyScreen(Lobby lobby) {
        this.lobby = lobby;
    }

    private static boolean contains(int[] values, int value) {
        return indexOf(values, value) >= 0;
    }

    private static int indexOf(int[] values, int value) {
        for (int i = 0; i < values.length; i++) if (values[i] == value) return i;
        return -1;
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
        if (game.state.library == null) {
            return;
        }
        if (libraryWindow == null) {
            initLibraryWindow();
        }
        updatesRunning = true;
        com.broll.gainea.net.proto.NT_LobbySettings lobbySettings = (com.broll.gainea.net.proto.NT_LobbySettings) lobby.getSettings();
        updateSelection(expansions, lobbySettings.getExpansionSetting());
        updateSelection(goals, lobbySettings.getGoalTypes());
        updateSelection(points, lobbySettings.getPointLimit());
        updateSelection(startGoals, lobbySettings.getStartGoals() - 1);
        updateSelection(startLocations, lobbySettings.getStartLocations() - 1);
        updateSelection(monsters, lobbySettings.getMonsters());
        updateSelection(rounds, indexOf(roundOptions, lobbySettings.getRoundLimit()));
        lobbyTable.clear();
        lobby.getUsers().forEach(player -> {
            com.broll.gainea.net.proto.NT_PlayerSettings settings = (com.broll.gainea.net.proto.NT_PlayerSettings) player.getSettings();
            NT_Lib_Fraction fraction = game.state.library.getFractions(settings.getFraction());
            if (player == lobby.getOwner()) {
                lobbyTable.add(new IconLabel(game, 6, ""));
            } else {
                lobbyTable.add(info(" "));
            }
            lobbyTable.add(label(player.getName()));
            int icon = fraction.getCommander().getIcon();
            lobbyTable.add(new Image(TextureUtils.unitIcon(game, icon))).size(41).spaceRight(10);
            if (player.isMe()) {
                this.playerFraction = fraction.getIndex();
                if (fratcionBox.getSelectedIndex() != settings.getFraction()) {
                    fratcionBox.setSelectedIndex(settings.getFraction());
                }
                if (ready.isChecked() != settings.getReady()) {
                    ready.setChecked(settings.getReady());
                }
                lobbyTable.add(fratcionBox);
                lobbyTable.add(ready).right().expandX();
            } else {
                CheckBox playerReady = new CheckBox("Bereit", skin);
                playerReady.setChecked(settings.getReady());
                playerReady.setDisabled(true);
                lobbyTable.add(info(fraction.getName()));
                lobbyTable.add(playerReady).right().expandX();
                if (lobby.getOwner().isMe()) {
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

    private void kickPlayer(User player) {
        lobby.send(NT_LobbyKick.newBuilder().setPlayer(player.getId()).build());
    }

    private CheckBox playerReady() {
        CheckBox ready = new CheckBox("Bereit", skin);
        ready.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (updatesRunning) return;
                AudioPlayer.playSound("button.ogg");
                lobby.send(com.broll.gainea.net.proto.NT_PlayerReady.newBuilder().setReady(ready.isChecked()).build());
            }
        });
        return ready;
    }

    private SelectBox fractionSelectBox() {
        int[] takenFractions = lobby.getUsers().stream().mapToInt(p -> ((com.broll.gainea.net.proto.NT_PlayerSettings) p.getSettings()).getFraction()).toArray();
        SelectBox selectBox = new SelectBox(skin);
        selectBox.setItems(Arrays.stream(FractionType.values()).map(FractionType::getDisplayName).toArray());
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (updatesRunning) return;
                AudioPlayer.playSound("button.ogg");
                if (contains(takenFractions, selectBox.getSelectedIndex())) {
                    //fraction taken, reset selection
                    selectBox.setSelectedIndex(playerFraction);
                } else {
                    lobby.send(com.broll.gainea.net.proto.NT_PlayerChangeFraction.newBuilder().setFraction(selectBox.getSelectedIndex()).build());
                }
            }
        });
        return selectBox;
    }

    private SelectBox lobbySettingsBox(int setting, IntFunction<Integer> mapping, String[] values) {
        SelectBox selectBox = new SelectBox(skin);
        selectBox.setItems(values);
        if (lobby.getOwner().isMe()) {
            selectBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (updatesRunning) return;
                    AudioPlayer.playSound("button.ogg");
                    lobby.send(com.broll.gainea.net.proto.NT_UpdateLobbySettings.newBuilder().setSetting(setting).setValue(mapping.apply(selectBox.getSelectedIndex())).build());
                }
            });
        } else {
            selectBox.setDisabled(true);
        }
        return selectBox;
    }

    private void initLibraryWindow() {
        libraryWindow = new LibraryWindow(game);
        Table pop = new Table();
        pop.setFillParent(true);
        pop.add(libraryWindow).expand().fill().center();
        libraryWindow.setVisible(false);
        game.uiStage.addActor(libraryWindow);
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
        expansions = lobbySettingsBox(LobbySetting.EXPANSIONS.value, i -> i, Arrays.stream(ExpansionSetting.values()).map(ExpansionSetting::getName).toArray(String[]::new));
        goals = lobbySettingsBox(LobbySetting.GOAL_TYPES.value, i -> i, Arrays.stream(GoalTypes.values()).map(GoalTypes::getGoalName).toArray(String[]::new));
        points = lobbySettingsBox(LobbySetting.POINT_LIMIT.value, i -> i, zeroNumbers(20));
        startGoals = lobbySettingsBox(LobbySetting.START_GOALS.value, i -> i + 1, numbers(6));
        startLocations = lobbySettingsBox(LobbySetting.START_LOCATIONS.value, i -> i + 1, numbers(10));
        monsters = lobbySettingsBox(LobbySetting.MONSTERS.value, i -> i, zeroNumbers(20));
        rounds = lobbySettingsBox(LobbySetting.ROUND_LIMIT.value, i -> roundOptions[i], Arrays.stream(roundOptions).mapToObj(Integer::toString).toArray(String[]::new));
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
        window.add(TableUtils.textButton(skin, "Info", () -> {
            if (libraryWindow != null) {
                libraryWindow.toFront();
                libraryWindow.setVisible(!libraryWindow.isVisible());
            }
        })).right().row();
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
        if (lobby.getMyUser() == lobby.getOwner()) {
            settings.add(TableUtils.textButton(skin, "+Bot", () -> lobby.send(com.broll.gainea.net.proto.NT_AddBot.newBuilder().build()))).padLeft(100);
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
