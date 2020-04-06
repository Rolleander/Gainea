package com.broll.gainea.server.core.player;

import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.PlayerData;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.fractions.impl.FireFraction;

public class PlayerFactory {

    public static Player create(GameContainer game, com.broll.networklib.server.impl.Player<PlayerData> serverPlayer) {
        PlayerData data = serverPlayer.getData();
        int id = serverPlayer.getId();
        String name = serverPlayer.getName();
        Fraction fraction = createFraction(data.getFraction());
        Player player = new Player(fraction, serverPlayer);
        fraction.init(game, player);
        return player;
    }

    public static Fraction createFraction(FractionType type) {
        switch (type) {
            case FIRE:
                return new FireFraction();
            case WATER:
        }
        return null;
    }

}
