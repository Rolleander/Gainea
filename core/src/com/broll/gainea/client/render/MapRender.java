package com.broll.gainea.client.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

public class MapRender {

    private List<ExpansionRender> expansionRenders=new ArrayList<>();

    public MapRender(){

    }

    public void addRender(ExpansionRender expansionRender){
        expansionRenders.add(expansionRender);
    }

    public void render(SpriteBatch batch) {
       expansionRenders.forEach(r->r.render(batch));
    }

    public void render(ShapeRenderer shape) {
        expansionRenders.forEach(r->r.render(shape));
    }
}
