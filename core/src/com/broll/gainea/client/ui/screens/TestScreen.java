package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.client.ui.AbstractScreen;

import org.apache.commons.lang3.StringUtils;

public class TestScreen extends AbstractScreen {
    @Override
    public Actor build() {
        Table vg = new Table();
        vg.setFillParent(true);
        vg.setBackground(new TextureRegionDrawable(new Texture("textures/title.png")));
        vg.pad(50, 50, 50, 50);
        Table window = new Table(skin);
        window.pad(30, 20, 10, 20);
        window.defaults().space(15);
        window.setBackground("window");
        window.top();
        window.add(title("TestLobby")).left().row();

        Table lobbyTable = new Table(skin);
        lobbyTable.top();
        lobbyTable.setBackground("menu-bg");
        lobbyTable.pad(10);
        lobbyTable.defaults().space(5);
        for(int i=0; i<10; i++){
            Table player = new Table(skin);
            player.left();
            player.setBackground("highlight");
            player.add(info("player "+i));
            lobbyTable.add(player).expandX().fillX().row();
        }
        window.add(lobbyTable).fill().expand().colspan(2).row();
        Table chatTable = new Table(skin);
        chatTable.setBackground("menu-bg");
        chatTable.pad(10);
        chatTable.defaults().space(5);
        for(int i=0; i<20; i++){
            Table player = new Table(skin);
            player.left();
            player.setBackground("highlight");
            player.add(info("player "+i));
            chatTable.add(player).expandX().fillX().row();
        }
        ScrollPane scrollPane = new ScrollPane(chatTable,skin);
        scrollPane.setScrollBarPositions(false,true);
        scrollPane.setOverscroll(false,false);
        scrollPane.setScrollingDisabled(true,false);
        scrollPane.setFadeScrollBars(false);
        window.add(scrollPane).fillX().expandX().height(150).colspan(2).row();

        TextField chatText = new TextField("",skin);
        chatText.addListener(new InputListener(){

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if(keycode == Input.Keys.ENTER && !StringUtils.isEmpty(chatText.getText())){
                    chatTable.add(chatText.getText()).expandX().fillX().row();
                    chatText.setText("");
                    return true;
                }
                return false;
            }

        });
        Button sendChat = new Button(skin);
        sendChat.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(!StringUtils.isEmpty(chatText.getText())){
                    chatTable.add(chatText.getText()).expandX().fillX().row();
                    scrollPane.invalidate();
                    scrollPane.layout();
                    chatText.setText("");
                    scrollPane.setScrollY(scrollPane.getMaxY());
                    return true;
                }
                return false;
            }
        });
        sendChat.add(label("Send"));

        window.add(chatText).expandX().fillX();
        window.add(sendChat).row();

        vg.add(window).expand().fill();
        return vg;
    }

}
