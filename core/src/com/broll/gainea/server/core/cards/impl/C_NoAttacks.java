package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.objects.buffs.BooleanBuff;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.processing.TurnNumberTrigger;

public class C_NoAttacks extends AbstractCard {

    private final static int DURATION = 3;

    public C_NoAttacks() {
        super(14, "Wachpatrouille", "Wählt einen Gegner, dieser kann " + DURATION + " Runden keine Angriffe ausführen.");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Player player = selectHandler.selectOtherPlayer(owner, "Welcher Spieler darf nicht mehr angreifen?");
        BooleanBuff buff = new BooleanBuff(false);
        player.getAttackingAllowed().addBuff(buff);
        game.getBuffDurationProcessor().timeoutBuff(buff, DURATION);
    }

}
