package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.elements.ClosableWindow;
import com.broll.gainea.client.ui.elements.IconLabel;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.client.ui.elements.MenuUnit;
import com.broll.gainea.client.ui.elements.TableUtils;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionFactory;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.objects.BattleObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerWindow extends ClosableWindow {

    private Table content;

    public PlayerWindow(Gainea game, Skin skin) {
        super(game, "Spieler", skin);
        content = new Table();
        add(content).expand().fill();
        TableUtils.consumeClicks(this);
        update();
    }

    public void update() {
        List<NT_Player> players = game.state.getPlayers();
        if (players == null) {
            return;
        }
        center(850, players.size() * 100 + 50);
        content.clear();
        content.top().left();
        content.pad(10);
        int space = 30;
        content.add(LabelUtils.info(skin, "Spieler")).padRight(space * 3).fillX().expandX().left();
        content.add(LabelUtils.info(skin, "Fraktion")).padRight(space * 3).left();
        content.add(LabelUtils.info(skin, "Punkte")).padRight(space).left();
        content.add(new IconLabel(game, skin, 2, "")).padRight(space).left();
        content.add(LabelUtils.info(skin, "Karten")).padRight(space).left();
        content.add(LabelUtils.info(skin, "Einheiten")).padRight(space).left();
        content.add(new IconLabel(game, skin, 0, "")).padRight(space).left();
        content.add(new IconLabel(game, skin, 1, "")).left().row();
        players.stream().sorted((p2, p1) -> {
            int p = Integer.compare(p1.points, p2.points);
            if (p == 0) {
                return Integer.compare(p1.stars, p2.stars);
            }
            return p;
        }).forEach(player -> {
            int pad = 15;
            content.add(LabelUtils.info(skin, player.name)).padTop(15).fillX().expandX().left();
            content.add(LabelUtils.info(skin, FractionType.values()[player.fraction].getName())).padTop(15).left();
            content.add(LabelUtils.info(skin, "" + player.points)).padTop(15).left();
            content.add(LabelUtils.info(skin, "" + player.stars)).padTop(15).left();
            content.add(LabelUtils.info(skin, "" + player.cards)).padTop(15).left();
            content.add(LabelUtils.info(skin, "" + player.units.length)).padTop(15).left();
            int power = 0;
            int health = 0;
            int maxHealth = 0;
            for (NT_Unit unit : player.units) {
                power += unit.power;
                health += unit.health;
                maxHealth += unit.maxHealth;
            }
            content.add(LabelUtils.info(skin, "" + power)).padTop(15).left();
            content.add(LabelUtils.info(skin, health + "/" + maxHealth)).padTop(15).left().row();
        });
    }

}
