package com.broll.gainea.server.core.bot;

import com.broll.gainea.net.NT_Battle_Reaction;
import com.broll.gainea.net.NT_Battle_Start;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_EndTurn;
import com.broll.gainea.net.NT_Event_FinishedGoal;
import com.broll.gainea.net.NT_Event_ReceivedGoal;
import com.broll.gainea.net.NT_LoadedGame;
import com.broll.gainea.net.NT_PlayerAction;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_StartGame;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.battle.BattleHandler;
import com.broll.gainea.server.core.bot.impl.BotAttack;
import com.broll.gainea.server.core.bot.strategy.BotStrategy;
import com.broll.gainea.server.core.bot.strategy.StrategyConstants;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.ProcessingUtils;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.PackageReceiver;
import com.broll.networklib.server.impl.BotSite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotPlayerSite extends BotSite<PlayerData> {
    private final static Logger Log = LoggerFactory.getLogger(BotPlayerSite.class);

    private BotActionHandler botActionHandler;
    private boolean allowRetreat = false;
    private BotStrategy strategy;


    @PackageReceiver
    public void gameStart(NT_StartGame start) {
        Log.info(this.getBot() + " Bot send loaded Game!");
        sendServer(new NT_LoadedGame());
        GameContainer game = ((LobbyData) getBot().getServerLobby().getData()).getGame();
        Player player = getBot().getData().getGamePlayer();
        strategy = new BotStrategy(game, player, new StrategyConstants());
        this.botActionHandler = new BotActionHandler(game, player, strategy);
    }

    @PackageReceiver
    public void endTurn(NT_EndTurn nt) {
        //can only end turn, does not wait
        sendServer(new NT_EndTurn());
    }

    @PackageReceiver
    public void turnActions(NT_PlayerTurnActions turn) {
        pickTurnAction(turn);
    }

    @PackageReceiver
    public void handleAction(NT_PlayerAction requiredAction) {
        sendServer(botActionHandler.react(requiredAction.action));
    }

    @PackageReceiver
    public void battleStart(NT_Battle_Start start) {
        allowRetreat = start.allowRetreat && start.attacker == getBot().getId();
    }

    @PackageReceiver
    public void battleUpdate(NT_Battle_Update update) {
        ProcessingUtils.pause(BattleHandler.getAnimationDelay(update.attackerRolls.length, update.defenderRolls.length));
        if (allowRetreat) {
            BotAttack attack = (BotAttack) botActionHandler.getActionHandler(BotAttack.class);
            NT_Battle_Reaction nt = new NT_Battle_Reaction();
            nt.keepAttacking = attack.keepAttacking(update);
            sendServer(nt);
        }
    }

    @PackageReceiver
    public void newGoal(NT_Event_ReceivedGoal nt) {
        strategy.synchronizeGoalStrategies();
    }

    @PackageReceiver
    public void finishedGoal(NT_Event_FinishedGoal nt) {
        if (nt.player == getBot().getId()) {
            strategy.synchronizeGoalStrategies();
        }
    }

    private void pickTurnAction(NT_PlayerTurnActions actions) {
        strategy.prepareTurn();
        sendServer(botActionHandler.createBestReaction(actions));
    }

}
