package com.broll.gainea.server.sites;

import com.broll.gainea.net.NT_Battle_Reaction;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.utils.MessageUtils;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.networklib.server.impl.Player;
import com.broll.networklib.server.impl.ServerLobby;
import com.broll.networklib.server.impl.ServerLobbyListener;
import com.esotericsoftware.minlog.Log;

import java.util.List;
import java.util.stream.Collectors;

public class LobbyListener implements ServerLobbyListener<LobbyData, PlayerData> {

    @Override
    public void playerJoined(ServerLobby<LobbyData, PlayerData> lobby, Player<PlayerData> player) {
        PlayerData playerData = player.getData();
        if (playerData == null) {
            playerData = new PlayerData();
            player.setData(playerData);
        }
        playerData.setFraction(findOpenFraction(lobby));
    }

    private FractionType findOpenFraction(ServerLobby<LobbyData, PlayerData> lobby) {
        List<FractionType> fractions = lobby.getPlayers().stream().map(Player::getData).map(PlayerData::getFraction).filter(fraction -> fraction != null).collect(Collectors.toList());
        for (FractionType fractionType : FractionType.values()) {
            if (!fractions.contains(fractionType)) {
                return fractionType;
            }
        }
        return FractionType.values()[0];
    }

    @Override
    public void playerLeft(ServerLobby<LobbyData, PlayerData> lobby, Player<PlayerData> player) {

    }

    @Override
    public void playerDisconnected(ServerLobby<LobbyData, PlayerData> lobby, Player<PlayerData> player) {
        MessageUtils.gameLog(lobby.getData().getGame(), "Verbindung zu " + player.getName() + " verloren!");
        GameContainer game = lobby.getData().getGame();
        if (game != null) {
            //try retreat from fight so battle does not get stuck
            game.getBattleHandler().playerReaction(player.getData().getGamePlayer(), new NT_Battle_Reaction());
        }
    }

    @Override
    public void playerReconnected(ServerLobby<LobbyData, PlayerData> lobby, Player<PlayerData> player) {
        GameContainer game = lobby.getData().getGame();
        if (game != null) {
            game.getProcessingCore().executeParallel(() -> {
                MessageUtils.gameLog(game, player.getName() + " ist zurück!");
                //send game reconnect update to reconnecting player
                com.broll.gainea.server.core.player.Player gamePlayer = player.getData().getGamePlayer();
                player.sendTCP(game.reconnect(gamePlayer));
                //check for open actions and resend them
                game.getReactionHandler().playerReconnected(gamePlayer);
            }, 100);
        }
    }

    @Override
    public void lobbyClosed(ServerLobby<LobbyData, PlayerData> lobby) {

    }

}
