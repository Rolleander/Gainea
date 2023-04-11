package com.broll.gainea.server.core.stats;

import com.broll.gainea.net.NT_GameStatistic;
import com.broll.gainea.net.NT_RoundStatistic;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter;
import com.broll.gainea.server.core.utils.GameUtils;

import java.util.ArrayList;
import java.util.List;

public class GameStatistic extends GameUpdateReceiverAdapter {

    private GameContainer game;
    private List<TurnStatistic> turnStatistics = new ArrayList<>();

    public GameStatistic(GameContainer game) {
        this.game = game;
    }

    @Override
    public void roundStarted() {
        registerRound();
    }

    public void registerRound() {
        this.turnStatistics.add(calcTurnStatistic());
    }

    public void sendStatistic() {
        calcTurnStatistic();
        NT_GameStatistic nt = new NT_GameStatistic();
        nt.rounds = turnStatistics.stream().map(TurnStatistic::get).toArray(NT_RoundStatistic[]::new);
        GameUtils.sendUpdate(game, nt);
    }

    private TurnStatistic calcTurnStatistic() {
        TurnStatistic statistic = new TurnStatistic();
        statistic.players = game.getAllPlayers().stream().map(this::calcPlayerStatistic).toArray(PlayerStatistic[]::new);
        return statistic;
    }

    private PlayerStatistic calcPlayerStatistic(Player player) {
        PlayerStatistic statistic = new PlayerStatistic();
        statistic.controlledLocations = (short) player.getControlledLocations().size();
        statistic.cards = (short) player.getCardHandler().getCards().size();
        statistic.stars = (short) player.getGoalHandler().getStars();
        statistic.points = (short) player.getGoalHandler().getScore();
        player.getUnits().forEach(unit -> {
            statistic.units++;
            statistic.totalPower += unit.getPower().getValue();
            statistic.totalHealth += unit.getHealth().getValue();
        });
        return statistic;
    }

}
