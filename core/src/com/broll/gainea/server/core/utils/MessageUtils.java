package com.broll.gainea.server.core.utils;

import com.broll.gainea.net.NT_Event_TextInfo;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.player.Player;

public final class MessageUtils {

    public static void gameLog(GameContainer game, String text) {
        NT_Event_TextInfo info = new NT_Event_TextInfo();
        info.type = NT_Event_TextInfo.TYPE_MESSAGE_LOG;
        info.text = text;
        game.getReactionHandler().getActionHandlers().getReactionActions().sendGameUpdate(info);
    }

    public static void displayMessage(GameContainer game, String text) {
        NT_Event_TextInfo info = new NT_Event_TextInfo();
        info.type = NT_Event_TextInfo.TYPE_MESSAGE_DISPLAY;
        info.text = text;
        game.getReactionHandler().getActionHandlers().getReactionActions().sendGameUpdate(info);
    }

    public static void displayMessage(GameContainer game, Player forPlayer, String text) {
        NT_Event_TextInfo info = new NT_Event_TextInfo();
        info.type = NT_Event_TextInfo.TYPE_MESSAGE_DISPLAY;
        info.text = text;
        forPlayer.getServerPlayer().sendTCP(info);
    }

}
