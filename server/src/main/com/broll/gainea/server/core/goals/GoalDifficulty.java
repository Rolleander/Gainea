package com.broll.gainea.server.core.goals;

public enum GoalDifficulty {

    EASY(1,"(1) Einfach"),MEDIUM(2,"(2) Mittel"),HARD(3,"(3) Schwer");

    private int points;
    private String label;

    GoalDifficulty(int points, String label){
        this.points = points;
        this.label =label;
    }

    public int getPoints() {
        return points;
    }

    public String getLabel() {
        return label;
    }
}
