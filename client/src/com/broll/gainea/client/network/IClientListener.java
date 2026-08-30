package com.broll.gainea.client.network;

import com.broll.mpnll.client.impl.LobbyLookup;
import com.broll.mpnll.client.lobby.Lobby;

public interface IClientListener {
    void discoveredLobbies(LobbyLookup info);
    void connectedLobby(Lobby lobby);
    void connectionFailure(String reason);
    void loadingStateUpdate(boolean loading);
}
