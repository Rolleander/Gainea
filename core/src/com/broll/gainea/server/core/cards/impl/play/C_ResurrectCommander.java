package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.utils.PlayerUtils;

public class C_ResurrectCommander extends Card {
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
