package com.broll.gainea.server.core.player;

import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionFactory;
import com.broll.gainea.server.init.PlayerData;
import com.broll.gainea.server.core.GameContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PlayerFactory {

    public static List<Player> create(GameContainer game, Collection<com.broll.networklib.server.impl.Player<PlayerData>> serverPlayers) {
        List<Player> players = new ArrayList<>();
        int color = 0;
        Iterator<com.broll.networklib.server.impl.Player<PlayerData>> iterator = serverPlayers.iterator();
        while (iterator.hasNext()) {
            Player player = create(game, iterator.next());
            player.setColor(color);
            players.add(player);
            color++;
        }
        return players;
    }

    public static Player create(GameContainer game, com.broll.networklib.server.impl.Player<PlayerData> serverPlayer) {
        PlayerData data = serverPlayer.getData();
        Fraction fraction = FractionFactory.create(data.getFraction());
        Player player = new Player(game, fraction, serverPlayer);
        fraction.init(game, player);
        return player;
    }


}
