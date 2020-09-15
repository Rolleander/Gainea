package com.broll.gainea.server.core.goals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;

public abstract class AbstractGoal implements IGameUpdateReceiver {

    private String text;
    private GoalDifficulty difficulty;
    private String restrictionInfo;
    private ExpansionType[] requiredExpansions;
    protected GameContainer game;
    protected Player player;

    public AbstractGoal(GoalDifficulty difficulty, String text) {
        this.difficulty = difficulty;
        this.text = text;
    }

    public boolean init(GameContainer game, Player player) {
        this.game = game;
        this.player = player;
        return validForGame();
    }

    protected void setCustomRestrictionInfo(String restrictionInfo) {
        this.restrictionInfo = restrictionInfo;
    }

    protected void setExpansionRestriction(ExpansionType... expansions) {
        this.requiredExpansions = expansions;
        this.restrictionInfo = Arrays.stream(expansions).map(ExpansionType::getName).collect(Collectors.joining(","));
    }

    protected boolean validForGame() {
        List<ExpansionType> activeExpansions = game.getMap().getActiveExpansionTypes();
        for (ExpansionType type : requiredExpansions) {
            if (!activeExpansions.contains(type)) {
                //required expansion of goal is not active in this game
                return false;
            }
        }
        return true;
    }

    protected void success() {
        player.getGoalHandler().removeGoal(this);
        player.getGoalHandler().addPoints(difficulty.getPoints());
    }

    public GoalDifficulty getDifficulty() {
        return difficulty;
    }

    public NT_Goal nt() {
        NT_Goal goal = new NT_Goal();
        goal.description = text;
        goal.points = difficulty.getPoints();
        goal.restriction = restrictionInfo;
        return goal;
    }

    @Override
    public void battleResult(BattleResult result) {

    }

    @Override
    public void playedCard(AbstractCard card) {

    }

    @Override
    public void moved(List<MapObject> units, Location location) {

    }

    @Override
    public void spawned(MapObject object, Location location) {

    }

    @Override
    public void damaged(BattleObject unit, int damage) {

    }

    @Override
    public void earnedStars(Player player, int stars) {

    }
}
