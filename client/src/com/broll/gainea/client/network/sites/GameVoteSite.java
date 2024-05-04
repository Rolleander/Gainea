package com.broll.gainea.client.network.sites;

import com.broll.gainea.client.ui.ingame.windows.VotingWindow;
import com.broll.gainea.net.NT_Vote;
import com.broll.gainea.net.NT_VotePending;
import com.broll.gainea.net.NT_Vote_Base;
import com.broll.gainea.net.NT_Vote_KickPlayer;
import com.broll.gainea.net.NT_Vote_SkipTurn;
import com.broll.networklib.PackageReceiver;

public class GameVoteSite extends AbstractGameSite {

    private VotingWindow currentVote;

    @PackageReceiver
    public void voteEnded(NT_Vote nt) {
        if (currentVote != null) {
            currentVote.result(nt.yes);
        }
    }

    @PackageReceiver
    public void voteStarted(NT_VotePending nt) {
        String text = getVoteText(nt.vote);
        boolean requestedByMe = nt.fromPlayerId == game.state.getPlayer().getId();
        currentVote = new VotingWindow(game, text, requestedByMe);
        game.uiStage.addActor(currentVote);
    }

    private String getVoteText(NT_Vote_Base nt) {
        if (nt instanceof NT_Vote_SkipTurn) {
            return "Aktuellen Zug überspringen?";
        } else if (nt instanceof NT_Vote_KickPlayer) {
            String playerName = game.state.getPlayer(((NT_Vote_KickPlayer) nt).playerId).name;
            return playerName + " aus dem Spiel entfernen?";
        }
        return "";
    }

}

