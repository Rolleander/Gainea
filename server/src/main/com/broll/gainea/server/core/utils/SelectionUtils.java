package com.broll.gainea.server.core.utils;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.required.SelectChoiceAction;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SelectionUtils {
    private SelectionUtils() {

    }

    private static Set<Location> getPlayerUnitLocations(Player player, Predicate<Unit> predicate) {
        return player.getUnits().stream().filter(predicate).map(Unit::getLocation).collect(Collectors.toSet());
    }

    private static Set<Location> getPlayersUnitLocations(GameContainer game, Predicate<Unit> predicate) {
        return game.getAllPlayers().stream().flatMap(it -> getPlayerUnitLocations(it, predicate).stream()).collect(Collectors.toSet());
    }

    private static Set<Location> getNeutralUnitLocations(GameContainer game, Predicate<Unit> predicate) {
        return game.getObjects().stream().filter(it -> it instanceof Unit && predicate.test((Unit) it)).map(MapObject::getLocation).collect(Collectors.toSet());
    }

    private static Set<Location> getAllUnitLocations(GameContainer game, Predicate<Unit> predicate) {
        return Stream.concat(getNeutralUnitLocations(game, predicate).stream(), getPlayersUnitLocations(game, predicate).stream()).collect(Collectors.toSet());
    }

    public static Unit selectUnit(GameContainer game, Player selectingPlayer, String text, List<Unit> units) {
        return selectUnitFromLocations(game, selectingPlayer, units.stream().map(Unit::getLocation).collect(Collectors.toSet()), units::contains, text);
    }

    public static Unit selectPlayerUnit(GameContainer game, Player player, String text) {
        return selectPlayerUnit(game, player, text, it -> true);
    }

    public static Unit selectPlayerUnit(GameContainer game, Player player, String text, Predicate<Unit> predicate) {
        return selectUnitFromLocations(game, getPlayerUnitLocations(player, predicate), it -> it.getOwner() == player && predicate.test(it), text);
    }

    public static Unit selectPlayerUnit(GameContainer game, Player selectingPlayer, Player unitsFromPlayer, String text, Predicate<Unit> predicate) {
        return selectUnitFromLocations(game, selectingPlayer, getPlayerUnitLocations(unitsFromPlayer, predicate), it -> it.getOwner() == unitsFromPlayer && predicate.test(it), text);
    }

    public static Unit selectOtherPlayersUnit(GameContainer game, Player player, String text) {
        return selectOtherPlayersUnit(game, player, text, it -> true);
    }

    public static Unit selectOtherPlayersUnit(GameContainer game, Player player, String text, Predicate<Unit> predicate) {
        return selectUnitFromLocations(game, PlayerUtils.getOtherPlayers(game, player)
                        .flatMap(p -> getPlayerUnitLocations(p, predicate).stream()).collect(Collectors.toSet()),
                it -> it.getOwner() != player && it.getOwner() != null && predicate.test(it), text);
    }

    public static Unit selectHostileUnit(GameContainer game, Player player, String text) {
        return selectUnitFromLocations(game, getAllUnitLocations(game, it -> it.getOwner() != player), it -> it.getOwner() != player, text);
    }

    public static Unit selectAnyUnit(GameContainer game, String text) {
        return selectAnyUnit(game, text, it -> true);
    }

    public static Unit selectAnyUnit(GameContainer game, String text, Predicate<Unit> predicate) {
        return selectUnitFromLocations(game, getAllUnitLocations(game, predicate), predicate, text);
    }

    public static Monster selectWildMonster(GameContainer game, String text) {
        return selectWildMonster(game, text, it -> true);
    }

    public static Monster selectWildMonster(GameContainer game, String text, Predicate<Monster> predicate) {
        Predicate<Unit> check = it -> it.getOwner() == null && it instanceof Monster && predicate.test((Monster) it);
        return (Monster) selectUnitFromLocations(game, getNeutralUnitLocations(game, check), check, text);
    }

    public static Unit selectUnitFromLocations(GameContainer game, Collection<Location> locations, Predicate<Unit> predicate, String text) {
        Player selectingPlayer = game.getCurrentPlayer();
        return (Unit) selectFromLocations(game, selectingPlayer, locations, it -> it instanceof Unit && predicate.test((Unit) it), text);
    }

    public static Unit selectUnitFromLocations(GameContainer game, Player selectingPlayer, Collection<Location> locations, Predicate<Unit> predicate, String text) {
        return (Unit) selectFromLocations(game, selectingPlayer, locations, it -> it instanceof Unit && predicate.test((Unit) it), text);
    }

    public static MapObject selectFromLocations(GameContainer game, Player selectingPlayer, Collection<Location> locations, Predicate<MapObject> predicate, String text) {
        Location pickedLocation;
        if (locations.isEmpty()) {
            return null;
        }
        if (locations.size() == 1) {
            pickedLocation = locations.iterator().next();
        } else {
            SelectChoiceAction handler = game.getReactionHandler().getActionHandlers().getHandler(SelectChoiceAction.class);
            pickedLocation = handler.selectLocation(selectingPlayer, text, new ArrayList<>(locations));
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
