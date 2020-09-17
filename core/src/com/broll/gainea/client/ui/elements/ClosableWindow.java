package com.broll.gainea.client.ui.elements;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.Gainea;

public class ClosableWindow extends Window {

    private float width, height;
    protected Skin skin;
    protected Gainea game;
    private boolean set = false;


    public ClosableWindow(Gainea game, String title, Skin skin) {
        super(title, skin);
        this.game = game;
        this.skin = skin;
        Table bar = (Table) getChild(0);
        ImageButton button = new ImageButton(new TextureRegionDrawable(TextureUtils.icon(game, 3)));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove();
            }
        });
        bar.add(button);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!set) {
            set = true;
            float cx = getStage().getWidth() / 2;
            float cy = getStage().getHeight() / 2;
            setBounds(cx - width / 2, cy - height / 2, width, height);
        }
    }

    public void center(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
