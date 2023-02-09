package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class C_ReplaceGoal extends Card {
    public C_ReplaceGoal() {
        super(39, "Zielstrategie", "Wähle aus drei neuen Zielen und ersetze damit ein vorhandenes Ziel");
        setDrawChance(0.5f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        List<Goal> goals = game.getGoalStorage().getAnyGoals(owner, 3);
        Goal newGoal = goals.get(selectHandler.selectObject("Wähle ein neues Ziel", goals.stream().map(Goal::nt).collect(Collectors.toList())));
        List<Goal> oldGoals = owner.getGoalHandler().getGoals();
        Goal oldGoal = oldGoals.get(selectHandler.selectObject("Welches alte Ziel soll ersetzt werden?", oldGoals.stream().map(Goal::nt).collect(Collectors.toList())));
        owner.getGoalHandler().removeGoal(oldGoal);
        owner.getGoalHandler().newGoal(newGoal);
    }

}
