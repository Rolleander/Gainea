package com.broll.gainea.client.network.sites;

import com.broll.gainea.client.game.GoalState;
import com.broll.gainea.client.ui.screens.GameScreen;
import com.broll.gainea.client.ui.screens.LobbyScreen;
import com.broll.gainea.net.proto.NT_LoadedGame;
import com.broll.gainea.net.proto.NT_StartGame;
import com.broll.mpnll.client.site.MessageReceiverRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameStateSite extends AbstractGameSite {
    private static final Logger Log = LoggerFactory.getLogger(GameStateSite.class);

    @Override
    protected void registerReceivers(MessageReceiverRegistry registry) {
        on(registry, com.broll.gainea.net.proto.NT_Lib.class, this::received);
        on(registry, com.broll.gainea.net.proto.NT_StartGame.class, this::received);
        on(registry, com.broll.gainea.net.proto.NT_ReconnectGame.class, this::received);
        on(registry, com.broll.gainea.net.proto.NT_GameOver.class, this::received);
        on(registry, com.broll.gainea.net.proto.NT_GameStatistic.class, this::received);
    }

    private void received(com.broll.gainea.net.proto.NT_Lib lib) {
        game.state.library = lib;
        if (game.ui.inGameUI != null) game.ui.inGameUI.updateWindows();
        if (game.ui.getCurrentScreen() instanceof LobbyScreen)
            ((LobbyScreen) game.ui.getCurrentScreen()).updateLobby();
    }

    private void received(com.broll.gainea.net.proto.NT_StartGame message) {
        start(message);
    }

    private void received(com.broll.gainea.net.proto.NT_ReconnectGame message) {
        Log.info("player reconnected!");
        NT_StartGame start = message.getStart();
        game.state.init(start.getExpansionsList(), start.getPointLimit(), start.getRoundLimit(), getPlayer());
        game.state.getCards().addAll(start.getCardsList());
        game.state.shop = start.getShop();
        game.state.update(start.getUpdate());
        showGame();
        message.getGoalsList().forEach(goal -> game.state.getGoals().add(new GoalState(goal)));
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_GameOver message) {
        game.ui.inGameUI.hideWindows();
        game.state.update(message.getUpdate());
        game.state.playerTurnEnded();
        game.ui.inGameUI.gameOver(message);
    }

    private void received(com.broll.gainea.net.proto.NT_GameStatistic message) {
        game.state.setStatistic(message);
    }

    private void start(NT_StartGame start) {
        game.state.init(start.getExpansionsList(), start.getPointLimit(), start.getRoundLimit(), getPlayer());
        game.state.getCards().addAll(start.getCardsList());
        game.state.shop = start.getShop();
        game.state.update(start.getUpdate());
        showGame();
    }

    private void showGame() {
        game.ui.showScreen(new GameScreen());
        game.ui.inGameUI.updateWindows();
        send(NT_LoadedGame.newBuilder().build());
    }
}
