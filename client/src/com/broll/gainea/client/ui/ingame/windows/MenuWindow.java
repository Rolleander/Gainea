package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.windows.ClosableWindow;

public abstract class MenuWindow extends ClosableWindow {

    public MenuWindow(Gainea game, String title, Skin skin) {
        super(game, title, skin);
    }

    public void toggleVisiblity() {
        if (isVisible()) {
            setVisible(false);
        } else {
            setVisible(true);
            toFront();
        }
    }

    public abstract void update();
}
