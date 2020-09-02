package com.broll.gainea.client.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.broll.gainea.net.NT_BoardObject;

public class MapObjectRender extends Actor {

    private NT_BoardObject object;

    public MapObjectRender(NT_BoardObject object){
        this.object = object;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public NT_BoardObject getObject() {
        return object;
    }
}
