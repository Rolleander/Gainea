package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.GlobalBuff;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.player.Player;

public class C_PlayerNoAttacks extends AbstractCard {

    private final static int DURATION = 3;

    public C_PlayerNoAttacks() {
        super(14, "Wachpatrouille", "Wählt einen Gegner, dieser kann " + DURATION + " Runden keine Angriffe ausführen.");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Player player = selectHandler.selectOtherPlayer(owner, "Welcher Spieler darf nicht mehr angreifen?");
        IntBuff buff = new IntBuff(BuffType.SET, 0);
        GlobalBuff.createForPlayer(game, player, buff, unit -> unit.getAttacksPerTurn().addBuff(buff), NT_Abstract_Event.EFFECT_DEBUFF);
        game.getBuffProcessor().timeoutBuff(buff, DURATION);
    }

}
