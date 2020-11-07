package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class C_PlaceDragon extends AbstractCard {
    public C_PlaceDragon() {
        super(32, "Drachenhort", "Platziert einen wilden Feuerdrachen auf einen beliebigen unbesetzten Berg");
        setDrawChance(0.7f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    public void play() {
        Monster monster = new Monster();
        monster.setName("Feuerdrache");
        monster.setIcon(119);
        monster.setPower(4);
        monster.setHealth(5);
        monster.setMaxHealth(5);
        List<Area> locations = game.getMap().getAllAreas().stream().filter(it -> it.getType() == AreaType.MOUNTAIN && LocationUtils.emptyOrWildMonster(it)).collect(Collectors.toList());
        if (!locations.isEmpty()) {
            Location target = selectHandler.selectLocation("Wählt einen Ort", locations);
            game.getObjects().add(monster);
            UnitControl.spawn(game, monster, target);
        }
    }


}
