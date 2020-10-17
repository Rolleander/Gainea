package com.broll.gainea.server.core.utils;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.player.Player;

public final class GameUtils {
    private GameUtils() {

    }

    public static void sendUpdate(GameContainer game, Player player, Object update, Object updateForOtherPlayers) {
        player.getServerPlayer().sendTCP(update);
        sendUpdateExceptFor(game, updateForOtherPlayers, player);
    }

    public static void sendUpdateExceptFor(GameContainer game, Object update, Player exceptPlayer) {
        game.getPlayers().stream().filter(p -> p != exceptPlayer).forEach(p -> p.getServerPlayer().sendTCP(update));
    }

    public static void sendUpdate(GameContainer game, Object update) {
        GameUtils.sendUpdate(game, update);
    }
}
