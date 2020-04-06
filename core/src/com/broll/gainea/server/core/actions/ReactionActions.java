package com.broll.gainea.server.core.actions;

import com.broll.gainea.server.core.player.Player;

public interface ReactionActions {

    void endTurn();

    void sendBoardUpdate();

    ActionContext requireAction(Player player, RequiredActionContext action);

    void sendGameUpdate(Object update);

}
