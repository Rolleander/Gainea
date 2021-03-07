package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.StreamUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.ArrayList;
import java.util.List;

public class C_War extends DirectlyPlayedCard {

    public C_War() {
        super(9, "Wilde Schlacht", "Jeder Spieler wählt ein feindlich besetztes Land. Jeder Einheit darauf wird 1 Schaden zugefügt.");
        setDrawChance(0.5f);
    }

    @Override
    protected void play() {
        PlayerUtils.iteratePlayers(game, 500, player -> {
            List<Location> locations = new ArrayList<>(PlayerUtils.getHostileLocations(game, player));
            if (!locations.isEmpty()) {
                Location location = selectHandler.selectLocation("Wähle feindliches Land", locations);
                StreamUtils.safeForEach(location.getInhabitants().stream().filter(it -> it instanceof BattleObject).map(it -> (BattleObject) it),
                        unit -> UnitControl.damage(game, unit, 1));
            }
        });
    }

}
