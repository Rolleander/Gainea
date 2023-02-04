package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.goals.CustomOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class G_MoveUnit extends CustomOccupyGoal {

    private Area from, to;
    private int distance;
    private List<BattleObject> walkingUnits = new ArrayList<>();

    public G_MoveUnit() {
        this(GoalDifficulty.EASY, 6);
    }

    public G_MoveUnit(GoalDifficulty difficulty, int distance) {
        super(difficulty, "");
        this.distance = distance;
    }

    private void findPath() {
        from = (Area) LocationUtils.getRandomFree(game.getMap().getAllAreas());
        int step = 0;
        List<Location> visited = new ArrayList<>();
        List<Location> remaining = from.getWalkableNeighbours();
        do {
            step++;
            if (step == distance) {
                Collections.shuffle(remaining);
                to = (Area) remaining.stream().filter(it -> it instanceof Area).findFirst().orElse(null);
                if (to != null) {
                    return;
                }
            }
            visited.addAll(remaining);
            remaining = remaining.stream().flatMap(it -> it.getWalkableNeighbours().stream()).collect(Collectors.toList());
            remaining.removeAll(visited);
        } while (!remaining.isEmpty());
    }

    @Override
    public boolean init(GameContainer game, Player player) {
        List<Area> startingPoints = game.getMap().getAllAreas();
        Collections.shuffle(startingPoints);
        while (!startingPoints.isEmpty() && to == null) {
            from = startingPoints.remove(0);
            findPath();
        }
        if (to == null) {
            return false;
        }
        this.text = "Bewege eine Einheit von " + from.getName() + " nach " + to.getName();
        setProgressionGoal(distance);
        return super.init(game, player);
    }

    @Override
    public void check() {
        walkingUnits.removeIf(BattleObject::isDead);
        walkingUnits.addAll(PlayerUtils.getUnits(player, from));
        if (walkingUnits.stream().anyMatch(it -> it.getLocation() == to)) {
            updateProgression(distance);
            success();
        } else {
            int closestDistance = walkingUnits.stream().map(it -> LocationUtils.getWalkingDistance(it.getLocation(), to)).reduce(distance, Math::min);
            updateProgression(distance - closestDistance);
        }
    }
}
