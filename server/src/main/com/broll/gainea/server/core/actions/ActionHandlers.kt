package com.broll.gainea.server.core.actions

import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_Action_Attack
import com.broll.gainea.net.NT_Action_Card
import com.broll.gainea.net.NT_Action_Move
import com.broll.gainea.net.NT_Action_PlaceUnit
import com.broll.gainea.net.NT_Action_SelectChoice
import com.broll.gainea.net.NT_Action_Shop
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.actions.optional.AttackAction
import com.broll.gainea.server.core.actions.optional.CardAction
import com.broll.gainea.server.core.actions.optional.MoveUnitAction
import com.broll.gainea.server.core.actions.optional.ShopAction
import com.broll.gainea.server.core.actions.required.PlaceUnitAction
import com.broll.gainea.server.core.actions.required.SelectChoiceAction

class ActionHandlers(private val game: Game, val reactionActions: ReactionActions) {
    private val handlers: MutableMap<Class<out NT_Action>, AbstractActionHandler<*, *>> = HashMap()
    private val handlers2: MutableMap<Class<out AbstractActionHandler<*, *>>, AbstractActionHandler<*, *>> =
        HashMap()

    init {
        initHandlers()
    }

    private fun initHandlers() {
        initHandler(NT_Action_PlaceUnit::class.java, PlaceUnitAction())
        initHandler(NT_Action_Move::class.java, MoveUnitAction())
        initHandler(NT_Action_Attack::class.java, AttackAction())
        initHandler(NT_Action_Card::class.java, CardAction())
        initHandler(NT_Action_SelectChoice::class.java, SelectChoiceAction())
        initHandler(NT_Action_Shop::class.java, ShopAction())
    }

    private fun initHandler(
        actionClass: Class<out NT_Action>,
        handler: AbstractActionHandler<*, *>
    ) {
        handler.init(game, reactionActions, this)
        handlers[actionClass] = handler
        handlers2[handler.javaClass] = handler
    }

    fun <T : NT_Action> getHandlerForAction(actionClass: Class<T>): AbstractActionHandler<T, ActionContext<out T>>? {
        return handlers.get(actionClass) as AbstractActionHandler<T, ActionContext<out T>>?
    }

    fun <T : AbstractActionHandler<*, *>> getHandler(handlerClass: Class<T>): T {
        return handlers2[handlerClass] as T
    }
}
