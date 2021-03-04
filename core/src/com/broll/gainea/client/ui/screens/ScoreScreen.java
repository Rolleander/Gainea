package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.client.ui.AbstractScreen;
import com.broll.gainea.client.ui.components.GameChat;
import com.broll.gainea.net.NT_GameOver;
import com.broll.gainea.net.NT_GameStatistic;
import com.broll.networklib.client.impl.GameLobby;

import org.apache.commons.lang3.StringUtils;

public class ScoreScreen extends AbstractScreen {

    private NT_GameOver end;

    public ScoreScreen(NT_GameOver end) {
        this.end = end;
    }

    @Override
    public Actor build() {
        NT_GameStatistic statistic = game.state.getStatistic();
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
        lobbyTable.pad(15);
        lobbyTable.defaults().space(5);
        lobbyTable.defaults().left();
        lobbyTable.defaults().spaceRight(100);
        window.add(lobbyTable).fill().expand().colspan(2).row();
        window.add(new GameChat(skin, this.game.client.getClient().getConnectedLobby())).colspan(2).expandX().fillX();
        vg.add(window).expand().fill();
        return vg;
    }

}
