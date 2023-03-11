package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_HealingSpell extends Card {
    public C_HealingSpell() {
        super(62, "Lichtbeschwörung", "Heilt alle eure Einheiten");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        owner.getUnits().stream().filter(Unit::isHurt).forEach(unit -> {
            UnitControl.heal(game, unit, 10000);
        });
    }

}
