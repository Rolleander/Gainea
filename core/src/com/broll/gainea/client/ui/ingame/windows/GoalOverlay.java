package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_Goal;

import org.apache.commons.lang3.StringUtils;

public class GoalOverlay extends Table {

    private Table content = new Table();
    private Gainea game;

    public GoalOverlay(Gainea game) {
        this.game = game;
        setFillParent(true);
        top();
        left();
        add(content);
        content.pad(10);
        content.defaults().space(10);
        update();
    }

    public void update() {
        content.clear();
        game.state.getGoals().forEach(goal -> {
            content.add(renderGoal(game, goal)).left().row();
        });
    }

    public static Table renderGoal(Gainea game, NT_Goal goal) {
        Skin skin = game.ui.skin;
        Table table = new Table(skin);
        int w = 500;
        table.left();
        table.setBackground("goal-bg");
        table.add(new Image(TextureUtils.icon(game, 8 + goal.points)));
        String info = "";
        if (goal.progressionGoal != NT_Goal.NO_PROGRESSION_GOAL) {
            info = "   [BLUE](" + goal.progression + "/" + goal.progressionGoal + ")[]";
        }
        if (StringUtils.isNotEmpty(goal.restriction)) {
            info += "   [BROWN]" + goal.restriction + "[]";
        }
        table.add(LabelUtils.autoWrap(LabelUtils.markup(skin, goal.description + info), w)).width(w);
        return table;
    }

}
