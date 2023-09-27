package com.broll.gainea.server.core.utils

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.actions.required.SelectChoiceAction
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream

object SelectionUtils {
    private fun getPlayerUnitLocations(player: Player?, predicate: Predicate<Unit?>): Set<Location?> {
        return player.getUnits().stream().filter(predicate).map { obj: Unit? -> obj.getLocation() }.collect(Collectors.toSet())
    }

    private fun getPlayersUnitLocations(game: GameContainer, predicate: Predicate<Unit?>): Set<Location?> {
        return game.allPlayers.stream().flatMap { it: Player? -> getPlayerUnitLocations(it, predicate).stream() }.collect(Collectors.toSet())
    }

    private fun getNeutralUnitLocations(game: GameContainer, predicate: Predicate<Unit?>): Set<Location?> {
        return game.objects.stream().filter { it: MapObject? -> it is Unit && predicate.test(it as Unit?) }.map { obj: MapObject? -> obj.getLocation() }.collect(Collectors.toSet())
    }

    private fun getAllUnitLocations(game: GameContainer, predicate: Predicate<Unit?>): Set<Location?> {
        return Stream.concat(getNeutralUnitLocations(game, predicate).stream(), getPlayersUnitLocations(game, predicate).stream()).collect(Collectors.toSet())
    }

    fun selectUnit(game: GameContainer, selectingPlayer: Player?, text: String?, units: List<Unit?>): Unit? {
        return selectUnitFromLocations(game, selectingPlayer, units.stream().map { obj: Unit? -> obj.getLocation() }.collect(Collectors.toSet()), { o: Unit? -> units.contains(o) }, text)
    }

    @JvmOverloads
    fun selectPlayerUnit(game: GameContainer, player: Player, text: String?, predicate: Predicate<Unit?> = Predicate { it: Unit? -> true }): Unit? {
        return selectUnitFromLocations(game, getPlayerUnitLocations(player, predicate), { it: Unit? -> it.getOwner() === player && predicate.test(it) }, text)
    }

    fun selectPlayerUnit(game: GameContainer, selectingPlayer: Player?, unitsFromPlayer: Player?, text: String?, predicate: Predicate<Unit?>): Unit? {
        return selectUnitFromLocations(game, selectingPlayer, getPlayerUnitLocations(unitsFromPlayer, predicate), { it: Unit -> it.owner === unitsFromPlayer && predicate.test(it) }, text)
    }

    @JvmOverloads
    fun selectOtherPlayersUnit(game: GameContainer, player: Player, text: String?, predicate: Predicate<Unit?> = Predicate { it: Unit? -> true }): Unit? {
        return selectUnitFromLocations(game, PlayerUtils.getOtherPlayers(game, player)
                .flatMap { p: Player? -> getPlayerUnitLocations(p, predicate).stream() }.collect(Collectors.toSet()),
                { it: Unit? -> it.getOwner() !== player && it.getOwner() != null && predicate.test(it) }, text)
    }

    fun selectHostileUnit(game: GameContainer, player: Player, text: String?): Unit? {
        return selectUnitFromLocations(game, getAllUnitLocations(game) { it: Unit? -> it.getOwner() !== player }, { it: Unit? -> it.getOwner() !== player }, text)
    }

    @JvmOverloads
    fun selectAnyUnit(game: GameContainer, text: String?, predicate: Predicate<Unit?> = Predicate { it: Unit? -> true }): Unit? {
        return selectUnitFromLocations(game, getAllUnitLocations(game, predicate), predicate, text)
    }

    @JvmOverloads
    fun selectWildMonster(game: GameContainer, text: String?, predicate: Predicate<Monster?> = Predicate { it: Monster? -> true }): Monster? {
        val check = Predicate { it: Unit? -> it.getOwner() == null && it is Monster && predicate.test(it as Monster?) }
        return selectUnitFromLocations(game, getNeutralUnitLocations(game, check), check, text) as Monster?
    }

    fun selectUnitFromLocations(game: GameContainer, locations: Collection<Location?>, predicate: Predicate<Unit?>, text: String?): Unit? {
        val selectingPlayer = game.currentPlayer
        return selectFromLocations(game, selectingPlayer, locations, { it: MapObject? -> it is Unit && predicate.test(it as Unit?) }, text) as Unit?
    }

    fun selectUnitFromLocations(game: GameContainer, selectingPlayer: Player?, locations: Collection<Location?>, predicate: Predicate<Unit>, text: String?): Unit? {
        return selectFromLocations(game, selectingPlayer, locations, { it: MapObject? -> it is Unit && predicate.test(it) }, text) as Unit?
    }

    fun selectFromLocations(game: GameContainer, selectingPlayer: Player?, locations: Collection<Location?>, predicate: Predicate<MapObject?>, text: String?): MapObject? {
        val pickedLocation: Location?
        if (locations.isEmpty()) {
            return null
        }
        pickedLocation = if (locations.size == 1) {
            locations.iterator().next()
        } else {
            val handler = game.reactionHandler.actionHandlers.getHandler(SelectChoiceAction::class.java)
            handler!!.selectLocation(selectingPlayer, text, ArrayList(locations))
        }
        return selectFromLocation(game, selectingPlayer, pickedLocation, predicate, text)
    }

    fun selectFromLocation(game: GameContainer, selectingPlayer: Player?, location: Location?, predicate: Predicate<MapObject?>, text: String?): MapObject? {
        val selection = location.getInhabitants().stream().filter { it: MapObject? -> predicate.test(it) }.collect(Collectors.toList())
        if (selection.isEmpty()) {
            return null
        }
        if (selection.size == 1) {
            return selection[0]
        }
        val handler = game.reactionHandler.actionHandlers.getHandler(SelectChoiceAction::class.java)
        return selection[handler!!.selectObject(selectingPlayer, text, selection.stream().map { obj: MapObject? -> obj!!.nt() }.collect(Collectors.toList()))]
    }
}
