package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;

public class C_PlaceWatersnake extends Card {
    public C_PlaceWatersnake() {
        super(49, "Verseuchte Gewässer", "Platziert eine wilde Seeschlange auf ein beliebiges unbesetztes Meer");
        setDrawChance(0.8f);
    }

    @Override
    public boolean isPlayable() {
        return !getLocations().isEmpty();
    }

    private List<Location> getLocations() {
        return game.getMap().getAllAreas().stream().filter(it -> it.getType() == AreaType.LAKE && LocationUtils.emptyOrWildMonster(it)).collect(Collectors.toList());
    }

    @Override
    protected void play() {
        Monster monster = new Monster();
        monster.setName("Seeschlange");
        monster.setIcon(120);
        monster.setPower(4);
        monster.setHealth(4);
        placeUnitHandler.placeUnit(owner, monster, getLocations(), "Platziert die Seeschlange");
    }


}
