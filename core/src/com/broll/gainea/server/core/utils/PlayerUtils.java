package com.broll.gainea.server.core.utils;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.player.Player;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class PlayerUtils {

    public static void iteratePlayers(GameContainer game, int pauseBetween, Consumer<Player> consumer) {
        int current = game.getCurrentPlayer();
        for (int i = 0; i < game.getPlayers().size(); i++) {
            int nr = (game.getCurrentPlayer() + i) % game.getPlayers().size();
            consumer.accept(game.getPlayers().get(nr));
            ProcessingUtils.pause(pauseBetween);
        }
    }

    public static boolean isCommanderAlive(Player player) {
        return getCommander(player).isPresent();
    }

    public static Optional<Commander> getCommander(Player player) {
        return player.getUnits().stream().filter(unit -> unit instanceof Commander).map(it -> (Commander) it).findFirst();
    }


}
