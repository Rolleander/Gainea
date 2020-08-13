package com.broll.gainea.server.core.player;

import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.init.PlayerData;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.fractions.impl.FireFraction;
import com.broll.gainea.server.core.fractions.impl.WaterFraction;

public class PlayerFactory {

    public static Player create(GameContainer game, com.broll.networklib.server.impl.Player<PlayerData> serverPlayer) {
        PlayerData data = serverPlayer.getData();
        Fraction fraction = createFraction(data.getFraction());
        Player player = new Player(game, fraction, serverPlayer);
        fraction.init(game, player);
        return player;
    }

    public static Fraction createFraction(FractionType type) {
        switch (type) {
            case FIRE:
                return new FireFraction();
            case WATER:
                return new WaterFraction();
        }
        throw new RuntimeException("No fraction factory for type "+type);
    }

}
