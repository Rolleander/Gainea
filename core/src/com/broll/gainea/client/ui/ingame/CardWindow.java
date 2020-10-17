package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.elements.ClosableWindow;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.client.ui.elements.TableUtils;
import com.broll.gainea.client.ui.elements.TextureUtils;

public class CardWindow extends ClosableWindow {
    private Table content;

    public CardWindow(Gainea game, Skin skin) {
        super(game, "Karten", skin);
        content = new Table();
        ScrollPane scrollPane = new ScrollPane(content, skin);
        scrollPane.setScrollBarPositions(false, true);
        scrollPane.setOverscroll(false, false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        add(scrollPane).expand().fill();
        TableUtils.consumeClicks(this);
        center(1000, 500);
        update();
    }

    public void update() {
        content.clear();
        content.top().left();
        content.pad(10);
        content.defaults().space(10);
        game.state.getCards().forEach(card -> {
            Table table = new Table(skin);
            table.defaults().space(15);
            table.left();
            table.setBackground("menu-bg");
            Table box = new Table(skin);
            box.top();
            box.add(LabelUtils.label(skin, card.title)).left().row();
            int w = 600;
            box.add(LabelUtils.autoWrap(LabelUtils.info(skin, card.text), w)).width(w).left().row();
            box.add(new TextButton("Einsetzen!", skin)).right().bottom();
            table.add(new Image(new TextureRegionDrawable(TextureUtils.cardPicture(game, card.picture)))).top();
            table.add(box).top();
            content.add(table).expandX().fillX().row();
        });
    }
}
