package com.broll.gainea.test

import com.broll.gainea.net.proto.NT_Action
import com.broll.gainea.net.proto.NT_Action_Move
import com.broll.gainea.net.proto.NT_Action_PlaceUnit
import com.broll.gainea.net.proto.NT_PlayerTurnActions
import com.broll.gainea.server.core.actions.ActionContext
import com.broll.gainea.server.core.actions.RequiredActionContext
import com.broll.gainea.server.init.ContentLibrary
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ProtoMessagesTest {
    @Test
    fun `game state is generated protobuf`() {
        val game = testGame(playersCount = 1)
        val start = game.start(game.allPlayers.first())

        assertEquals(1, start.playersCount)
        assertEquals(game.map.expansions.size, start.expansionsCount)
    }

    @Test
    fun `polymorphic actions use protobuf oneof`() {
        val move = NT_Action_Move.newBuilder().setActionId(7).setLocation(3).build()
        val turns = NT_PlayerTurnActions.newBuilder()
            .addActions(NT_Action.newBuilder().setMove(move)).build()

        assertTrue(turns.actionsList.single().hasMove())
        assertEquals(7, turns.actionsList.single().move.actionId)
    }

    @Test
    fun `required action serializes assigned id`() {
        val inner = object : ActionContext<NT_Action_PlaceUnit>(
            NT_Action_PlaceUnit.newBuilder().addPossibleLocations(3).build()
        ) {}
        val required = RequiredActionContext(inner, "Place unit")

        required.assignActionId(9)

        assertEquals(9, required.nt().action.placeUnit.actionId)
        assertEquals(9, inner.action.actionId)
    }

    @Test
    fun `content library is generated protobuf`() {
        assertTrue(ContentLibrary.nt.cardsCount > 0)
        assertTrue(ContentLibrary.nt.fractionsCount > 0)
    }
}
