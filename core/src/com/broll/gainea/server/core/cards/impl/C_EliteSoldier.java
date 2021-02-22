package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.actions.required.PlaceUnitAction;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.player.Player;

public class C_EliteSoldier extends DirectlyPlayedCard {
    public C_EliteSoldier() {
        super(25, "Profi anheuern", "Rekrutiert einen Elitekrieger");
    }

    @Override
    protected void play() {
        EliteSoldier soldier = new EliteSoldier(owner);
        game.getReactionHandler().getActionHandlers().getHandler(PlaceUnitAction.class).placeUnit(owner, soldier, owner.getControlledLocations(), "Platziere Elitekrieger");
    }

    private class EliteSoldier extends Soldier {

        public EliteSoldier(Player owner) {
            super(owner);
            setStats(2, 2);
            setName("Elitekrieger");
            setIcon(105);
        }
    }
}
