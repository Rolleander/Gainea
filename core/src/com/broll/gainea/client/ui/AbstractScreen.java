package com.broll.gainea.client.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.networklib.client.LobbyGameClient;

public abstract class AbstractScreen {

    protected Skin skin;
    protected LobbyGameClient client;
    protected GameUI ui;

    public AbstractScreen(){

    }

    void init(Skin skin, LobbyGameClient client, GameUI ui){
        this.skin = skin;
        this.client = client;
        this.ui = ui;
    }

    protected void backToTitle(){
        ui.showScreen(new StartScreen());
    }


    protected Label info(String text){
        return new Label(text,skin);
    }


    protected Label title(String text){
        Label l= new Label(text,skin,"title");
        return l;
    }

    protected Label label(String text){
        Label l= new Label(text,skin,"title");
        l.setFontScale(0.7f);
        return l;
    }



    public abstract Actor build();
}
