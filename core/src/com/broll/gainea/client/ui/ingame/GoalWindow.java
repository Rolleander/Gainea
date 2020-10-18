package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.elements.ClosableWindow;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.client.ui.elements.TableUtils;
import com.broll.gainea.net.NT_Goal;


public class GoalWindow extends ClosableWindow {
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
        table.add(LabelUtils.label(skin, "" + goal.points)).expandX().fillX().left();
        table.add(LabelUtils.label(skin, goal.restriction)).right().row();
        int w = 650;
        table.add(LabelUtils.autoWrap(LabelUtils.info(skin, goal.description), w)).width(w).left();
        return table;
    }
}
