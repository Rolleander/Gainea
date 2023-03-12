package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;

public class C_TakeOver extends Card {
    public C_TakeOver() {
        super(17, "Treueschwur", "");
    }

    @Override
    public boolean isPlayable() {
        return false;
    }

    @Override
    protected void play() {

    }
}
