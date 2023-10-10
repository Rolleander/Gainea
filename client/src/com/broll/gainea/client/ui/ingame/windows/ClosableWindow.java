package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.utils.TextureUtils;

public class ClosableWindow extends Window {

    protected Skin skin;
    protected Gainea game;
    private float width, height;
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
                setVisible(false);
            }
        });
        bar.add(button);
        bar.setBackground(new ColorDrawable(0.3f, 0.3f, 0.3f, 1f));
        padTop(25);
        setVisible(false);
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

    private class ColorDrawable extends BaseDrawable {

        private final static int BORDER = 4;
        private float r, g, b, a;
        private Color savedBatchColor = new Color();
        private Texture blankWhite;

        public ColorDrawable(float r, float g, float b, float a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            this.blankWhite = game.assets.get("textures/white.png", Texture.class);
        }

        @Override
        public void draw(Batch batch, float x, float y, float width, float height) {
            savedBatchColor.set(batch.getColor());
            batch.setColor(r, g, b, a);
            batch.draw(blankWhite, x - BORDER, y, width + BORDER * 2, height);
            batch.setColor(savedBatchColor);
        }
    }
}
