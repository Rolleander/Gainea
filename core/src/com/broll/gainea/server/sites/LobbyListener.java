package com.broll.gainea.server.sites;

import com.broll.gainea.server.LobbyData;
import com.broll.gainea.server.PlayerData;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.networklib.server.impl.Player;
import com.broll.networklib.server.impl.ServerLobby;
import com.broll.networklib.server.impl.ServerLobbyListener;

import java.util.List;
import java.util.stream.Collectors;

class LobbyListener implements ServerLobbyListener<LobbyData, PlayerData> {

    @Override
    public void playerJoined(ServerLobby<LobbyData, PlayerData> lobby, Player<PlayerData> player) {
        PlayerData playerData = new PlayerData();
        playerData.setFraction(findOpenFraction(lobby));
        player.setData(playerData);
    }

    private FractionType findOpenFraction(ServerLobby<LobbyData, PlayerData> lobby){
        List<FractionType> fractions = lobby.getPlayers().stream().map(Player::getData).map(PlayerData::getFraction).collect(Collectors.toList());
        for(FractionType fractionType : FractionType.values()){
            if(!fractions.contains(fractionType)){
                return fractionType;
            }
        }
        return null;
    }

    @Override
    public void playerLeft(ServerLobby<LobbyData, PlayerData> lobby, Player<PlayerData> player) {

    }
}
