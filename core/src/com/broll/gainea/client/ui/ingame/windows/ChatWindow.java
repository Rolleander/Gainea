package com.broll.gainea.client.ui.ingame.windows;

import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.components.GameChat;

public class ChatWindow extends MenuWindow {
    public ChatWindow(Gainea game) {
        super(game, "Chat", game.ui.skin);
        add(new GameChat(skin, this.game.client.getClient().getConnectedLobby())).expandX().fillX();
        center(700, 200);
    }

    @Override
    public void update() {

    }
}
