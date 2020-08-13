package com.broll.gainea.server.sites;

import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.networklib.server.impl.Player;
import com.broll.networklib.server.impl.ServerLobby;
import com.broll.networklib.server.impl.ServerLobbyListener;

import java.util.List;
import java.util.stream.Collectors;

public class LobbyListener implements ServerLobbyListener<LobbyData, PlayerData> {

    @Override
    public void playerJoined(ServerLobby<LobbyData, PlayerData> lobby, Player<PlayerData> player) {
        PlayerData playerData = new PlayerData();
        player.setData(playerData);
        playerData.setFraction(findOpenFraction(lobby));
    }

    private FractionType findOpenFraction(ServerLobby<LobbyData, PlayerData> lobby) {
        List<FractionType> fractions = lobby.getPlayers().stream().map(Player::getData).map(PlayerData::getFraction).filter(fraction -> fraction != null).collect(Collectors.toList());
        for (FractionType fractionType : FractionType.values()) {
            if (!fractions.contains(fractionType)) {
                return fractionType;
            }
        }
        return null;
    }

    @Override
    public void playerLeft(ServerLobby<LobbyData, PlayerData> lobby, Player<PlayerData> player) {

    }

    @Override
    public void lobbyClosed(ServerLobby<LobbyData, PlayerData> lobby) {

    }

}
