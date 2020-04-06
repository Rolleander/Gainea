package com.broll.gainea.server.core.actions;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.impl.AttackAction;
import com.broll.gainea.server.core.actions.impl.MoveUnitAction;
import com.broll.gainea.server.core.actions.impl.PlaceUnitAction;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_MoveUnit;
import com.broll.gainea.net.NT_Action_PlaceUnit;

import java.util.HashMap;
import java.util.Map;

public class ActionHandlers {

    private Map<Class<? extends NT_Action>, AbstractActionHandler> handlers = new HashMap<>();
    private GameContainer game;
    private ReactionActions reactionResult;

    public ActionHandlers(GameContainer game, ReactionActions reactionResult) {
        this.game = game;
        this.reactionResult = reactionResult;
        initHandlers();
    }

    public ReactionActions getReactionResult() {
        return reactionResult;
    }

    private void initHandlers() {
        initHandler(NT_Action_PlaceUnit.class, new PlaceUnitAction());
        initHandler(NT_Action_MoveUnit.class, new MoveUnitAction());
        initHandler(NT_Action_Attack.class, new AttackAction());
    }

    private void initHandler(Class<? extends NT_Action> actionClass, AbstractActionHandler handler) {
        handler.init(game, reactionResult,this);
        handlers.put(actionClass, handler);
    }

    public <T extends NT_Action> AbstractActionHandler<T,?> getHandler(Class<T> actionClass) {
        return handlers.get(actionClass);
    }

}
