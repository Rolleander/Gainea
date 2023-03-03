package com.broll.gainea.server.core.player;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.server.impl.LobbyPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PlayerFactory {

    public static List<Player> create(GameContainer game, Collection<LobbyPlayer<PlayerData>> serverPlayers) {
        List<Player> players = new ArrayList<>();
        int color = 0;
        Iterator<LobbyPlayer<PlayerData>> iterator = serverPlayers.iterator();
        while (iterator.hasNext()) {
            Player player = create(game, iterator.next());
            player.setColor(color);
            players.add(player);
            color++;
            game.getUpdateReceiver().register(player.getFraction());
        }
        return players;
    }

    public static Player create(GameContainer game, LobbyPlayer<PlayerData> serverPlayer) {
        PlayerData data = serverPlayer.getData();
        Fraction fraction = data.getFraction().create();
        Player player = new Player(game, fraction, serverPlayer);
        fraction.init(game, player);
        return player;
    }


}
