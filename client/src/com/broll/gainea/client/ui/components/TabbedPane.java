package com.broll.gainea.client.ui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.ui.utils.TableUtils;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class TabbedPane extends Table {

    private Table content = new Table();
    private List<Button> buttons = new ArrayList<>();

    private List<Pair<String, Actor>> tabs;

    public TabbedPane(Skin skin, List<Pair<String, Actor>> tabs) {
        super(skin);
        this.tabs = tabs;
        Table top = new Table();
        for (int i = 0; i < tabs.size(); i++) {
            Pair<String, Actor> tab = tabs.get(i);
            int finalI = i;
            Button button = TableUtils.textButton(skin, tab.getLeft(), () -> clickedButton(finalI));
            top.add(button);
            buttons.add(button);
        }
        buttons.get(0).setChecked(true);
        add(top).left().spaceBottom(5).row();
        add(content).colspan(tabs.size()).expand().fill();
        show(tabs.get(0).getRight());
    }

    private void clickedButton(int index) {
        this.show(tabs.get(index).getRight());
        for (int i = 0; i < tabs.size(); i++) {
            buttons.get(i).setChecked(i == index);
        }
    }

    private void show(Actor widget) {
        content.clearChildren();
        content.add(widget).expand().fill();
    }
}
