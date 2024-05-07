package com.broll.gainea.client.ui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.utils.LabelUtils;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class ListSelectionPane extends Table {

    private final int W = 185;
    private Table content = new Table();
    private List<SelectionButton> buttons = new ArrayList<>();

    private List<Pair<String, Actor>> tabs;

    public ListSelectionPane(Skin skin, List<Pair<String, Actor>> tabs) {
        this(skin, tabs, false);
    }

    public ListSelectionPane(Skin skin, List<Pair<String, Actor>> tabs, boolean vertical) {
        super(skin);
        this.tabs = tabs;
        Table buttonBar = new Table();
        for (int i = 0; i < tabs.size(); i++) {
            Pair<String, Actor> tab = tabs.get(i);
            SelectionButton button = new SelectionButton(getSkin(), tab.getLeft(), i);
            buttonBar.add(button).width(W).left();
            buttons.add(button);
            if (vertical) {
                buttonBar.row();
            }
        }
        buttons.get(0).setSelected(true);
        if (vertical) {
            add(buttonBar).left().top().spaceRight(5);
        } else {
            add(buttonBar).left().top().spaceBottom(5).row();
        }
        add(content).expand().fill().top().center();
        show(tabs.get(0).getRight());
    }

    private void clickedButton(int index) {
        this.show(tabs.get(index).getRight());
        for (int i = 0; i < tabs.size(); i++) {
            buttons.get(i).setSelected(i == index);
        }
    }

    private void show(Actor widget) {
        content.clearChildren();
        content.add(widget).expand().fill();
    }

    private class SelectionButton extends Table {

        public SelectionButton(Skin skin, String text, int index) {
            super(skin);
            setTouchable(Touchable.enabled);
            add(LabelUtils.info(skin, text)).pad(5);
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    AudioPlayer.playSound("button.ogg");
                    event.stop();
                    clickedButton(index);
                }
            });
            setSelected(false);
        }

        public void setSelected(boolean selected) {
            if (selected) {
                setBackground("selected");
            } else {
                setBackground("menu-bg");
            }
        }

    }


}
