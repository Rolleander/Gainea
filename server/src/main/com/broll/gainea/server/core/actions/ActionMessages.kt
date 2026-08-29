package com.broll.gainea.server.core.actions

import com.broll.gainea.net.proto.*
import com.google.protobuf.Message

fun Message.actionId(): Int = when (this) {
    is NT_Action_Attack -> actionId
    is NT_Action_Card -> actionId
    is NT_Action_Move -> actionId
    is NT_Action_PlaceUnit -> actionId
    is NT_Action_SelectChoice -> actionId
    is NT_Action_Shop -> actionId
    else -> error("Unsupported action message ${javaClass.name}")
}

@Suppress("UNCHECKED_CAST")
fun <T : Message> T.withActionId(id: Int): T = when (this) {
    is NT_Action_Attack -> toBuilder().setActionId(id).build()
    is NT_Action_Card -> toBuilder().setActionId(id).build()
    is NT_Action_Move -> toBuilder().setActionId(id).build()
    is NT_Action_PlaceUnit -> toBuilder().setActionId(id).build()
    is NT_Action_SelectChoice -> toBuilder().setActionId(id).build()
    is NT_Action_Shop -> toBuilder().setActionId(id).build()
    else -> error("Unsupported action message ${javaClass.name}")
} as T

fun Message.asAction(): NT_Action = when (this) {
    is NT_Action_Attack -> NT_Action.newBuilder().setAttack(this).build()
    is NT_Action_Card -> NT_Action.newBuilder().setCard(this).build()
    is NT_Action_Move -> NT_Action.newBuilder().setMove(this).build()
    is NT_Action_PlaceUnit -> NT_Action.newBuilder().setPlaceUnit(this).build()
    is NT_Action_SelectChoice -> NT_Action.newBuilder().setSelectChoice(this).build()
    is NT_Action_Shop -> NT_Action.newBuilder().setShop(this).build()
    else -> error("Unsupported action message ${javaClass.name}")
}

fun NT_Action.message(): Message = when (valueCase) {
    NT_Action.ValueCase.ATTACK -> attack
    NT_Action.ValueCase.CARD -> card
    NT_Action.ValueCase.MOVE -> move
    NT_Action.ValueCase.PLACE_UNIT -> placeUnit
    NT_Action.ValueCase.SELECT_CHOICE -> selectChoice
    NT_Action.ValueCase.SHOP -> shop
    else -> error("Action has no value")
}
