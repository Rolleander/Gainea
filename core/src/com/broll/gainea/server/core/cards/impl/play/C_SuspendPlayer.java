package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.AbstractCard;

public class C_SuspendPlayer extends AbstractCard {
    public C_SuspendPlayer() {
        super(12,"In den Kerker", "Ein Spieler deiner Wahl muss eine Runde aussetzen");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        selectHandler.selectOtherPlayer(owner, "Spieler der Aussetzen muss:").skipRounds(1);
    }
}
