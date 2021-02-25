package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.components.IconLabel;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.fractions.FractionType;

import java.util.List;

public class PlayerWindow extends MenuWindow {

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
        content.defaults().padTop(15).left();
        players.stream().sorted((p2, p1) -> {
            int p = Integer.compare(p1.points, p2.points);
            if (p == 0) {
                return Integer.compare(p1.stars, p2.stars);
            }
            return p;
        }).forEach(player -> {
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
        });
    }

}
