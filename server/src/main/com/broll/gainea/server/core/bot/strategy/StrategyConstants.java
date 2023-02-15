package com.broll.gainea.server.core.bot.strategy;

public class StrategyConstants {

    private float winchanceForTargetConquer = 0.6f;
    private float winchanceForWildFight = 0.7f;
    private float winchanceForPlayerFight = 0.5f;

    public float getWinchanceForPlayerFight() {
        return winchanceForPlayerFight;
    }

    public float getWinchanceForTargetConquer() {
        return winchanceForTargetConquer;
    }

    public float getWinchanceForWildFight() {
        return winchanceForWildFight;
    }
}
