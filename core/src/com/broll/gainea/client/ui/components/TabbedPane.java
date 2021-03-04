package com.broll.gainea.client.ui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.broll.gainea.client.ui.utils.TableUtils;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class TabbedPane extends Table {

    private Table content = new Table();

    public TabbedPane(Skin skin, List<Pair<String,Actor>> tabs){
        super(skin);
        Table top = new Table();
        tabs.forEach(tab->{
            top.add(TableUtils.textButton(skin, tab.getLeft(), ()->this.show(tab.getRight())));
        });
        add(top).left().row();
        add(content).colspan(tabs.size()).expand().fill();
        show(tabs.get(0).getRight());
    }

    private void show(Actor widget){
        content.clearChildren();
        content.add(widget).expand().fill();
    }
}
