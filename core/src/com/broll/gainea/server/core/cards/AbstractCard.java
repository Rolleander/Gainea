package com.broll.gainea.server.core.cards;

import com.broll.gainea.misc.Consumer;
import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Card;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.player.Player;

public abstract class AbstractCard {

    protected GameContainer game;
    protected Player owner;
    private int id;

    private String title, text;
    private float drawChance = 1;

    public AbstractCard(String title, String text) {
        this.text = text;
        this.title = title;
    }

    protected void setDrawChance(float drawChance) {
        this.drawChance = drawChance;
    }

    public float getDrawChance() {
        return drawChance;
    }

    public boolean init(GameContainer game, Player owner, int id) {
        this.game = game;
        this.owner = owner;
        this.id = id;
        return Math.random() < drawChance;
    }

    public abstract boolean isPlayable();

    public abstract void play(ActionHandlers actionHandlers);

    public NT_Card nt() {
        NT_Card card = new NT_Card();
        card.id = id;
        card.text = text;
        card.title = title;
        return card;
    }

    public int getId() {
        return id;
    }
}
