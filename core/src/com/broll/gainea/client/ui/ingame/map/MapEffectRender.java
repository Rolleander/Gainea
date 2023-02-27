package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_BoardEffect;

public class MapEffectRender extends Actor {

    private final static int FIRE_WIDTH = 119;
    private final static int FIRE_HEIGHT = 188;

    private int effect;
    private Animation<TextureRegion> animation;
    private TextureRegion texture;
    private float animationTime;

    public MapEffectRender(Gainea game, NT_BoardEffect effect) {
        setPosition(effect.x, effect.y);
        this.effect = effect.effect;
        if (this.effect == NT_BoardEffect.EFFECT_FIRE) {
            animation = new Animation<>(0.07f, TextureUtils.split(game.assets.get("textures/fire.png", Texture.class), FIRE_WIDTH, FIRE_HEIGHT));
            animation.setPlayMode(Animation.PlayMode.LOOP);
            setZIndex(500);
        } else if (this.effect == NT_BoardEffect.EFFECT_PORTAL) {
            setZIndex(50);
            texture = new TextureRegion(game.assets.get("textures/gate.png", Texture.class));
            addAction(Actions.alpha(0.8f));
            addAction(Actions.forever(Actions.rotateBy(150, 1)));
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        animationTime += Gdx.graphics.getDeltaTime();
        if (effect == NT_BoardEffect.EFFECT_FIRE) {
            int srcFunc = batch.getBlendSrcFunc();
            int dstFunc = batch.getBlendDstFunc();
            batch.enableBlending();
            TextureRegion frame = animation.getKeyFrame(animationTime);
            batch.setBlendFunction(GL30.GL_ONE, GL30.GL_ONE_MINUS_SRC_COLOR);
            batch.draw(frame, getX() - frame.getRegionWidth() / 2, getY() - frame.getRegionHeight() / 2);
            batch.setBlendFunction(srcFunc, dstFunc);
        } else if (effect == NT_BoardEffect.EFFECT_PORTAL) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            float xc = texture.getRegionWidth() / 2;
            float yc = texture.getRegionHeight() / 2;
            batch.draw(texture, getX() - xc, getY() - yc, xc, yc, xc * 2, yc * 2, 1, 1, getRotation());
            batch.setColor(color.r, color.g, color.b, 1f);
        }
    }
}
