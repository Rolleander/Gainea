package com.broll.gainea.server.core.actions.required;

import com.broll.gainea.net.NT_Action_SelectChoice;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.actions.AbstractActionHandler;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.actions.RequiredActionContext;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.player.Player;
import com.broll.networklib.server.impl.ConnectionSite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class SelectChoiceAction extends AbstractActionHandler<NT_Action_SelectChoice, SelectChoiceAction.Context> {

    private final static Logger Log = LoggerFactory.getLogger(SelectChoiceAction.class);

    class Context extends ActionContext<NT_Action_SelectChoice> {
        Player selectingPlayer;
        int selectedOption;

        public Context(NT_Action_SelectChoice action) {
            super(action);
        }
    }

    public int selection(String message, List<String> choices) {
        return selection(game.getPlayers().get(game.getCurrentPlayer()), message, choices);
    }

    public int selection(Player player, String message, List<String> choices) {
        Log.debug("Select choice action");
        Context context = build(choices);
        actionHandlers.getReactionActions().requireAction(player, new RequiredActionContext<>(context, message));
        Log.trace("Wait for select choice reaction");
        processingBlock.waitFor();
        return context.selectedOption;
    }

    public int selectObject(String message, List<? extends Object> choices) {
        return selectObject(game.getPlayers().get(game.getCurrentPlayer()), message, choices);
    }

    public int selectObject(Player player, String message, List<? extends Object> choices) {
        assureNotEmpty(choices);
        Log.debug("Select object action");
        NT_Action_SelectChoice action = new NT_Action_SelectChoice();
        action.objectChoices = choices.toArray(new Object[0]);
        Context context = new Context(action);
        actionHandlers.getReactionActions().requireAction(player, new RequiredActionContext<>(context, message));
        Log.trace("Wait for select choice reaction");
        processingBlock.waitFor();
        return context.selectedOption;
    }

    public Location selectLocation(String message, List<? extends Location> choices) {
        return selectLocation(game.getPlayers().get(game.getCurrentPlayer()), message, choices);
    }

    public Location selectLocation(Player player, String message, List<? extends Location> choices) {
        assureNotEmpty(choices);
        Log.debug("Select location action");
        NT_Action_SelectChoice action = new NT_Action_SelectChoice();
        action.objectChoices = choices.stream().map(Location::getNumber).toArray();
        Context context = new Context(action);
        actionHandlers.getReactionActions().requireAction(player, new RequiredActionContext<>(context, message));
        Log.trace("Wait for select choice reaction");
        processingBlock.waitFor();
        return choices.get(context.selectedOption);
    }

    public Player selectOtherPlayer(Player player, String message) {
        Log.debug("Select other player action");
        List<Player> players = game.getPlayers();
        Context context = build(players.stream().filter(it -> it != player).map(Player::getServerPlayer).map(com.broll.networklib.server.impl.Player::getName).collect(Collectors.toList()));
        context.selectingPlayer = player;
        int selectingIndex = players.indexOf(context.selectingPlayer);
        actionHandlers.getReactionActions().requireAction(player, new RequiredActionContext<>(context, message));
        Log.trace("Wait for select choice reaction");
        processingBlock.waitFor();
        int selectedOption = context.selectedOption;
        if (selectedOption >= selectingIndex) {
            selectedOption++; //skip selecting player
        }
        return players.get(selectedOption);
    }

    private void assureNotEmpty(List choices) {
        if (choices.isEmpty()) {
            throw new RuntimeException("Invalid select choice context: list of choices is empty");
        }
    }

    private Context build(List<String> choices) {
        assureNotEmpty(choices);
        NT_Action_SelectChoice action = new NT_Action_SelectChoice();
        action.choices = choices.toArray(new String[0]);
        return new Context(action);
    }

    @Override
    public void handleReaction(Context context, NT_Action_SelectChoice action, NT_Reaction reaction) {
        Log.trace("Handle select choice reaction");
        context.selectedOption = reaction.option;
        processingBlock.resume();
    }

}
