package com.broll.gainea.server.core.utils

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.actions.required.SelectChoiceAction
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral

object SelectionUtils {
    private fun getPlayerUnitLocations(player: Player, predicate: (Unit) -> Boolean) = player.units.filter(predicate).map { it.location }.toHashSet()

    private fun getPlayersUnitLocations(game: GameContainer, predicate: (Unit) -> Boolean) = game.allPlayers.flatMap { it.units.filter(predicate) }.map { it.location }.toHashSet()

    private fun getNeutralUnitLocations(game: GameContainer, predicate: (Unit) -> Boolean) = game.objects.filterIsInstance(Unit::class.java).filter(predicate).map { it.location }.toHashSet()

    private fun getAllUnitLocations(game: GameContainer, predicate: (Unit) -> Boolean) =
            setOf(getPlayersUnitLocations(game, predicate), getNeutralUnitLocations(game, predicate)).flatten().toHashSet()

    fun selectUnit(game: GameContainer, selectingPlayer: Player, text: String, units: List<Unit>): Unit? =
            selectUnitFromLocations(game, selectingPlayer, units.map { it.location }, { unit -> units.contains(unit) }, text)

    fun selectPlayerUnit(game: GameContainer, player: Player, text: String, predicate: (Unit) -> Boolean = { true }) =
            selectUnitFromLocations(game, getPlayerUnitLocations(player, predicate), { it.owner === player && predicate(it) }, text)


    fun selectPlayerUnit(game: GameContainer, selectingPlayer: Player, unitsFromPlayer: Player, text: String, predicate: (Unit) -> Boolean = { true }) =
            selectUnitFromLocations(game, selectingPlayer, getPlayerUnitLocations(unitsFromPlayer, predicate), { it.owner === unitsFromPlayer && predicate(it) }, text)


    fun selectOtherPlayersUnit(game: GameContainer, player: Player, text: String, predicate: (Unit) -> Boolean = { true }) =
            selectUnitFromLocations(game, PlayerUtils.getOtherPlayers(game, player)
                    .flatMap { p -> getPlayerUnitLocations(p, predicate) },
                    { it.owner !== player && !it.owner.isNeutral() && predicate(it) }, text)


    fun selectHostileUnit(game: GameContainer, player: Player, text: String): Unit? {
        return selectUnitFromLocations(game, getAllUnitLocations(game) { it.owner !== player }, { it.owner !== player }, text)
    }

    fun selectAnyUnit(game: GameContainer, text: String, predicate: (Unit) -> Boolean = { true }) =
            selectUnitFromLocations(game, getAllUnitLocations(game, predicate), predicate, text)


    fun selectWildMonster(game: GameContainer, text: String, predicate: (Monster) -> Boolean = { true }): Monster? {
        val check = { it: Unit -> it.owner.isNeutral() && it is Monster && predicate(it) }
        return selectUnitFromLocations(game, getNeutralUnitLocations(game, check), check, text) as Monster?
    }

    fun selectUnitFromLocations(game: GameContainer, locations: Collection<Location>, predicate: (Unit) -> Boolean, text: String): Unit? {
        val selectingPlayer = game.currentPlayer
        return selectFromLocations(game, selectingPlayer, locations, { it: MapObject? -> it is Unit && predicate(it) }, text) as Unit?
    }

    fun selectUnitFromLocations(game: GameContainer, selectingPlayer: Player, locations: Collection<Location>, predicate: (Unit) -> Boolean, text: String): Unit? {
        return selectFromLocations(game, selectingPlayer, locations, { it is Unit && predicate(it) }, text) as Unit?
    }

    fun selectFromLocations(game: GameContainer, selectingPlayer: Player, locations: Collection<Location>, predicate: (MapObject) -> Boolean, text: String): MapObject? {
        if (locations.isEmpty()) {
            return null
        }
        val pickedLocation = if (locations.size == 1) {
            locations.first()
        } else {
            val handler = game.reactionHandler.actionHandlers.getHandler(SelectChoiceAction::class.java)
            handler.selectLocation(selectingPlayer, text, ArrayList(locations))
        }
        return selectFromLocation(game, selectingPlayer, pickedLocation, predicate, text)
    }

    fun selectFromLocation(game: GameContainer, selectingPlayer: Player, location: Location, predicate: (MapObject) -> Boolean, text: String): MapObject? {
        val selection = location.inhabitants.filter(predicate)
        if (selection.isEmpty()) {
            return null
        }
        if (selection.size == 1) {
            return selection[0]
        }
        val handler = game.reactionHandler.actionHandlers.getHandler(SelectChoiceAction::class.java)
        return selection[handler.selectObject(selectingPlayer, text, selection.map { it.nt() })]
    }
}
