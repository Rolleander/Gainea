package com.broll.gainea.server.core.cards.impl;

import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.ShipUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class C_Pirates extends DirectlyPlayedCard {
    public C_Pirates() {
        super(38, "Piraten!", "Piraten befallen zufällige unbesetzte Schiffe");
        setDrawChance(0.8f);
    }

    @Override
    protected void play() {
        List<Ship> freeShips = game.getMap().getAllShips().stream().filter(LocationUtils::noControlledUnits).collect(Collectors.toList());
        Collections.shuffle(freeShips);
        int count = MathUtils.random(2, 3);
        for (int i = 0; i < Math.min(count, freeShips.size()); i++) {
            Soldier pirate = new Soldier(null);
            pirate.setName("Pirat");
            pirate.setIcon(121);
            pirate.setPower(2);
            pirate.setHealth(2);
            UnitControl.spawn(game, pirate, freeShips.get(i));
        }
    }

}
