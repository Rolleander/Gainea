package com.broll.gainea.server.core.bot.impl;

import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.bot.BotAction;
import com.broll.gainea.server.core.bot.BotUtils;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;

import java.util.List;

public class BotPlace extends BotAction<NT_Action_PlaceUnit> {
    @Override
    protected void handleAction(NT_Action_PlaceUnit action, NT_Reaction reaction) {
        short[] locationOptions = ((NT_Action_PlaceUnit) action).possibleLocations;
        if (locationOptions.length == 1) {
            reaction.option = 0;
            return;
        }
        BattleObject unit = BotUtils.getObject(game, action.unitToPlace);
        List<Location> locations = BotUtils.getLocations(game, locationOptions);
        int index = strategy.chooseUnitPlace(unit, locations);
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
