package com.broll.gainea.client.ui.ingame.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.utils.LabelUtils;

public class RoundImageButton extends Button {

    private TextureRegion texture;

    private Label numberLabel;

    private String text;

    public RoundImageButton(Skin skin, TextureRegion texture) {
        this.texture = texture;
        this.setStyle(new ButtonStyle());
        this.numberLabel = LabelUtils.markup(skin, "");
    }

    @Override
    public float getPrefHeight() {
        return texture.getRegionWidth();
    }

    @Override
    public float getHeight() {
        return texture.getRegionHeight();
    }

    public void setText(String text) {
        this.text = text;
    }

    public void whenClicked(Runnable action) {
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioPlayer.playSound("button.ogg");
                action.run();
                event.stop();
            }
        });
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && this.getTouchable() != Touchable.enabled) return null;
        if (!isVisible() || isDisabled()) return null;
        if (Vector2.dst(texture.getRegionWidth() / 2, texture.getRegionHeight() / 2, x, y) < texture.getRegionWidth() / 2) {
            return this;
        }
        return null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        float a = 0.8f;
        if (isDisabled()) {
            a = 0.5f;
        }
        batch.setColor(color.r, color.g, color.b, a * parentAlpha);
        batch.draw(texture, getX(), getY());
        if (text != null) {
            numberLabel.setText(text);
            numberLabel.pack();
            numberLabel.setPosition(getX() + texture.getRegionWidth() / 2 - numberLabel.getWidth() / 2, getY() + 5);
            numberLabel.draw(batch, parentAlpha);
        }
        batch.setColor(color.r, color.g, color.b, 1f);
    }
}