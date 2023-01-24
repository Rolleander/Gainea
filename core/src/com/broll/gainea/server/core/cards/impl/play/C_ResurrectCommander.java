package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.utils.PlayerUtils;

public class C_ResurrectCommander extends Card {
    public C_ResurrectCommander() {
        super(61, "Auferstehung", "Lasst euren gefallenen Feldherr zurückkehren");
        setDrawChance(2f);
    }

    @Override
    public boolean isPlayable() {
        return !PlayerUtils.isCommanderAlive(owner);
    }

    @Override
    protected void play() {
        placeUnitHandler.placeCommander(owner, owner.getControlledLocations());
    }

}
