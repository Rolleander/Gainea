package com.broll.gainea.client.ui.ingame.hud;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.components.ColorCircle;
import com.broll.gainea.client.ui.components.IconLabel;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.fractions.FractionType;

import java.util.List;

public class PlayerOverlay extends Table {

    private Table content = new Table();
    private Gainea game;

    public PlayerOverlay(Gainea game) {
        this.game = game;
        setFillParent(true);
        bottom();
        left();
        add(content);
        content.setSkin(game.ui.skin);
        content.pad(10);
        content.setBackground("info-bg");
        content.defaults().space(10);
        update();
    }

    private void header(Table content) {
        Skin skin = game.ui.skin;
        content.top().left();
        content.pad(10);
        int space = 5;
        content.add(LabelUtils.markup(skin, "  ")).left();
        content.add(LabelUtils.markup(skin, "Spieler")).padRight(space * 3).fillX().expandX().left();
        content.add(LabelUtils.markup(skin, "Fraktion")).padRight(space * 3).left();
        content.add(LabelUtils.markup(skin, "Punkte")).padRight(space).left();
        content.add(new IconLabel(game, 2, "")).padRight(space).left();
        content.add(LabelUtils.markup(skin, "Karten")).padRight(space).left();
        content.add(LabelUtils.markup(skin, "Einheiten")).padRight(space).left();
        content.add(new IconLabel(game, 0, "")).padRight(space).left();
        content.add(new IconLabel(game, 1, "")).left();
    }

    private void row(Table content, NT_Player player) {
        Skin skin = game.ui.skin;
        content.add(new ColorCircle(game, player.color)).padLeft(5);
        content.add(LabelUtils.markup(skin, player.name)).fillX().expandX();
        content.add(LabelUtils.markup(skin, FractionType.values()[player.fraction].getDisplayName()));
        content.add(LabelUtils.markup(skin, "" + player.points));
        content.add(LabelUtils.markup(skin, "" + player.stars));
        content.add(LabelUtils.markup(skin, "" + player.cards));
        content.add(LabelUtils.markup(skin, "" + player.units.length));
        int power = 0;
        int health = 0;
        int maxHealth = 0;
        for (NT_Unit unit : player.units) {
            power += unit.power;
            health += unit.health;
            maxHealth += unit.maxHealth;
        }
        content.add(LabelUtils.markup(skin, "" + power));
        content.add(LabelUtils.markup(skin, health + "/" + maxHealth)).row();
    }

    public void update() {
        List<NT_Player> players = game.state.getPlayers();
        if (players == null) {
            return;
        }
        content.clear();
        header(content);
        content.row();
        content.defaults().left();
        players.forEach(player ->
                row(content, player)
        );
    }
}
