package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class C_ReplaceGoal extends Card {
    public C_ReplaceGoal() {
        super(39,"Zielstrategie", "Ersetze eines deiner Ziele durch ein neues Ziel einer beliebigen Schwierigkeitsstufe");
        setDrawChance(0.5f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        List<Goal> goals = owner.getGoalHandler().getGoals();
        Goal oldGoal = goals.get(selectHandler.selectObject("Welches Ziel soll ersetzt werden?", goals.stream().map(Goal::nt).collect(Collectors.toList())));
        owner.getGoalHandler().removeGoal(oldGoal);
        List<String> difficulties = Arrays.stream(GoalDifficulty.values()).map(GoalDifficulty::getLabel).collect(Collectors.toList());
        GoalDifficulty difficulty = GoalDifficulty.values()[selectHandler.selection("Welche Schwierigkeit soll das neue Ziel sein?", difficulties)];
        game.getGoalStorage().assignNewGoal(owner, difficulty);
        //todo: broken
    }

}
