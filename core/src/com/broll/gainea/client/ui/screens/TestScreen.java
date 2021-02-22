package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.client.ui.AbstractScreen;
import com.broll.gainea.client.ui.elements.TextureUtils;

import org.apache.commons.lang3.StringUtils;

public class TestScreen extends AbstractScreen {
    @Override
    public Actor build() {
        Table vg = new Table();
        vg.setFillParent(true);
        Table f =  new Table();
        f.setBackground(new TextureRegionDrawable(new Texture("textures/cards.png")));
        for(int i=0; i<80; i++){
            f.add(new Label(i+"",skin)).padTop(TextureUtils.CARD_HEIGHT).row();
        }
        vg.add(scroll(f)).fillY().expandY().padRight(50);
        f = new Table();
        f.setBackground(new TextureRegionDrawable(new Texture("textures/units.png")));
        for(int i=0; i<10 * 20; i++){
            Cell<Label> c = f.add(new Label(i + "", skin)).padLeft(TextureUtils.UNIT_SIZE).padBottom(TextureUtils.UNIT_SIZE);
            if(i % 10 == 9){
                c.row();
            }
        }

       vg.add(scroll(f)).fillY().expandY();

        return vg;
    }

    private ScrollPane scroll(Actor actor){
        ScrollPane scrollPane = new ScrollPane(actor, skin);
        scrollPane.setScrollBarPositions(true, true);
        scrollPane.setOverscroll(false, false);
        scrollPane.setScrollingDisabled(false, false);
        scrollPane.setFadeScrollBars(false);
        return scrollPane;
    }

}
