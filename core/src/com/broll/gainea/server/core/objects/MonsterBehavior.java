package com.broll.gainea.server.core.objects;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.UnitControl;
import com.google.common.collect.Lists;

import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MonsterBehavior {
    RESIDENT((game, monster) -> {
    }),
    RANDOM((game, monster) -> {
        Location target = RandomUtils.pickRandom(getPossibleTargets(monster).collect(Collectors.toList()));
        if (target != null) {
            UnitControl.conquer(game, Lists.newArrayList(monster), target);
        }
    }),
    AGGRESSIVE((game, monster) -> {
        Location target = RandomUtils.pickRandom(getPossibleTargets(monster).filter(it -> !LocationUtils.noControlledUnits(it)).collect(Collectors.toList()));
        if (target != null) {
            UnitControl.conquer(game, Lists.newArrayList(monster), target);
        } else {
            RANDOM.doAction(game, monster);
        }
    }),
    FLEEING((game, monster) -> {
        Location target = RandomUtils.pickRandom(getPossibleTargets(monster).filter(Location::isFree).collect(Collectors.toList()));
        if (target != null) {
            UnitControl.move(game, monster, target);
        }
    });

    private BiConsumer<GameContainer, Monster> action;

    MonsterBehavior(BiConsumer<GameContainer, Monster> action) {
        this.action = action;
    }

    public void doAction(GameContainer game, Monster monster) {
        this.action.accept(game, monster);
    }

    private static Stream<Location> getPossibleTargets(Monster monster) {
        return monster.getLocation().getConnectedLocations().stream().filter(it -> !(it instanceof Ship));
    }

}
