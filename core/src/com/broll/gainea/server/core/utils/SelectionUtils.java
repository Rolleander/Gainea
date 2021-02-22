package com.broll.gainea.server.core.utils;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.required.SelectChoiceAction;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
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

    public static BattleObject selectPlayerUnit(GameContainer game, Player player, String text) {
        return selectPlayerUnit(game, player, text, it -> true);
    }

    public static BattleObject selectPlayerUnit(GameContainer game, Player player, String text, Predicate<BattleObject> predicate) {
        return selectUnitFromLocations(game, player.getControlledLocations(), it -> it.getOwner() == player && predicate.test(it), text);
    }

    public static BattleObject selectOtherPlayersUnit(GameContainer game, Player player, String text) {
        return selectOtherPlayersUnit(game, player, text, it->true);
    }

    public static BattleObject selectOtherPlayersUnit(GameContainer game, Player player, String text, Predicate<BattleObject> predicate) {
        Set<Location> locations = new HashSet<>();
        PlayerUtils.getOtherPlayers(game, player).forEach(otherPlayer -> locations.addAll(otherPlayer.getControlledLocations()));
        return selectUnitFromLocations(game, new ArrayList<>(locations), it -> it.getOwner() != player && it.getOwner() != null && predicate.test(it), text);
    }

    public static BattleObject selectAnyUnit(GameContainer game, String text) {
        return selectAnyUnit(game, text, it -> true);
    }

    public static BattleObject selectAnyUnit(GameContainer game, String text, Predicate<BattleObject> predicate) {
        Set<Location> locations = new HashSet<>();
        locations.addAll(game.getObjects().stream().map(MapObject::getLocation).collect(Collectors.toList()));
        game.getPlayers().forEach(player -> locations.addAll(player.getControlledLocations()));
        return selectUnitFromLocations(game, new ArrayList<>(locations), predicate, text);
    }

    public static BattleObject selectWildUnit(GameContainer game, String text) {
        return selectWildUnit(game, text, it -> true);
    }

    public static BattleObject selectWildUnit(GameContainer game, String text, Predicate<BattleObject> predicate) {
        return selectUnitFromLocations(game, game.getObjects().stream().map(MapObject::getLocation).distinct().collect(Collectors.toList()),
                it -> it.getOwner() == null && predicate.test(it), text);
    }

    private static BattleObject selectUnitFromLocations(GameContainer game, List<Location> locations, Predicate<BattleObject> predicate, String text) {
        return (BattleObject) selectFromLocations(game, locations, it -> it instanceof BattleObject && predicate.test((BattleObject) it), text);
    }

    private static MapObject selectFromLocations(GameContainer game, List<Location> locations, Predicate<MapObject> predicate, String text) {
        Location pickedLocation;
        if (locations.size() == 1) {
            pickedLocation = locations.get(0);
        } else {
            SelectChoiceAction handler = game.getReactionHandler().getActionHandlers().getHandler(SelectChoiceAction.class);
            pickedLocation = handler.selectLocation(text, locations);
        }
        return selectFromLocation(game, pickedLocation, predicate, text);
    }

    private static MapObject selectFromLocation(GameContainer game, Location location, Predicate<MapObject> predicate, String text) {
        List<MapObject> selection = location.getInhabitants().stream().filter(it -> predicate.test(it)).collect(Collectors.toList());
        SelectChoiceAction handler = game.getReactionHandler().getActionHandlers().getHandler(SelectChoiceAction.class);
        return selection.get(handler.selectObject(text, selection));
    }
}
