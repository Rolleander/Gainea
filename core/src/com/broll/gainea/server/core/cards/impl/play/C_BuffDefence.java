package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_BuffDefence extends Card {

    private final static int BUFF = 2;

    public C_BuffDefence() {
        super(52, "Schildformation", "Verleiht einer Einheit +" + BUFF + " Leben");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        BattleObject unit = SelectionUtils.selectPlayerUnit(game, owner, "Welche Einheit soll ausgerüstet werden?");
        unit.changeHealth(BUFF);
        UnitControl.focus(game, unit, NT_Abstract_Event.EFFECT_BUFF);
    }

}
