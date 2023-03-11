package com.broll.gainea.server.core.utils;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.required.SelectChoiceAction;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class SelectionUtils {
    private SelectionUtils() {

    }

    public static Unit selectPlayerUnit(GameContainer game, Player player, String text) {
        return selectPlayerUnit(game, player, text, it -> true);
    }

    public static Unit selectPlayerUnit(GameContainer game, Player player, String text, Predicate<Unit> predicate) {
        return selectUnitFromLocations(game, player.getControlledLocations(), it -> it.getOwner() == player && predicate.test(it), text);
    }

    public static Unit selectPlayerUnit(GameContainer game, Player selectingPlayer, Player unitsFromPlayer, String text, Predicate<Unit> predicate) {
        return selectUnitFromLocations(game, selectingPlayer, unitsFromPlayer.getControlledLocations(), it -> it.getOwner() == unitsFromPlayer && predicate.test(it), text);
    }

    public static Unit selectOtherPlayersUnit(GameContainer game, Player player, String text) {
        return selectOtherPlayersUnit(game, player, text, it -> true);
    }

    public static Unit selectOtherPlayersUnit(GameContainer game, Player player, String text, Predicate<Unit> predicate) {
        Set<Location> locations = new HashSet<>();
        PlayerUtils.getOtherPlayers(game, player).forEach(otherPlayer -> locations.addAll(otherPlayer.getControlledLocations()));
        return selectUnitFromLocations(game, new ArrayList<>(locations), it -> it.getOwner() != player && it.getOwner() != null && predicate.test(it), text);
    }

    public static Unit selectEnemyUnit(GameContainer game, Player player, String text) {
        Set<Location> locations = new HashSet<>();
        PlayerUtils.getOtherPlayers(game, player).forEach(otherPlayer -> locations.addAll(otherPlayer.getControlledLocations()));
        locations.addAll(game.getObjects().stream().filter(it -> it instanceof Monster).map(MapObject::getLocation).collect(Collectors.toList()));
        return selectUnitFromLocations(game, new ArrayList<>(locations), it -> it.getOwner() != player, text);
    }

    public static Unit selectAnyUnit(GameContainer game, String text) {
        return selectAnyUnit(game, text, it -> true);
    }

    public static Unit selectAnyUnit(GameContainer game, String text, Predicate<Unit> predicate) {
        Set<Location> locations = new HashSet<>();
        locations.addAll(game.getObjects().stream().map(MapObject::getLocation).collect(Collectors.toList()));
        game.getAllPlayers().forEach(player -> locations.addAll(player.getControlledLocations()));
        return selectUnitFromLocations(game, new ArrayList<>(locations), predicate, text);
    }

    public static Monster selectWildMonster(GameContainer game, String text) {
        return selectWildMonster(game, text, it -> true);
    }

    public static Monster selectWildMonster(GameContainer game, String text, Predicate<Unit> predicate) {
        return (Monster) selectUnitFromLocations(game, game.getObjects().stream().filter(it -> it instanceof Monster).map(MapObject::getLocation).distinct().collect(Collectors.toList()),
                it -> it.getOwner() == null && it instanceof Monster && predicate.test(it), text);
    }

    public static Unit selectUnitFromLocations(GameContainer game, List<Location> locations, Predicate<Unit> predicate, String text) {
        Player selectingPlayer = game.getCurrentPlayer();
        return (Unit) selectFromLocations(game, selectingPlayer, locations, it -> it instanceof Unit && predicate.test((Unit) it), text);
    }

    public static Unit selectUnitFromLocations(GameContainer game, Player selectingPlayer, List<Location> locations, Predicate<Unit> predicate, String text) {
        return (Unit) selectFromLocations(game, selectingPlayer, locations, it -> it instanceof Unit && predicate.test((Unit) it), text);
    }

    public static MapObject selectFromLocations(GameContainer game, Player selectingPlayer, List<Location> locations, Predicate<MapObject> predicate, String text) {
        Location pickedLocation;
        if (locations.isEmpty()) {
            return null;
        }
        if (locations.size() == 1) {
            pickedLocation = locations.get(0);
        } else {
            SelectChoiceAction handler = game.getReactionHandler().getActionHandlers().getHandler(SelectChoiceAction.class);
            pickedLocation = handler.selectLocation(selectingPlayer, text, locations);
        }
        return selectFromLocation(game, selectingPlayer, pickedLocation, predicate, text);
    }

    public static MapObject selectFromLocation(GameContainer game, Player selectingPlayer, Location location, Predicate<MapObject> predicate, String text) {
        List<MapObject> selection = location.getInhabitants().stream().filter(it -> predicate.test(it)).collect(Collectors.toList());
        if (selection.isEmpty()) {
            return null;
        }
        if (selection.size() == 1) {
            return selection.get(0);
        }
        SelectChoiceAction handler = game.getReactionHandler().getActionHandlers().getHandler(SelectChoiceAction.class);
        return selection.get(handler.selectObject(selectingPlayer, text, selection.stream().map(MapObject::nt).collect(Collectors.toList())));
    }
}
