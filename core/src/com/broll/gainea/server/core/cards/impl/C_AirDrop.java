package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.impl.PlaceUnitAction;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.IteratePlayersProcess;
import com.broll.gainea.server.core.utils.UnitUtils;

import java.util.stream.Collectors;

public class C_AirDrop extends DirectlyPlayedCard {
    public C_AirDrop() {
        super("Verstärkung", "Jeder Spieler platziert eine Einheit!");
    }

    @Override
    public void play(ActionHandlers actionHandlers) {
        IteratePlayersProcess.run(game, (player, iterator) -> {
            PlaceUnitAction handler = game.getReactionHandler().getActionHandlers().getHandler(PlaceUnitAction.class);
            handler.placeSoldier(player.getControlledLocations().collect(Collectors.toList()), (unit, location) ->
                    iterator.nextPlayer()
            );
        });
    }

}
