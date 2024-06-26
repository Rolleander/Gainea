package com.broll.gainea.client.ui.ingame.unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.server.core.objects.monster.MonsterBehavior;

public class MonsterRender extends UnitRender {
    private final static int W = 154;
    private final static int H = 74;
    private TextureRegion starPlate;
    private Texture bubble;
    private Label.LabelStyle timerStyle;


    public MonsterRender(Gainea game, Skin skin, NT_Monster unit) {
        super(game, skin, unit);
        this.labelDisplacement = 25;
        this.timerStyle = new Label.LabelStyle(blackStyle.font, Color.LIGHT_GRAY);
        setHeight(radius * 2 + 48);
        bubble = game.assets.get("textures/bubble.png", Texture.class);
    }


    @Override
    public void init(NT_BoardObject object) {
        super.init(object);
        NT_Monster monster = (NT_Monster) getUnit();
        int stars = monster.stars;
        starPlate = new TextureRegion(game.assets.get("textures/star_plates.png", Texture.class), stars * W, 0, W, H);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setRenderColor(batch, parentAlpha);
        //draw star plate
        if (shouldDrawPlate()) {
            batch.draw(starPlate, getX() - radius - 26, getY() - radius + 48);
        }
        resetRenderColor(batch);
        super.draw(batch, parentAlpha);
        setRenderColor(batch, parentAlpha);
        NT_Monster monster = (NT_Monster) getUnit();
        MonsterBehavior behavior = MonsterBehavior.values()[monster.behavior];
        if (shouldDrawPlate() && behavior != MonsterBehavior.RESIDENT && monster.actionTimer > NT_Monster.NO_ACTION_TIMER) {
            batch.draw(bubble, getX() - 18, getY() - radius - 16);
            int timer = monster.actionTimer;
            if (timer <= 1) {
                numberLabel.setStyle(redStyle);
            } else {
                numberLabel.setStyle(timerStyle);
            }
            numberLabel.setPosition(getX() - 6, getY() - radius + 3);
            numberLabel.setText("" + timer);
            numberLabel.draw(batch, parentAlpha);
        }
        resetRenderColor(batch);
    }
}
