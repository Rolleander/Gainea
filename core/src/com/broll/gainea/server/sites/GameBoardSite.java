package com.broll.gainea.server.sites;

import com.broll.gainea.net.NT_EndTurn;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.actions.ReactionHandler;
import com.broll.networklib.PackageReceiver;
import com.broll.networklib.server.ConnectionRestriction;
import com.broll.networklib.server.RestrictionType;

public class GameBoardSite extends AbstractGameSite {

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    public void reaction(NT_Reaction reaction) {
        GameContainer game = getGame();
        ActionContext action = game.getAction(reaction.actionId);
        if (action == null) {
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
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    public void reaction(NT_EndTurn endTurn) {
        //only react to if its players turn and no action is running right now
        if (playersTurn() && !getGame().getReactionHandler().actionActive()) {
            //dont allow next turn if there are required actions for the player remaining
            if (!getGame().getReactionHandler().hasRequiredActionFor(getGamePlayer())) {
                nextTurn();
            }
        }
    }

}
