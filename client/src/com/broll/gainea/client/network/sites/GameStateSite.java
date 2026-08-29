package com.broll.gainea.client.network.sites;

import com.broll.gainea.client.network.ProtoMapper;
import com.broll.gainea.client.ui.screens.GameScreen;
import com.broll.gainea.client.ui.screens.LobbyScreen;
import com.broll.gainea.net.NT_ReconnectGame;
import com.broll.gainea.net.NT_StartGame;
import com.broll.gainea.net.proto.NT_LoadedGame;
import com.broll.mpnll.client.site.MessageReceiverRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

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
        game.state.library = ProtoMapper.library(lib);
        if (game.ui.inGameUI != null) game.ui.inGameUI.updateWindows();
        if (game.ui.getCurrentScreen() instanceof LobbyScreen)
            ((LobbyScreen) game.ui.getCurrentScreen()).updateLobby();
    }

    private void received(com.broll.gainea.net.proto.NT_StartGame message) {
        start(ProtoMapper.start(message), message.getExpansionsList());
    }

    private void received(com.broll.gainea.net.proto.NT_ReconnectGame message) {
        Log.info("player reconnected!");
        NT_ReconnectGame reconnect = ProtoMapper.reconnect(message);
        start(reconnect, message.getExpansionsList());
        game.state.getGoals().addAll(Arrays.asList(reconnect.goals));
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_GameOver message) {
        com.broll.gainea.net.NT_GameOver end = ProtoMapper.gameOver(message);
        game.ui.inGameUI.hideWindows();
        game.state.update(end);
        game.state.playerTurnEnded();
        game.ui.inGameUI.gameOver(end);
    }

    private void received(com.broll.gainea.net.proto.NT_GameStatistic message) {
        game.state.setStatistic(ProtoMapper.statistic(message));
    }

    private void start(NT_StartGame start, java.util.List<com.broll.gainea.net.proto.NT_Expansion> expansions) {
        game.state.init(expansions, start.pointLimit, start.roundLimit, getPlayer());
        game.state.getCards().addAll(Arrays.asList(start.cards));
        game.state.shop = start.shop;
        game.state.update(start);
        game.ui.showScreen(new GameScreen());
        game.ui.inGameUI.updateWindows();
        send(NT_LoadedGame.newBuilder().build());
    }
}
