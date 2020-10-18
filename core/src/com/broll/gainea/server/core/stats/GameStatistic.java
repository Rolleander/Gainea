package com.broll.gainea.server.core.stats;

import com.broll.gainea.server.core.GameContainer;

import java.util.ArrayList;
import java.util.List;

public class GameStatistic {

    private GameContainer game;
    private List<TurnStatistic> turnStatistics = new ArrayList<>();

    public GameStatistic(GameContainer game) {
        this.game = game;
    }

    public void nextTurn() {
        this.turnStatistics.add(calcTurnStatistic());
    }

    private TurnStatistic calcTurnStatistic() {
        TurnStatistic statistic = new TurnStatistic();
        return statistic;
    }

}
