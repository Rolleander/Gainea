package com.broll.gainea.server.core.actions

import com.broll.gainea.net.NT_PlayerTurnActions
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.actions.optional.AttackAction
import com.broll.gainea.server.core.actions.optional.CardAction
import com.broll.gainea.server.core.actions.optional.MoveUnitAction
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.PlayerUtils
import org.apache.commons.collections4.map.MultiValueMap

class TurnBuilder(private val game: GameContainer, private val actionHandlers: ActionHandlers) {
    fun build(player: Player): NT_PlayerTurnActions {
        game.clearActions()
        val turn = NT_PlayerTurnActions()
        val actions = mutableListOf<ActionContext<*>>()
        actions.addAll(buildMoveAndAttackActions(player))
        actions.addAll(buildCardActions(player))
        turn.actions = actions.map { it.action }.toTypedArray()
        actions.forEach { game.pushAction(it) }
        return turn
    }

    private fun buildMoveAndAttackActions(player: Player): List<ActionContext<*>> {
        val actions = mutableListOf<ActionContext<*>>()
        val attackHandler = actionHandlers.getHandler(AttackAction::class.java)
        val moveHandler = actionHandlers.getHandler(MoveUnitAction::class.java)
        val moveableTo = MultiValueMap<Location, Unit>()
        val attackableTo = MultiValueMap<Location, Unit>()
        player.units.filter { it.controllable }.forEach { unit ->
            val walkableLocations = unit.location!!.connectedLocations.filter { unit.canMoveTo(it) }.toMutableList()
            val attackableLocations = walkableLocations.filter { PlayerUtils.hasHostileArmy(player, it) }
            walkableLocations.removeAll(attackableLocations)
            if (unit.hasRemainingAttack()) {
                attackableLocations.forEach { attackableTo.put(it, unit) }
            }
            if (unit.hasRemainingMove()) {
                walkableLocations.forEach { moveableTo.put(it, unit) }
            }
        }
        moveableTo.keys.forEach {
            actions += moveHandler.move(moveableTo.getCollection(it).toList(), it)
        }
        attackableTo.keys.forEach {
            actions += attackHandler.attack(attackableTo.getCollection(it).toList(), it)
        }
        return actions
    }

    private fun buildCardActions(player: Player): List<ActionContext<*>> {
        val cardAction = actionHandlers.getHandler(CardAction::class.java)
        return player.cardHandler.cards.filter { it.isPlayable }.map { cardAction.playableCard(player, it) }
    }
}
