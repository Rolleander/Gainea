package com.broll.gainea.server.init;

import com.broll.gainea.server.core.goals.GoalDifficulty;

import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public enum GoalTypes {

    ALL("Alle", GoalDifficulty.values()),
    ONLY_EASY("Nur Einfach", new GoalDifficulty[]{GoalDifficulty.EASY}),
    ONLY_MEDIUM("Nur Mittel", new GoalDifficulty[]{GoalDifficulty.MEDIUM}),
    ONLY_HARD("Nur Schwer", new GoalDifficulty[]{GoalDifficulty.HARD});

    private String name;
    private GoalDifficulty[] difficulties;

    GoalTypes(String name, GoalDifficulty[] difficulties) {
        this.name = name;
        this.difficulties = difficulties;
    }

    public String getName() {
        return name;
    }

    public boolean contains(GoalDifficulty difficulty) {
        return ArrayUtils.contains(difficulties, difficulty);
    }
}
