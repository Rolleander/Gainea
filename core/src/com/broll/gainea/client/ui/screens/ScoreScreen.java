package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.Screen;
import com.broll.gainea.client.ui.components.ColorCircle;
import com.broll.gainea.client.ui.components.GameChat;
import com.broll.gainea.client.ui.components.IconLabel;
import com.broll.gainea.client.ui.components.StatisticGraph;
import com.broll.gainea.client.ui.components.TabbedPane;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.net.NT_GameOver;
import com.broll.gainea.net.NT_GameStatistic;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.fractions.FractionType;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Collectors;

public class ScoreScreen extends Screen {

    private NT_GameOver end;

    public ScoreScreen(NT_GameOver end) {
        this.end = end;
    }

    private int sortPlayers(NT_Player p2, NT_Player p1) {
        int c = Integer.compare(p1.points, p2.points);
        if (c == 0) {
            c = Integer.compare(p1.stars, p2.stars);
            if (c == 0) {
                c = Integer.compare(p1.units.length, p2.units.length);
            }
        }
        return c;
    }

    private Table lobbyTable() {
        Table window = new Table();
        Table lobbyTable = new Table(skin);
        lobbyTable.top();
        lobbyTable.left();
        lobbyTable.add(info("")).padRight(100);
        header(game, lobbyTable);
        lobbyTable.row();
        List<NT_Player> players = game.state.getPlayers().stream().sorted(this::sortPlayers).collect(Collectors.toList());
        for (int i = 0; i < players.size(); i++) {
            lobbyTable.add(info((i + 1) + "."));
            row(game, lobbyTable, players.get(i));
        }
        window.add(lobbyTable).row();
        window.add(new GameChat(skin, this.game.client.getClient().getConnectedLobby())).colspan(2).expandX().fillX();
        return window;
    }

    private Table statisticTable() {
        NT_GameStatistic statistic = game.state.getStatistic();
        int rounds = statistic.rounds.length;
        int players = game.state.getPlayers().size();
        int[][] total = new int[players][rounds];
        int[][] power = new int[players][rounds];
        int[][] locations = new int[players][rounds];
        int[][] units = new int[players][rounds];
        for (int r = 0; r < rounds; r++) {
            for (int p = 0; p < players; p++) {
                total[p][r] = statistic.rounds[r].total[p];
                power[p][r] = statistic.rounds[r].fightingPower[p];
                locations[p][r] = statistic.rounds[r].locations[p];
                units[p][r] = statistic.rounds[r].units[p];
            }
        }
        Table statistics = new Table();
        statistics.defaults().pad(5).space(5);
        statistics.add(label("Gesamt"));
        statistics.add(label("Kampfkraft")).row();
        statistics.add(new StatisticGraph(game, total)).expand().fill();
        statistics.add(new StatisticGraph(game, power)).expand().fill().row();
        statistics.add(label("Einheiten"));
        statistics.add(label("Gebiete")).row();
        statistics.add(new StatisticGraph(game, units)).expand().fill();
        statistics.add(new StatisticGraph(game, locations)).expand().fill().row();
        statistics.add(new StatisticGraph.PlayerLegend(game, game.state.getPlayers().stream().map(it -> it.name).toArray(String[]::new))).colspan(2).expandX().fillX().height(25);
        return statistics;
    }

    public static void header(Gainea game, Table content) {
        Skin skin = game.ui.skin;
        content.top().left();
        content.pad(10);
        int space = 30;
        content.add(LabelUtils.info(skin, "  ")).left();
        content.add(LabelUtils.info(skin, "Spieler")).padRight(space * 3).fillX().expandX().left();
        content.add(LabelUtils.info(skin, "Fraktion")).padRight(space * 3).left();
        content.add(LabelUtils.info(skin, "Punkte")).padRight(space).left();
        content.add(new IconLabel(game, 2, "")).padRight(space).left();
        content.add(LabelUtils.info(skin, "Karten")).padRight(space).left();
        content.add(LabelUtils.info(skin, "Einheiten")).padRight(space).left();
        content.add(new IconLabel(game, 0, "")).padRight(space).left();
        content.add(new IconLabel(game, 1, "")).left();
    }

    public static void row(Gainea game, Table content, NT_Player player) {
        Skin skin = game.ui.skin;
        content.add(new ColorCircle(game, player.color)).padLeft(10);
        content.add(LabelUtils.info(skin, player.name)).fillX().expandX();
        content.add(LabelUtils.info(skin, FractionType.values()[player.fraction].getName()));
        content.add(LabelUtils.info(skin, "" + player.points));
        content.add(LabelUtils.info(skin, "" + player.stars));
        content.add(LabelUtils.info(skin, "" + player.cards));
        content.add(LabelUtils.info(skin, "" + player.units.length)).padTop(15);
        int power = 0;
        int health = 0;
        int maxHealth = 0;
        for (NT_Unit unit : player.units) {
            power += unit.power;
            health += unit.health;
            maxHealth += unit.maxHealth;
        }
        content.add(LabelUtils.info(skin, "" + power));
        content.add(LabelUtils.info(skin, health + "/" + maxHealth)).row();
    }

    @Override
    public Actor build() {
        Table vg = new Table();
        vg.setFillParent(true);
        vg.center();
        vg.setBackground(new TextureRegionDrawable(new Texture("textures/title.jpg")));
        vg.pad(50, 50, 50, 50);
        Table window = new Table(skin);
        window.pad(30, 20, 10, 20);
        window.defaults().space(15);
        window.setBackground("window");
        window.setBackground("menu-bg");
        window.top();
        window.add(title("Results")).left();
        window.row();
        List<Pair<String, Actor>> tabs = Lists.newArrayList(Pair.of("Lobby", lobbyTable()), Pair.of("Statistik", statisticTable()));
        window.add(new TabbedPane(skin, tabs)).fill().expand().colspan(2).row();
        window.add(TableUtils.textButton(skin, "Verlassen",
                () -> game.ui.showScreen(new LobbyScreen(game.state.getPlayer().getLobby()))));
        vg.add(window).expand().fill();
        return vg;
    }

}
