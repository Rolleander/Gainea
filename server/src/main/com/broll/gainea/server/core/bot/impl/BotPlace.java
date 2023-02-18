package com.broll.gainea.server.core.bot.impl;

import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.actions.required.PlaceUnitAction;
import com.broll.gainea.server.core.bot.BotAction;
import com.broll.gainea.server.core.bot.BotUtils;
import com.broll.gainea.server.core.map.Location;

import java.util.List;

public class BotPlace extends BotAction<NT_Action_PlaceUnit> {
    @Override
    protected void handleAction(NT_Action_PlaceUnit action, NT_Reaction reaction) {
        if (action.possibleLocations.length == 1) {
            reaction.option = 0;
            return;
        }
        PlaceUnitAction handler = game.getReactionHandler().getActionHandlers().getHandler(PlaceUnitAction.class);
        List<Location> locations = BotUtils.getLocations(game, action.possibleLocations);
        int index = strategy.chooseUnitPlace(handler.getUnitToPlace(), locations);
        reaction.option = index;
    }

    @Override
    public Class<NT_Action_PlaceUnit> getActionClass() {
        return NT_Action_PlaceUnit.class;
    }

    @Override
    public float score(NT_Action_PlaceUnit action, NT_PlayerTurnActions turn) {
        return -1;
    }
}
