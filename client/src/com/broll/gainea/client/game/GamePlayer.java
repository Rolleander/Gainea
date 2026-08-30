package com.broll.gainea.client.game;

import com.broll.mpnll.client.lobby.Lobby;
import com.broll.mpnll.client.lobby.User;

/** Stable in-game identity, also usable by the offline map test. */
public final class GamePlayer {
    private final int id;
    private final Lobby lobby;

    private GamePlayer(int id, Lobby lobby) { this.id = id; this.lobby = lobby; }
    public static GamePlayer connected(User user) { return new GamePlayer(user.getId(), user.getLobby()); }
    public static GamePlayer offline(int id) { return new GamePlayer(id, null); }
    public int getId() { return id; }
    public Lobby getLobby() { return lobby; }
}
