package com.broll.gainea.server.sites;

import com.broll.gainea.net.NT_EndTurn;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.net.NT_TextInfo;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.actions.ReactionActions;
import com.broll.gainea.server.core.actions.RequiredActionContext;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.actions.ReactionHandler;
import com.broll.networklib.PackageReceiver;
import com.broll.networklib.server.PackageRestriction;
import com.broll.networklib.server.RestrictionType;

public class GameBoardSite extends AbstractGameSite {

    public void init(GameContainer game) {
        game.initHandlers(new ReactionResultHandler());
    }

    public void nextTurn() {
        GameContainer game = getGame();
        Player player = game.nextTurn();
        if (player.getSkipRounds() > 0) {
            player.consumeSkippedRound();
            //send aussetzen info to all players
            NT_TextInfo info = new NT_TextInfo();
            info.text = player.getServerPlayer().getName() + " muss aussetzen!";
            getLobby().sendToAllTCP(info);
            //auto start next round after delay
            game.schedule(3000, () -> nextTurn());
        } else {
            doPlayerTurn(player, game.getTurnBuilder().build(player));
        }
    }

    @PackageReceiver
    @PackageRestriction(RestrictionType.LOBBY_LOCKED)
    public void reaction(NT_Reaction reaction) {
        GameContainer game = getGame();
        ActionContext action = game.getAction(reaction.actionId);
        if(action==null){
            //invalid action, ignore client request
            return;
        }
        ReactionHandler handler = game.getReactionHandler();
        Player player = getGamePlayer();
        if (handler.hasRequiredActionFor(player)) {
            handler.handle(player, action, reaction);
        } else {
            //only handle optional actions when its the players turn
            if (playersTurn()) {
                handler.handle(player, action, reaction);
            }
        }
    }

    @PackageReceiver
    @PackageRestriction(RestrictionType.LOBBY_LOCKED)
    public void reaction(NT_EndTurn endTurn) {
        if (playersTurn()) {
            //dont allow next turn if there are required actions for the player remaining
            if (!getGame().getReactionHandler().hasRequiredActionFor(getGamePlayer())) {
                nextTurn();
            }
        }
    }

    private class ReactionResultHandler implements ReactionActions {

        @Override
        public void sendGameUpdate(Object update) {
            getLobby().sendToAllTCP(update);
        }

        @Override
        public void endTurn() {
            nextTurn();
        }

        @Override
        public void sendBoardUpdate() {
            sendGameUpdate(getGame().nt());
        }

        @Override
        public ActionContext requireAction(Player player, RequiredActionContext action) {
            getGame().getReactionHandler().requireAction(player, action);
            player.getServerPlayer().sendTCP(action.nt());
            if (action.getMessageForOtherPlayer() != null) {
                getGame().getPlayers().stream().filter(p -> p != player).forEach(p -> p.getServerPlayer().sendTCP(action.getMessageForOtherPlayer()));
            }
            return action;
        }

    }

}
