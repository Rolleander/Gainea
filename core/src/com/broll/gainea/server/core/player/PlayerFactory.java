package com.broll.gainea.server.core.player;

import com.broll.gainea.misc.PackageLoader;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionFactory;
import com.broll.gainea.server.init.PlayerData;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.fractions.impl.FireFraction;
import com.broll.gainea.server.core.fractions.impl.WaterFraction;

public class PlayerFactory {


    public static Player create(GameContainer game, com.broll.networklib.server.impl.Player<PlayerData> serverPlayer) {
        PlayerData data = serverPlayer.getData();
        Fraction fraction = FractionFactory.create(data.getFraction());
        Player player = new Player(game, fraction, serverPlayer);
        fraction.init(game, player);
        return player;
    }


}
