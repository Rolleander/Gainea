package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.client.ui.Screen;
import com.broll.gainea.client.ui.components.StatisticGraph;
import com.broll.gainea.client.ui.components.TabbedPane;
import com.broll.gainea.client.ui.ingame.windows.PlayerWindow;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class TestScoreScreen extends Screen {
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
        window.add(title("Results")).left();
        window.row();

        Table lobbyTable = new Table(skin);
        lobbyTable.top();
        lobbyTable.left();
        lobbyTable.setBackground("menu-bg");

        lobbyTable.add(info("")).padRight(100);
        PlayerWindow.header(game, lobbyTable);

        lobbyTable.row();
        NT_Player player = new NT_Player();
        player.name = "Franz";
        player.units =new NT_Unit[0];
        lobbyTable.add(info("1."));
        PlayerWindow.row(game, lobbyTable, player);

        Table statistics = new Table();
        statistics.defaults().pad(5).space(5);
        statistics.add(label("Einheiten"));
        statistics.add(label("Punkte")).row();
        statistics.add(graph()).expand().fill();
        statistics.add(graph()).expand().fill().row();
        statistics.add(label("Einheiten"));
        statistics.add(label("Punkte")).row();
        statistics.add(graph()).expand().fill();
        statistics.add(graph()).expand().fill().row();
        statistics.add(new StatisticGraph.PlayerLegend(game,new String[]{"Hnas","Peter","Bouncer","Slicer","w","fd","gfd","hg","df"})).colspan(2).expandX().fillX().height(25);

        List<Pair<String, Actor>> tabs = Lists.newArrayList(Pair.of("Lobby", lobbyTable),Pair.of("Statistik", statistics));
        window.add(new TabbedPane(skin,tabs)).fill().expand().colspan(2).row();
        vg.add(window).expand().fill();
        return vg;
    }

    private StatisticGraph graph(){
        return new StatisticGraph(game,new int[][]{
                 rand(),rand(),rand(),rand(), rand(),rand(),rand(),rand(),rand()
        });
    }

    private int[] rand(){
        int[] f = new int[25];
        for(int i=0; i<f.length; i++){
            f[i] = MathUtils.random(6,10);
        }
        return f;
    }

}
