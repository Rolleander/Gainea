package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.net.NT_Goal;


public class GoalWindow extends MenuWindow {
    private Table content;

    public GoalWindow(Gainea game, Skin skin) {
        super(game, "Ziele", skin);
        content = new Table();
        add(content).expand().fill();
        TableUtils.consumeClicks(this);
        center(750, 500);
        update();
    }

    public void update() {
        content.clear();
        content.top().left();
        content.pad(10);
        content.defaults().space(10);
        game.state.getGoals().forEach(goal -> {
            content.add(renderGoal(skin, goal)).expandX().fillX().row();
        });
    }

    public static Table renderGoal(Skin skin, NT_Goal goal) {
        Table table = new Table(skin);
        table.left();
        table.setBackground("menu-bg");
        table.add(LabelUtils.info(skin, "" + goal.points)).left();
        table.add(LabelUtils.info(skin, goal.restriction)).right().row();
        int w = 650;
        String progression = "";
        if (goal.progressionGoal != NT_Goal.NO_PROGRESSION_GOAL) {
            progression = "   [BLUE](" + goal.progression + "/" + goal.progressionGoal + ")[]";
        }
        table.add(LabelUtils.autoWrap(LabelUtils.markup(skin, goal.description + progression), w)).width(w).left();
        return table;
    }
}
