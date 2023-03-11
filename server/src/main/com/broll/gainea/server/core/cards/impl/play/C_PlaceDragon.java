package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.objects.monster.MonsterActivity;
import com.broll.gainea.server.core.objects.monster.MonsterBehavior;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;

public class C_PlaceDragon extends Card {
    public C_PlaceDragon() {
        super(32, "Drachenhort", "Platziert einen wilden Feuerdrachen auf einen beliebigen unbesetzten Berg");
        setDrawChance(0.7f);
    }

    @Override
    public boolean isPlayable() {
        return !getLocations().isEmpty();
    }

    private List<Location> getLocations() {
        return game.getMap().getAllAreas().stream().filter(it -> it.getType() == AreaType.MOUNTAIN && LocationUtils.emptyOrWildMonster(it)).collect(Collectors.toList());
    }

    @Override
    protected void play() {
        Monster monster = new Monster();
        monster.setName("Feuerdrache");
        monster.setIcon(119);
        monster.setPower(4);
        monster.setHealth(5);
        monster.setActivity(MonsterActivity.RARELY);
        monster.setBehavior(MonsterBehavior.AGGRESSIVE);
        placeUnitHandler.placeUnit(owner, monster, getLocations(), "Platziert den Feuerdrachen");
    }


}
