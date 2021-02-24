package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_LonelySoldier extends DirectlyPlayedCard {

    public C_LonelySoldier() {
        super(18, "Expedition ins Ungewisse", "Ihr erhaltet einen Soldaten auf einem zufälligen freien Feld");
    }

    @Override
    protected void play() {
        Location location = LocationUtils.getRandomFree(game.getMap().getAllAreas());
        if (location != null) {
            Soldier soldier = owner.getFraction().createSoldier();
            UnitControl.spawn(game, soldier, location);
        }
    }

}
