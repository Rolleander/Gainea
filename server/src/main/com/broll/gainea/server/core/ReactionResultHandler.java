package com.broll.gainea.server.core;

import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_PlayerTurnStart;
import com.broll.gainea.net.NT_PlayerWait;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.ReactionActions;
import com.broll.gainea.server.core.actions.RequiredActionContext;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.GameUtils;
import com.broll.gainea.server.core.utils.MessageUtils;
import com.broll.gainea.server.core.utils.ProcessingUtils;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.server.impl.ServerLobby;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReactionResultHandler implements ReactionActions {
    private final static Logger Log = LoggerFactory.getLogger(ReactionResultHandler.class);

    private GameContainer game;
    private ServerLobby<LobbyData, PlayerData> lobby;

    public ReactionResultHandler(GameContainer game, ServerLobby<LobbyData, PlayerData> lobby) {
        this.game = game;
        this.lobby = lobby;
    }

    @Override
    public void sendGameUpdate(Object update) {
        lobby.sendToAllTCP(update);
    }

    @Override
    public void endTurn() {
        game.getProcessingCore().execute(() -> {
            int turn = game.getRounds();
            Player player = game.nextTurn();
            if (player == null) {
                //no more turns
                game.end();
                return;
            }
            boolean newRound = game.getRounds() > turn;
            sendBoardUpdate();
            if (newRound) {
                game.getUpdateReceiver().roundStarted();
            }
            Log.info("Start next turn: " + player + " [Round " + turn + " Turn " + (game.getCurrentTurn() + 1) + " / " + game.getAllPlayers().size() + "]");
            game.getUpdateReceiver().turnStarted(player);
            if (!checkPlayerSkipped(player)) {
                doPlayerTurn(player);
            }
        });
    }

    private boolean checkPlayerSkipped(Player player) {
        boolean skip = player.getSkipRounds() > 0 || !player.isActive();
        if (skip) {
            Log.debug("Player turn skipped!");
            player.consumeSkippedRound();
            int delay = 0;
            if (player.isActive()) {
                //send aussetzen info to all players
                MessageUtils.displayMessage(game, player.getServerPlayer().getName() + " muss aussetzen!");
                delay = 3000;
            }
            //auto start next round after delay
            game.getProcessingCore().execute(this::endTurn, delay);
        }
        return skip;
    }

    private void doPlayerTurn(Player player) {
        //send turnstart to player and wait to other players
        NT_PlayerWait wait = new NT_PlayerWait();
        wait.playersTurn = player.getServerPlayer().getId();
        GameUtils.sendUpdate(game, player, new NT_PlayerTurnStart(), wait);
        ProcessingUtils.pause(1000);
        Fraction fraction = player.getFraction();
        ActionHandlers actionsHandler = game.getReactionHandler().getActionHandlers();
        //do fraction turn prepare
        fraction.prepareTurn(actionsHandler);
        //send turn actions to player
        player.getUnits().forEach(MapObject::turnStart);
        NT_PlayerTurnActions turn = game.getTurnBuilder().build(player);
        Log.trace("Send optional turn actions (" + turn.actions.length + ") to player " + player);
        player.getServerPlayer().sendTCP(turn);
        //do fraction turn started
        fraction.turnStarted(actionsHandler);
    }

    @Override
    public void sendBoardUpdate() {
        sendGameUpdate(game.nt());
    }

    @Override
    public ActionContext requireAction(Player player, RequiredActionContext action) {
        Log.trace("Require action for " + player + " : " + action.getAction());
        game.getReactionHandler().requireAction(player, action);
        player.getServerPlayer().sendTCP(action.nt());
        if (action.getMessageForOtherPlayer() != null) {
            GameUtils.sendUpdateExceptFor(game, action.getMessageForOtherPlayer(), player);
        }
        return action;
    }

}
