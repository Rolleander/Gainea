package com.broll.gainea.server.core.utils

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.actions.required.SelectChoiceAction
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral

private fun Player.getPlayerUnitLocations(predicate: (Unit) -> Boolean) =
    units.filter(predicate).map { it.location }.toHashSet()

private fun Game.getPlayersUnitLocations(predicate: (Unit) -> Boolean) =
    allPlayers.flatMap { it.units.filter(predicate) }.map { it.location }.toHashSet()

private fun Game.getNeutralUnitLocations(predicate: (Unit) -> Boolean) =
    objects.filterIsInstance(Unit::class.java).filter(predicate).map { it.location }.toHashSet()

private fun Game.getAllUnitLocations(predicate: (Unit) -> Boolean) =
    setOf(getPlayersUnitLocations(predicate), getNeutralUnitLocations(predicate)).flatten()
        .toHashSet()

fun Game.selectUnit(selectingPlayer: Player, text: String, units: List<Unit>): Unit? =
    selectUnitFromLocations(
        selectingPlayer,
        units.map { it.location },
        { unit -> units.contains(unit) },
        text
    )

fun Game.selectPlayerUnit(player: Player, text: String, predicate: (Unit) -> Boolean = { true }) =
    selectUnitFromLocations(
        player.getPlayerUnitLocations(predicate),
        { it.owner == player && predicate(it) },
        text
    )


fun Game.selectPlayerUnit(
    selectingPlayer: Player,
    unitsFromPlayer: Player,
    text: String,
    predicate: (Unit) -> Boolean = { true }
) =
    selectUnitFromLocations(
        selectingPlayer,
        unitsFromPlayer.getPlayerUnitLocations(predicate),
        { it.owner === unitsFromPlayer && predicate(it) },
        text
    )


fun Game.selectOtherPlayersUnit(
    player: Player,
    text: String,
    predicate: (Unit) -> Boolean = { true }
) =
    selectUnitFromLocations(
        getOtherPlayers(player)
            .flatMap { p -> p.getPlayerUnitLocations(predicate) },
        { it.owner !== player && !it.owner.isNeutral() && predicate(it) }, text
    )


fun Game.selectHostileUnit(player: Player, text: String) =
    selectUnitFromLocations(
        getAllUnitLocations() { player.isHostile(it) },
        { player.isHostile(it) },
        text
    )


fun Game.selectAnyUnit(text: String, predicate: (Unit) -> Boolean = { true }) =
    selectUnitFromLocations(getAllUnitLocations(predicate), predicate, text)


fun Game.selectWildMonster(text: String, predicate: (Monster) -> Boolean = { true }): Monster? {
    val check = { it: Unit -> it.owner.isNeutral() && it is Monster && predicate(it) }
    return selectUnitFromLocations(getNeutralUnitLocations(check), check, text) as Monster?
}

fun Game.selectUnitFromLocations(
    locations: Collection<Location>,
    predicate: (Unit) -> Boolean,
    text: String
) = selectFromLocations(
    currentPlayer,
    locations,
    { it: MapObject? -> it is Unit && predicate(it) },
    text
) as Unit?


fun Game.selectUnitFromLocations(
    selectingPlayer: Player,
    locations: Collection<Location>,
    predicate: (Unit) -> Boolean,
    text: String
) = selectFromLocations(selectingPlayer, locations, { it is Unit && predicate(it) }, text) as Unit?


fun Game.selectFromLocations(
    selectingPlayer: Player,
    locations: Collection<Location>,
    predicate: (MapObject) -> Boolean,
    text: String
): MapObject? {
    if (locations.isEmpty()) {
        return null
    }
    val distinctLocations = locations.distinct()
    val pickedLocation = if (distinctLocations.size == 1) {
        locations.first()
    } else {
        val handler = reactionHandler.actionHandlers.getHandler(SelectChoiceAction::class.java)
        handler.selectLocation(selectingPlayer, text, ArrayList(distinctLocations))
    }
    return selectFromLocation(selectingPlayer, pickedLocation, predicate, text)
}

fun Game.selectFromLocation(
    selectingPlayer: Player,
    location: Location,
    predicate: (MapObject) -> Boolean,
    text: String
): MapObject? {
    val selection = location.inhabitants.filter(predicate)
    if (selection.isEmpty()) {
        return null
    }
    if (selection.size == 1) {
        return selection[0]
    }
    val handler = reactionHandler.actionHandlers.getHandler(SelectChoiceAction::class.java)
    return selection[handler.selectObject(selectingPlayer, text, selection.map { it.nt() })]
}

