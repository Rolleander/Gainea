package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class DepthStage extends Stage {

    public DepthStage(Viewport viewport) {
        super(viewport);
    }

    public void sort() {
        this.getActors().sort((a, b) -> {
            DepthActor da = (DepthActor) a;
            DepthActor db = (DepthActor) b;
            if (da.depth == db.depth) {
                return Float.compare(da.getY(), db.getY());
            }
            return Integer.compare(da.depth, db.depth);
        });
    }

}
