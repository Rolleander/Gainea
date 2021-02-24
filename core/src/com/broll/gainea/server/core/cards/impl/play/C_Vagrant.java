package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;
import java.util.stream.Collectors;

public class C_Vagrant extends AbstractCard {
    public C_Vagrant() {
        super(0, "Einsamer Landstreicher", "Platziert einen neutralen Vagabund (2/2) auf ein beliebiges freies Feld.");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Soldier soldier = new Soldier(null);
        soldier.setIcon(1);
        soldier.setName("Vagabund");
        soldier.setStats(2,2);
        List<Area> locations = game.getMap().getAllAreas().stream().filter(Location::isFree).collect(Collectors.toList());
        Location location = selectHandler.selectLocation("Standort für Vagabund festlegen", locations);
        UnitControl.spawn(game, soldier, location);
    }


}
