package com.broll.gainea.server.sites;

import com.broll.gainea.net.NT_Battle_Reaction;
import com.broll.gainea.net.NT_EndTurn;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.battle.BattleHandler;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.actions.ReactionHandler;
import com.broll.networklib.PackageReceiver;
import com.broll.networklib.server.ConnectionRestriction;
import com.broll.networklib.server.RestrictionType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameBoardSite extends GameSite {
    private final static Logger Log = LoggerFactory.getLogger(GameBoardSite.class);

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    public void reaction(NT_Reaction reaction) {
        GameContainer game = getGame();
        if (!game.isGameOver()) {
            ActionContext action = game.getAction(reaction.actionId);
            if (action == null) {
                Log.warn("Ignore reaction, no action found for id " + reaction.actionId);
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
                } else {
                    Log.warn("Ignore optional action because its not players turn");
                }
            }
        }
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    public void reaction(NT_Battle_Reaction battle_reaction) {
        BattleHandler battle = getGame().getBattleHandler();
        if (battle.isBattleActive()) {
            battle.playerReaction(getGamePlayer(), battle_reaction);
        } else {
            Log.warn("Battle reaction ignored because battle is not active");
        }
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    public void reaction(NT_EndTurn endTurn) {
        //only react to if its players turn and no action is running right now
        if (!getGame().isGameOver() && playersTurn() && !getGame().getProcessingCore().isBusy()) {
            //dont allow next turn if there are required actions for the player remaining
            if (!getGame().getReactionHandler().hasRequiredActionFor(getGamePlayer())) {
                nextTurn();
            } else {
                Log.warn("End turn ignored because there are required actions remaining");
            }
        } else {
            Log.warn("End turn ignored because game is over, its not the players turn or processing core is busy");
        }
    }

}
