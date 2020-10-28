package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.RequiredActionContext;
import com.broll.gainea.server.core.actions.impl.SelectChoiceAction;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class C_ReplaceGoal extends AbstractCard {
    public C_ReplaceGoal() {
        super(50,"Zielstrategie", "Ersetze eines deiner Ziele durch ein neues Ziel einer beliebigen Schwierigkeitsstufe");
        setDrawChance(0.5f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    public void play() {
        List<AbstractGoal> goals = owner.getGoalHandler().getGoals();
        AbstractGoal oldGoal = goals.get(selectHandler.selectObject("Welches Ziel soll ersetzt werden?", goals.stream().map(AbstractGoal::nt).collect(Collectors.toList())));
        owner.getGoalHandler().removeGoal(oldGoal);
        List<String> difficulties = Arrays.stream(GoalDifficulty.values()).map(GoalDifficulty::getLabel).collect(Collectors.toList());
        GoalDifficulty difficulty = GoalDifficulty.values()[selectHandler.selection("Welche Schwierigkeit soll das neue Ziel sein?", difficulties)];
        game.getGoalStorage().assignNewGoal(owner, difficulty);
    }

}
