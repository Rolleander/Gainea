package com.broll.gainea.client.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public abstract class AbstractScreen {

    protected Skin skin;

    public AbstractScreen(){

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


    void init(Skin skin){
        this.skin = skin;
    }

    public abstract Actor build();
}
