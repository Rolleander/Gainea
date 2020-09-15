package com.broll.gainea.server.core.actions;

import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Action_SelectChoice;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.impl.AttackAction;
import com.broll.gainea.server.core.actions.impl.CardAction;
import com.broll.gainea.server.core.actions.impl.MoveUnitAction;
import com.broll.gainea.server.core.actions.impl.PlaceUnitAction;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.server.core.actions.impl.SelectChoiceAction;

import java.util.HashMap;
import java.util.Map;


public class ActionHandlers {

    private Map<Class<? extends NT_Action>, AbstractActionHandler> handlers = new HashMap<>();
    private Map<Class<? extends AbstractActionHandler>, AbstractActionHandler> handlers2 = new HashMap<>();

    private GameContainer game;
    private ReactionActions reactionResult;

    public ActionHandlers(GameContainer game, ReactionActions reactionResult) {
        this.game = game;
        this.reactionResult = reactionResult;
        initHandlers();
    }

    public ReactionActions getReactionActions() {
        return reactionResult;
    }

    private void initHandlers() {
        initHandler(NT_Action_PlaceUnit.class, new PlaceUnitAction());
        initHandler(NT_Action_Move.class, new MoveUnitAction());
        initHandler(NT_Action_Attack.class, new AttackAction());
        initHandler(NT_Action_Card.class, new CardAction());
        initHandler(NT_Action_SelectChoice.class, new SelectChoiceAction());
    }

    private void initHandler(Class<? extends NT_Action> actionClass, AbstractActionHandler handler) {
        handler.init(game, reactionResult, this);
        handlers.put(actionClass, handler);
        handlers2.put(handler.getClass(), handler);
    }

    public <T extends NT_Action> AbstractActionHandler<T, ?> getHandlerForAction(Class<T> actionClass) {
        return handlers.get(actionClass);
    }

    public <T extends AbstractActionHandler> T getHandler(Class<T> handlerClass) {
        return (T) handlers2.get(handlerClass);
    }

}
