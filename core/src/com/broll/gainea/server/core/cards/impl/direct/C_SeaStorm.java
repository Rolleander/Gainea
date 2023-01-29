package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.StreamUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class C_SeaStorm extends DirectlyPlayedCard {

    public C_SeaStorm() {
        super(74, "Seesturm", "Alle Schiffe wechseln ihre Besetzer zufällig mit anderen Schiffen der gleichen Karte");
    }

    @Override
    protected void play() {
        game.getMap().getExpansions().forEach(expansion -> {
            List<Ship> fullShips = expansion.getAllShips().stream().filter(it -> !it.isFree()).collect(Collectors.toList());
            List<Set<MapObject>> shipsWithUnits = fullShips.stream().map(Location::getInhabitants).collect(Collectors.toList());
            fullShips.forEach(it -> it.getInhabitants().clear());
            shipsWithUnits.forEach(shipUnits -> {
                Location newShip = RandomUtils.pickRandom(expansion.getAllShips().stream().filter(Location::isFree).collect(Collectors.toList()));
                UnitControl.move(game, new ArrayList<>(shipUnits), newShip);
            });
        });
    }

}
