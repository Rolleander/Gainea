package com.broll.gainea.client.network.sites;

import com.broll.gainea.client.ui.ingame.windows.VotingWindow;
import com.broll.gainea.net.proto.NT_Vote_Base;
import com.broll.mpnll.client.site.MessageReceiverRegistry;

public class GameVoteSite extends AbstractGameSite {
    private VotingWindow currentVote;

    @Override
    protected void registerReceivers(MessageReceiverRegistry registry) {
        on(registry, com.broll.gainea.net.proto.NT_Vote.class, this::voteEnded);
        on(registry, com.broll.gainea.net.proto.NT_VotePending.class, this::voteStarted);
    }

    private void voteEnded(com.broll.gainea.net.proto.NT_Vote nt) {
        if (currentVote != null) currentVote.result(nt.getYes());
    }

    private void voteStarted(com.broll.gainea.net.proto.NT_VotePending nt) {
        String text = getVoteText(nt.getVote());
        boolean requestedByMe = nt.getFromPlayerId() == game.state.getPlayer().getId();
        currentVote = new VotingWindow(game, text, requestedByMe);
        game.uiStage.addActor(currentVote);
    }

    private String getVoteText(NT_Vote_Base nt) {
        switch (nt.getValueCase()) {
            case SKIP_TURN:
                return "Aktuellen Zug überspringen?";
            case KICK_PLAYER:
                return game.state.getPlayer(nt.getKickPlayer().getPlayerId()).name + " aus dem Spiel entfernen?";
            default:
                return "";
        }
    }
}
