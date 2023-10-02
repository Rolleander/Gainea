package com.broll.gainea.client.network;

import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.client.tasks.DiscoveredLobbies;

public interface IClientListener {
    void discoveredLobbies(DiscoveredLobbies discoveredLobbies);

    void connectedLobby(GameLobby lobby);

    void connectionFailure(String reason);

    void loadingStateUpdate(boolean loading);
}
