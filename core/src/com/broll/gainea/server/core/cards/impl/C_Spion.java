package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.ArrayList;
import java.util.Set;

public class C_Spion extends AbstractCard {
    public C_Spion() {
        super(8, "Spion", "Platziert einen Soldat auf ein besetztes Land eines beliebigen Spielers ohne einen Kampf.");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Set<Location> locations = PlayerUtils.getHostileLocations(game, owner);
        placeUnitHandler.placeSoldier(owner, new ArrayList<>(locations));
    }

}
