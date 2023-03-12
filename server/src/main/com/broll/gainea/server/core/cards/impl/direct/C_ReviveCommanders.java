package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;

public class C_ReviveCommanders extends DirectlyPlayedCard {

    public C_ReviveCommanders() {
        super(55, "Rückkehr der Legenden", "Gefallene Feldherren kehren zu allen Spielern zurück");
        setDrawChance(0.5f);
    }

    @Override
    protected void play() {
        game.getActivePlayers().stream().filter(it -> !PlayerUtils.isCommanderAlive(it)).forEach(player -> {
            List<Location> locations = player.getControlledLocations();
            Location location = RandomUtils.pickRandom(locations);
            if (location == null) {
                location = LocationUtils.getRandomFree(game.getMap().getAllAreas());
            }
            Soldier commander = player.getFraction().createCommander();
            UnitControl.spawn(game, commander, location);
        });
    }

}
