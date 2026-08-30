package com.broll.gainea.client.network.sites;

import com.broll.gainea.client.game.PlayerPerformAction;
import com.broll.gainea.net.proto.NT_Action;
import com.broll.gainea.net.proto.NT_PlayerAction;
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
        Log.info("received action " + message);
        CURRENT_PLAYER_ACTION = message;
        game.state.performAction(message.getAction(), playerPerformAction);
    }

    public void performAction(NT_Action action, int option, int[] options) {
        int actionId = actionId(action);
        send(NT_Reaction.newBuilder().setActionId(actionId).setOption(option).addAllOptions(ints(options)).build());
        Log.info("sent reaction for action " + actionId);
    }

    private static int actionId(NT_Action action) {
        switch (action.getValueCase()) {
            case ATTACK: return action.getAttack().getActionId();
            case CARD: return action.getCard().getActionId();
            case MOVE: return action.getMove().getActionId();
            case PLACE_UNIT: return action.getPlaceUnit().getActionId();
            case SELECT_CHOICE: return action.getSelectChoice().getActionId();
            case SHOP: return action.getShop().getActionId();
            default: throw new IllegalArgumentException("Action has no value");
        }
    }

    private static java.util.List<Integer> ints(int[] values) {
        java.util.List<Integer> result = new java.util.ArrayList<>();
        if (values != null) for (int value : values) result.add(value);
        return result;
    }
}
