package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.RequiredActionContext;
import com.broll.gainea.server.core.actions.impl.SelectChoiceAction;
import com.broll.gainea.server.core.cards.AbstractCard;

public class C_SuspendPlayer extends AbstractCard {
    public C_SuspendPlayer() {
        super("Aussetzen", "Ein Spieler deiner Wahl muss eine Runde aussetzen");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    public void play(ActionHandlers actionHandlers) {
        SelectChoiceAction handler = actionHandlers.getHandler(SelectChoiceAction.class);
        actionHandlers.getReactionActions().requireAction(owner, new RequiredActionContext<>(handler.selectOtherPlayer(owner, selectedPlayer -> {
            selectedPlayer.skipRounds(1);
        }), "Spieler der Aussetzen muss:"));
    }
}
