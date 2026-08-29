package com.broll.gainea.server.init;

import com.broll.gainea.server.core.goals.GoalDifficulty;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum GoalTypes {
    ALL("Alle", Arrays.asList(GoalDifficulty.values())),
    ONLY_EASY("Nur Einfach", Collections.singletonList(GoalDifficulty.EASY)),
    ONLY_MEDIUM("Nur Mittel", Collections.singletonList(GoalDifficulty.MEDIUM)),
    ONLY_HARD("Nur Schwer", Collections.singletonList(GoalDifficulty.HARD));

    private final String goalName;
    private final List<GoalDifficulty> difficulties;
    GoalTypes(String goalName, List<GoalDifficulty> difficulties) { this.goalName = goalName; this.difficulties = difficulties; }
    public String getGoalName() { return goalName; }
    public boolean contains(GoalDifficulty difficulty) { return difficulties.contains(difficulty); }
}
