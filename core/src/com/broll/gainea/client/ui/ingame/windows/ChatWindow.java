package com.broll.gainea.client.ui.ingame.windows;

import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.components.GameChat;

import java.util.function.IntConsumer;

public class ChatWindow extends MenuWindow {

    private final GameChat chat;

    private IntConsumer newMessagesListener;

    public ChatWindow(Gainea game, IntConsumer newMessagesListener) {
        super(game, "Chat", game.ui.skin);
        this.newMessagesListener = newMessagesListener;
        chat = new GameChat(skin, this.game.client.getClient().getConnectedLobby());
        add(chat).expandX().fillX();
        chat.setNewMessageListener(this::newMessages);
        center(700, 200);
    }


    private void newMessages(int count) {
        if (!isVisible()) {
            newMessagesListener.accept(count);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            chat.resetNewMessages();
            newMessages(0);
        }
    }

    @Override
    public void update() {

    }


}
