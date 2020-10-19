package com.broll.gainea.server.core;

import com.broll.gainea.net.NT_PlayerTurn;
import com.broll.gainea.net.NT_PlayerWait;
import com.broll.gainea.net.NT_Event_TextInfo;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.actions.ReactionActions;
import com.broll.gainea.server.core.actions.RequiredActionContext;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.GameUtils;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.server.impl.ServerLobby;
import com.esotericsoftware.minlog.Log;

public class ReactionResultHandler implements ReactionActions {

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
        //reset actions
        game.clearActions();
        Player player = game.nextTurn();
        if (player.getSkipRounds() > 0) {
            player.consumeSkippedRound();
            //send aussetzen info to all players
            NT_Event_TextInfo info = new NT_Event_TextInfo();
            info.text = player.getServerPlayer().getName() + " muss aussetzen!";
            lobby.sendToAllTCP(info);
            //auto start next round after delay
            game.getProcessingCore().execute(this::endTurn, 3000);
        } else {
            game.getProcessingCore().execute(() ->
                    doPlayerTurn(player, game.getTurnBuilder().build(player)));
        }
    }

    private void doPlayerTurn(Player player, NT_PlayerTurn turn) {
        game.getReactionHandler().getActionHandlers().getReactionActions().sendBoardUpdate();
        //send turn to player
        player.getServerPlayer().sendTCP(turn);
        NT_PlayerWait wait = new NT_PlayerWait();
        wait.playersTurn = player.getServerPlayer().getId();
        //send wait to all others
        game.getPlayers().stream().filter(p -> p != player).forEach(p -> p.getServerPlayer().sendTCP(wait));
        //do fraction turn start
        player.getFraction().turnStarted(game.getReactionHandler().getActionHandlers());
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

    @Override
    public ActionContext optionalAction(ActionContext action) {
        game.getReactionHandler().optionalAction(action);
        return action;
    }

}
