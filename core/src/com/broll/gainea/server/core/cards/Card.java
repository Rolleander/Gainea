package com.broll.gainea.server.core.cards;

import com.broll.gainea.net.NT_Card;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.required.PlaceUnitAction;
import com.broll.gainea.server.core.actions.required.SelectChoiceAction;
import com.broll.gainea.server.core.player.Player;

public abstract class Card {

    protected GameContainer game;
    protected Player owner;
    private int id;
    private int picture;
    protected ActionHandlers actions;
    protected PlaceUnitAction placeUnitHandler;
    protected SelectChoiceAction selectHandler;

    private String title, text;
    private float drawChance = 1;

    public Card(int picture, String title, String text) {
        this.text = text;
        this.picture = picture;
        this.title = title;
    }

    protected void setDrawChance(float drawChance) {
        this.drawChance = drawChance;
    }

    public float getDrawChance() {
        return drawChance;
    }

    public void init(GameContainer game, Player owner, int id) {
        this.game = game;
        this.owner = owner;
        this.id = id;
    }

    public abstract boolean isPlayable();

    public void play(ActionHandlers actionHandlers) {
        this.actions = actionHandlers;
        placeUnitHandler = actions.getHandler(PlaceUnitAction.class);
        selectHandler = actions.getHandler(SelectChoiceAction.class);
        play();
    }

    protected abstract void play();

    public NT_Card nt() {
        NT_Card card = new NT_Card();
        card.id = (short) id;
        card.text = text;
        card.picture = (short) picture;
        card.title = title;
        return card;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public int getPicture() {
        return picture;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return super.toString()+"{" +
                "owner=" + owner +
                ", title='" + title + '\'' +
                '}';
    }
}
