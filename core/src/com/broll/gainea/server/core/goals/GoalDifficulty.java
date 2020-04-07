package com.broll.gainea.server.core.goals;

public enum GoalDifficulty {

    EASY(1),MEDIUM(2),HARD(3);

    private int points;

    GoalDifficulty(int points){
        this.points = points;
    }

    public int getPoints() {
        return points;
    }
}
