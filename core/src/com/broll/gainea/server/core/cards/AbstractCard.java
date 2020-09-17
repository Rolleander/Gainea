package com.broll.gainea.server.core.cards;

import com.broll.gainea.net.NT_Card;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.impl.PlaceUnitAction;
import com.broll.gainea.server.core.actions.impl.SelectChoiceAction;
import com.broll.gainea.server.core.player.Player;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public abstract class AbstractCard {

    protected GameContainer game;
    protected Player owner;
    private int id;
    private int picture;
    protected ActionHandlers actions;
    protected PlaceUnitAction placeUnitHandler;
    protected SelectChoiceAction selectHandler;

    private String title, text;
    private float drawChance = 1;

    public AbstractCard(int picture, String title, String text) {
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
        placeUnitHandler = actions.getHandler(PlaceUnitAction.class);
        selectHandler = actions.getHandler(SelectChoiceAction.class);
        this.actions = actionHandlers;
        play();
    }

    public abstract void play();

    public NT_Card nt() {
        NT_Card card = new NT_Card();
        card.id = id;
        card.text = text;
        card.picture = picture;
        card.title = title;
        return card;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }
}
