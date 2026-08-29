package com.broll.gainea.client.network.sites;

import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.network.ProtoMapper;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.proto.NT_EndTurn;
import com.broll.mpnll.client.lobby.User;
import com.broll.mpnll.client.site.MessageReceiverRegistry;

import java.util.*;

public class GameTurnSite extends AbstractGameSite {
    private final GameActionSite gameActionSite;
    private final List<NT_Action> actions = new ArrayList<>();
    private final PlayerPerformOptionalAction performedAction = new PlayerPerformOptionalAction() {
        @Override
        public void none() {
            game.state.getMapObjectsContainer().removeActions();
            send(NT_EndTurn.newBuilder().build());
        }

        @Override
        public void perform(NT_Action action, int option, int[] options) {
            gameActionSite.performAction(action, option, options);
        }
    };

    public GameTurnSite(GameActionSite gameActionSite) {
        this.gameActionSite = gameActionSite;
    }

    @Override
    protected void registerReceivers(MessageReceiverRegistry registry) {
        on(registry, com.broll.gainea.net.proto.NT_PlayerTurnStart.class, this::received);
        on(registry, com.broll.gainea.net.proto.NT_PlayerTurnActions.class, this::received);
        on(registry, com.broll.gainea.net.proto.NT_EndTurn.class, this::received);
        on(registry, com.broll.gainea.net.proto.NT_PlayerWait.class, this::received);
    }

    private void received(com.broll.gainea.net.proto.NT_PlayerTurnStart ignored) {
        game.state.playerTurnStart();
        turnStartMessage(getPlayer());
    }

    private void received(com.broll.gainea.net.proto.NT_PlayerTurnActions value) {
        actions.clear();
        game.state.turnIdle();
        actions.addAll(Arrays.asList(ProtoMapper.actions(value.getActionsList())));
        playerPickAction();
        game.ui.inGameUI.updateWindows();
        game.ui.inGameUI.getBattleHandler().clearBattleScreen();
    }

    private void received(com.broll.gainea.net.proto.NT_EndTurn ignored) {
        actions.clear();
        game.state.turnIdle();
        playerPickAction();
    }

    private void received(com.broll.gainea.net.proto.NT_PlayerWait wait) {
        game.state.updateIdleState(false);
        User other = getLobby().getUser(wait.getPlayersTurn());
        if (other != null) {
            game.state.playerTurnEnded();
            turnStartMessage(other);
        }
        actions.clear();
    }

    private void turnStartMessage(User player) {
        String message;
        if (player.isMe()) {
            message = "Du bist dran!";
            AudioPlayer.playSound("domm.ogg");
        } else message = player.getName() + "'s Zug!";
        game.ui.inGameUI.hideWindows();
        game.ui.inGameUI.infoMessages.show(message);
    }

    private void playerPickAction() {
        game.state.performOptionalAction(actions, performedAction);
    }
}
