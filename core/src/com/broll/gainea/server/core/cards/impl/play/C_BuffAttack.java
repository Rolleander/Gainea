package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Event;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_BuffAttack extends Card {
    public C_BuffAttack() {
        super(51, "Aufrüsten", "Verleiht einer Einheit +2 Angriff");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        BattleObject unit = SelectionUtils.selectPlayerUnit(game, owner, "Welche Einheit soll ausgerüstet werden?");
        unit.getPower().addValue(2);
        UnitControl.focus(game, unit, NT_Event.EFFECT_BUFF);
    }

}
