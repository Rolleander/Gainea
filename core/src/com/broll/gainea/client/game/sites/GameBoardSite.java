package com.broll.gainea.client.game.sites;

import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_ObjectMovement;
import com.broll.gainea.server.core.map.Location;
import com.broll.networklib.PackageReceiver;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameBoardSite extends AbstractGameSite {


    @PackageReceiver
    public void received(NT_BoardUpdate update) {
        game.state.update(update);
    }

    @PackageReceiver
    public void received(NT_ObjectMovement movement) {
        List<Integer> objectIds = Arrays.stream(movement.objects).boxed().collect(Collectors.toList());
        List<NT_BoardObject> moveObjects =  game.state.getObjects().stream().filter(it -> objectIds.contains(it.id)).collect(Collectors.toList());
        Location target =  game.state.getMap().getLocation(movement.location);

    }

}
