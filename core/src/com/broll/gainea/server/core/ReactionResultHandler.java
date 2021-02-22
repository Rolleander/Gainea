package com.broll.gainea.server.core;

import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_PlayerTurnStart;
import com.broll.gainea.net.NT_PlayerWait;
import com.broll.gainea.net.NT_Event_TextInfo;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.ReactionActions;
import com.broll.gainea.server.core.actions.RequiredActionContext;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.GameUtils;
import com.broll.gainea.server.core.utils.ProcessingUtils;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.server.impl.ServerLobby;

public class ReactionResultHandler implements ReactionActions {

    private GameContainer game;
    private ServerLobby<LobbyData, PlayerData> lobby;
    private TurnEvents turnEvents;

    public ReactionResultHandler(GameContainer game, ServerLobby<LobbyData, PlayerData> lobby) {
        this.game = game;
        this.lobby = lobby;
        this.turnEvents = new TurnEvents(game);
    }

    @Override
    public void sendGameUpdate(Object update) {
        lobby.sendToAllTCP(update);
    }

    @Override
    public void endTurn() {
        int turn = game.getTurns();
        Player player = game.nextTurn();
        boolean newRound = game.getTurns() > turn;
        game.getProcessingCore().execute(() -> {
            if (newRound) {
                game.getUpdateReceiver().roundStarted();
            }
            game.getUpdateReceiver().turnStarted(player);
            turnEvents.turnStarted(player, newRound);
            if (!checkPlayerSkipped(player)) {
                doPlayerTurn(player);
            }
        });
    }

    private boolean checkPlayerSkipped(Player player) {
        boolean skip = player.getSkipRounds() > 0;
        if (skip) {
            player.consumeSkippedRound();
            //send aussetzen info to all players
            NT_Event_TextInfo info = new NT_Event_TextInfo();
            info.text = player.getServerPlayer().getName() + " muss aussetzen!";
            lobby.sendToAllTCP(info);
            //auto start next round after delay
            game.getProcessingCore().execute(this::endTurn, 3000);
        }
        return skip;
    }

    private void doPlayerTurn(Player player) {
        sendBoardUpdate();
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
        player.getServerPlayer().sendTCP(game.getTurnBuilder().build(player));
        //do fraction turn started
        fraction.turnStarted(actionsHandler);
    }

    @Override
    public void sendBoardUpdate() {
        sendGameUpdate(game.nt());
    }

    @Override
    public ActionContext requireAction(Player player, RequiredActionContext action) {
        game.getReactionHandler().requireAction(player, action);
        player.getServerPlayer().sendTCP(action.nt());
        if (action.getMessageForOtherPlayer() != null) {
            GameUtils.sendUpdateExceptFor(game, action.getMessageForOtherPlayer(), player);
        }
        return action;
    }

}
