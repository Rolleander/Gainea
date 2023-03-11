package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.monster.MonsterBehavior;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;
import java.util.stream.Collectors;

public class C_MonsterBait extends Card {
    public C_MonsterBait() {
        super(69, "Köderstein", "Wählt ein neutrales Gebiet, alle benachbarten Monster bewegen sich dorthin und werden sesshaft.");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        List<Area> areas = game.getMap().getAllAreas().stream().filter(LocationUtils::emptyOrWildMonster).collect(Collectors.toList());
        Location location = selectHandler.selectLocation(owner, "Wähle einen Zielort", areas);
        location.getConnectedLocations().stream().flatMap(it -> LocationUtils.getMonsters(it).stream())
                .forEach(monster -> {
                    monster.setBehavior(MonsterBehavior.RESIDENT);
                    UnitControl.move(game, monster, location);
                });
    }
}
