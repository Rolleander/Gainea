package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;

public class C_Phoenix extends DirectlyPlayedCard {

    public C_Phoenix() {
        super(65, "Beschworener Phönix", "Beschwört einen Phönix (2/2), dieser kann 2 Felder pro Zug bewegt werden aber kann nicht angreifen.");
        setDrawChance(0.8f);
    }

    @Override
    protected void play() {
        Monster monster = new Monster();
        monster.setName("Phönix");
        monster.getAttacksPerTurn().setValue(0);
        monster.getMovesPerTurn().setValue(0);
        monster.setHealth(2);
        monster.setPower(2);
        monster.setIcon(124);
        placeUnitHandler.placeUnit(owner, monster, owner.getControlledLocations(), "Ort der Beschwörung wählen");
    }

}
