package com.broll.gainea.server.core.cards;

public abstract class DirectlyPlayedCard extends AbstractCard {
    public DirectlyPlayedCard(int picture,String title, String text) {
        super(picture,title, text);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }
}
