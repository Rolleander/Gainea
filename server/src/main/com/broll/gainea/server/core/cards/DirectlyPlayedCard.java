package com.broll.gainea.server.core.cards;

import com.broll.gainea.net.NT_Card;

public abstract class DirectlyPlayedCard extends Card {
    public DirectlyPlayedCard(int picture, String title, String text) {
        super(picture, title, text);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    public NT_Card nt() {
        NT_Card nt = super.nt();
        nt.playable = false;
        return nt;
    }
}
