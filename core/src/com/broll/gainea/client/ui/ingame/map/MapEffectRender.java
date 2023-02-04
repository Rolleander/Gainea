package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_BoardEffect;

public class MapEffectRender extends Actor {

    private final static int FIRE_WIDTH = 119;
    private final static  int FIRE_HEIGHT = 188;

    private Animation<TextureRegion> animation;
    private float animationTime;

    public MapEffectRender(Gainea game, NT_BoardEffect effect){
        setPosition(effect.x, effect.y);
        animation = new Animation<>(0.07f, TextureUtils.split(game.assets.get("textures/fire.png", Texture.class), FIRE_WIDTH, FIRE_HEIGHT));
        animation.setPlayMode(Animation.PlayMode.LOOP);
        setZIndex(5000);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        int srcFunc = batch.getBlendSrcFunc();
        int dstFunc = batch.getBlendDstFunc();
        batch.enableBlending();
        animationTime += Gdx.graphics.getDeltaTime();
        TextureRegion frame = animation.getKeyFrame(animationTime);
        batch.setBlendFunction(GL30.GL_ONE, GL30.GL_ONE_MINUS_SRC_COLOR);
        batch.draw(frame, getX() - frame.getRegionWidth() / 2 , getY() - frame.getRegionHeight()/2);
        batch.setBlendFunction(srcFunc, dstFunc);
    }
}
