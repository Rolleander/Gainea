package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_HolyJustice extends DirectlyPlayedCard {
    public C_HolyJustice() {
        super(7, "Göttliche Gerechtigkeit", "Spieler mit weniger Einheiten erhalten zusätzliche Soldaten");
        setDrawChance(0.6f);
    }


    @Override
    protected void play() {
        int avg = (int) Math.ceil((float) game.getActivePlayers().stream().mapToInt(it -> it.getUnits().size()).sum()
                / game.getActivePlayers().size());
        PlayerUtils.iteratePlayers(game, 0, player -> {
            int below = avg - player.getUnits().size();
            for (int i = 0; i < below; i++) {
                Location location = RandomUtils.pickRandom(player.getControlledLocations());
                if (location == null) {
                    location = LocationUtils.getRandomFree(game.getMap().getAllAreas());
                }
                UnitControl.spawn(game, player.getFraction().createSoldier(), location);
            }
        });
    }


}
