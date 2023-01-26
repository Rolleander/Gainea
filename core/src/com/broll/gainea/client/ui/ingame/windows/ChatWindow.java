package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.components.GameChat;

public class ChatWindow extends MenuWindow{
    public ChatWindow(Gainea game,  Skin skin) {
        super(game, "Chat", skin);
        add(new GameChat(skin, this.game.client.getClient().getConnectedLobby())).expandX().fillX();
        center(700, 200);
    }

    @Override
    public void update() {

    }
}
