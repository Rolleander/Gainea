package com.broll.gainea.client.ui.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.client.impl.IChatMessageListener;
import com.broll.networklib.client.impl.LobbyPlayer;

import org.apache.commons.lang3.StringUtils;

import java.util.function.IntConsumer;

public class GameChat extends Table {

    private Skin skin;
    private Table chatTable;
    private ScrollPane chatScrollPane;
    private GameLobby lobby;
    private int newMessages;

    private IntConsumer newMessageListener;

    public GameChat(Skin skin, GameLobby lobby) {
        this.skin = skin;
        this.lobby = lobby;
        this.setSkin(skin);
        if (lobby != null) {
            lobby.setChatMessageListener(new IChatMessageListener() {
                @Override
                public void fromPlayer(String msg, LobbyPlayer from) {
                    receivedNewMessage();
                    addChatMessage(from, msg);
                }

                @Override
                public void fromGame(String msg) {
                    receivedNewMessage();
                    addChatMessage(null, msg);
                }
            });
        }
        init();
    }

    public void setNewMessageListener(IntConsumer newMessageListener) {
        this.newMessageListener = newMessageListener;
    }

    private void init() {
        chatTable = new Table(skin);
        chatTable.top();
        chatTable.setBackground("menu-bg");
        chatTable.pad(10);
        chatTable.defaults().space(5);
        chatScrollPane = new ScrollPane(chatTable, skin);
        chatScrollPane.setScrollBarPositions(false, true);
        chatScrollPane.setOverscroll(false, false);
        chatScrollPane.setScrollingDisabled(true, false);
        chatScrollPane.setFadeScrollBars(false);
        add(chatScrollPane).fillX().expandX().height(150).colspan(2).row();
        TextField chatText = new TextField("", skin);
        chatText.addListener(new InputListener() {

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER && !StringUtils.isEmpty(chatText.getText())) {
                    sendChatMessage(chatText.getText());
                    chatText.setText("");
                    return true;
                }
                return false;
            }

        });
        Button sendChat = TableUtils.textButton(skin, "Send", () -> {
            if (!StringUtils.isEmpty(chatText.getText())) {
                sendChatMessage(chatText.getText());
                chatText.setText("");
            }
        });
        add(chatText).expandX().fillX();
        add(sendChat).row();
    }

    private void receivedNewMessage() {
        newMessages++;
        if (newMessageListener != null) {
            newMessageListener.accept(newMessages);
        }
    }

    public void resetNewMessages() {
        newMessages = 0;
    }

    private void addChatMessage(LobbyPlayer from, String message) {
        Table chat = new Table(skin);
        chat.left();
        chat.top();
        if (from != null) {
            chat.add(LabelUtils.color(LabelUtils.info(skin, from.getName()), Color.BLUE)).padRight(10);
        }
        chat.add(LabelUtils.info(skin, message));
        chatTable.add(chat).expandX().fillX().row();
        chatScrollPane.invalidate();
        chatScrollPane.layout();
        chatScrollPane.setScrollY(chatScrollPane.getMaxY());
    }

    private void sendChatMessage(String text) {
        lobby.sendChat(text);
        addChatMessage(lobby.getMyPlayer(), text);
    }


}
