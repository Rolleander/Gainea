package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.GodDragon;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_ResurrectCommander extends AbstractCard {
    public C_ResurrectCommander() {
        super(61, "Auferstehung", "Lasst euren gefallenen Feldherr zurückkehren");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        if (PlayerUtils.isCommanderAlive(owner)) {
            return;
        }
        placeUnitHandler.placeCommander(owner, owner.getControlledLocations());
    }

}
