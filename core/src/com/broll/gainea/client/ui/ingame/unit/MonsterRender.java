package com.broll.gainea.client.ui.ingame.unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.server.core.objects.MonsterBehavior;

public class MonsterRender extends UnitRender {
    private TextureRegion starPlate;
    private Texture bubble;
    private Label.LabelStyle timerStyle;
    private final static int W = 154;
    private final static int H = 74;


    public MonsterRender(Gainea game, Skin skin, NT_Monster unit) {
        super(game, skin, unit);
        this.timerStyle = new Label.LabelStyle(blackStyle.font, Color.LIGHT_GRAY);
        int stars = unit.stars;
        setHeight(MapObjectRender.R * 2 + 48);
        starPlate = new TextureRegion(game.assets.get("textures/star_plates.png", Texture.class), stars * W, 0, W, H);
        bubble = game.assets.get("textures/bubble.png", Texture.class);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        calcRenderColor(parentAlpha);
        batch.setColor(renderColor);
        //draw star plate
        if (shouldDrawPlate()) {
            batch.draw(starPlate, getX() - MapObjectRender.R - 26, getY() - MapObjectRender.R + 48);
        }
        super.draw(batch, parentAlpha);
        NT_Monster monster = (NT_Monster)getUnit();
        MonsterBehavior behavior = MonsterBehavior.values()[monster.behavior];
        if(shouldDrawPlate() && behavior != MonsterBehavior.RESIDENT){
            batch.draw(bubble, getX() - 18, getY()-MapObjectRender.R - 16);
            int timer = monster.actionTimer;
            if(timer<=1){
                numberLabel.setStyle(redStyle);
            }
            else{
                numberLabel.setStyle(timerStyle);
            }
            numberLabel.setPosition(getX() -6, getY()-MapObjectRender.R +3);
            numberLabel.setText("" + timer);
            numberLabel.draw(batch, parentAlpha);
        }
    }
}
