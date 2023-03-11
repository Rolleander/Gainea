package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Event;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_BuffMighty extends Card {
    public C_BuffMighty() {
        super(10, "Excaliburs Macht", "Verdoppelt Angriff und Leben einer Einheit");
        setDrawChance(0.2f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Unit unit = SelectionUtils.selectPlayerUnit(game, owner, "Welche Einheit soll gestärkt werden?");
        unit.setPower(unit.getPower().getRootValue() * 2);
        unit.getHealth().setValue(unit.getHealth().getRootValue() * 2);
        unit.getMaxHealth().setValue(unit.getMaxHealth().getRootValue() * 2);
        UnitControl.focus(game, unit, NT_Event.EFFECT_BUFF);
    }

}
