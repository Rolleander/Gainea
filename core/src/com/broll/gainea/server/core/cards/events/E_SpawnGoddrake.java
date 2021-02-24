package com.broll.gainea.server.core.cards.events;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.cards.EventCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.GodDragon;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class E_SpawnGoddrake extends EventCard {
    public E_SpawnGoddrake() {
        super(59, "Gaineas Herrscher", "Der Götterdrache erscheint!");
    }

    @Override
    protected void play() {
        GodDragon dragon = new GodDragon();
        Location location = LocationUtils.getRandomFree(game.getMap().getAllAreas());
        UnitControl.spawn(game, dragon, location);
    }

    public static boolean isGoddrakeAlive(GameContainer game) {
        return game.getObjects().stream().filter(it -> it instanceof GodDragon).findAny().isPresent();
    }
}
