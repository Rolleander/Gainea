package com.broll.gainea.client.ui.ingame.actions;

import com.broll.gainea.client.game.PlayerPerformAction;
import com.broll.gainea.client.ui.utils.ActionListener;
import com.broll.gainea.client.ui.ingame.map.MapAction;
import com.broll.gainea.net.NT_Action;

import java.util.List;

public class RequiredActionContainer {

    private List<MapAction> mapActions;
    private PlayerPerformAction playerPerformAction;
    private ActionListener reactionListener;

    public RequiredActionContainer(List<MapAction> mapActions, PlayerPerformAction playerPerformAction, ActionListener reactionListener) {
        this.mapActions = mapActions;
        this.playerPerformAction = playerPerformAction;
        this.reactionListener = reactionListener;
    }

    public void showMapAction(MapAction action) {
        mapActions.add(action);
    }

    public void reactionResult(NT_Action action, int option, int[] options) {
        playerPerformAction.perform(action, option, options);
        reactionListener.action();
    }

    public void reactionResult(NT_Action action, int option) {
        this.reactionResult(action, option, new int[0]);
    }

}
