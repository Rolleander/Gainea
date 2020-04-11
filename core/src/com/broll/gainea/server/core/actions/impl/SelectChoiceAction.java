package com.broll.gainea.server.core.actions.impl;

import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.net.NT_Action_SelectChoice;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.actions.AbstractActionHandler;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.actions.NoActionContext;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;

import java.util.List;
import java.util.stream.Collectors;

public class SelectChoiceAction extends AbstractActionHandler<NT_Action_SelectChoice, SelectChoiceAction.Context> {

    class Context extends ActionContext<NT_Action_SelectChoice> {
        ChoiceSelectedListener listener;
        PlayerSelectedListener playerSelectedListener;
        Player selectingPlayer;

        public Context(NT_Action_SelectChoice action) {
            super(action);
        }
    }

    public Context selection(List<String> choices, ChoiceSelectedListener listener) {
        Context context = build(choices);
        context.listener = listener;
        return context;
    }

    public Context selectObject(List<? extends Object> choices, ChoiceSelectedListener listener) {
        NT_Action_SelectChoice action = new NT_Action_SelectChoice();
        action.objectChoices = choices.toArray(new Object[0]);
        Context context = new Context(action);
        context.listener = listener;
        return context;
    }

    public Context selectOtherPlayer(Player player, PlayerSelectedListener listener) {
        Context context = build(game.getPlayers().stream().filter(it -> it != player).map(Player::getServerPlayer).map(com.broll.networklib.server.impl.Player::getName).collect(Collectors.toList()));
        context.playerSelectedListener = listener;
        context.selectingPlayer = player;
        return context;
    }

    private Context build(List<String> choices) {
        NT_Action_SelectChoice action = new NT_Action_SelectChoice();
        action.choices = choices.toArray(new String[0]);
        return new Context(action);
    }

    @Override
    public void handleReaction(Context context, NT_Action_SelectChoice action, NT_Reaction reaction) {
        int selectedOption = reaction.option;
        if (context.listener != null) {
            context.listener.selected(selectedOption);
        } else if (context.playerSelectedListener != null) {
            List<Player> players = game.getPlayers();
            int selectingIndex = players.indexOf(context.selectingPlayer);
            if (selectedOption >= selectingIndex) {
                selectedOption++; //skip selecting player
            }
            context.playerSelectedListener.selected(players.get(selectedOption));
        }
    }

    public interface ChoiceSelectedListener {
        void selected(int index);
    }

    public interface PlayerSelectedListener {
        void selected(Player player);
    }
}
