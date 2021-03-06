package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_HealingSpell extends AbstractCard {
    public C_HealingSpell() {
        super(62, "Lichtbeschwörung", "Heilt alle eure Einheiten");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        owner.getUnits().stream().filter(BattleObject::isHurt).forEach(unit->{
            UnitControl.heal(game, unit, 10000);
        });
    }

}
