package com.broll.gainea.client.network.sites;

import com.broll.gainea.client.game.PlayerPerformAction;
import com.broll.gainea.client.network.ProtoMapper;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_PlayerAction;
import com.broll.gainea.net.proto.NT_Reaction;
import com.broll.mpnll.client.site.MessageReceiverRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameActionSite extends AbstractGameSite {
    private static final Logger Log = LoggerFactory.getLogger(GameActionSite.class);
    public static NT_PlayerAction CURRENT_PLAYER_ACTION;
    private final PlayerPerformAction playerPerformAction = this::performAction;

    @Override
    protected void registerReceivers(MessageReceiverRegistry registry) {
        on(registry, com.broll.gainea.net.proto.NT_PlayerAction.class, this::received);
    }

    private void received(com.broll.gainea.net.proto.NT_PlayerAction message) {
        NT_PlayerAction action = new NT_PlayerAction();
        action.text = message.getText();
        action.action = ProtoMapper.action(message.getAction());
        Log.info("received action " + action);
        CURRENT_PLAYER_ACTION = action;
        game.state.performAction(action.action, playerPerformAction);
    }

    public void performAction(NT_Action action, int option, int[] options) {
        send(NT_Reaction.newBuilder().setActionId(action.actionId).setOption(option).addAllOptions(ints(options)).build());
        Log.info("sent reaction for action " + action.actionId);
    }

    private static java.util.List<Integer> ints(int[] values) {
        java.util.List<Integer> result = new java.util.ArrayList<>();
        if (values != null) for (int value : values) result.add(value);
        return result;
    }
}
