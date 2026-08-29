package com.broll.gainea.client.network.sites;

import com.broll.gainea.client.network.ProtoMapper;
import com.broll.mpnll.client.site.MessageReceiverRegistry;

public class GameBoardSite extends AbstractGameSite {
    @Override
    protected void registerReceivers(MessageReceiverRegistry registry) {
        on(registry, com.broll.gainea.net.proto.NT_BoardUpdate.class, this::received);
        on(registry, com.broll.gainea.net.proto.NT_GoalProgression.class, this::received);
    }

    private void received(com.broll.gainea.net.proto.NT_BoardUpdate update) {
        game.ui.inGameUI.getBattleHandler().clearBattleScreen();
        game.state.update(ProtoMapper.boardUpdate(update));
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_GoalProgression progression) {
        game.state.getGoals().get(progression.getIndex()).progression = progression.getProgression();
        game.ui.inGameUI.updateWindows();
    }
}
