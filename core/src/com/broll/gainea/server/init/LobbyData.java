package com.broll.gainea.server.init;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.net.NT_LobbySettings;
import com.broll.networklib.server.impl.LobbySettings;

public class LobbyData implements LobbySettings {

    private final static int START_LOCATIONS_DEFAULT = 3;

    private final static int START_GOALS_DEFAULT = 3;

    private final static int POINT_LIMIT_DEFAULT = 3;

    private ExpansionSetting expansionSetting = ExpansionSetting.BASIC_GAME;

    private GoalTypes goalTypes = GoalTypes.ALL;

    private int startLocations = START_LOCATIONS_DEFAULT;

    private int startGoals = START_GOALS_DEFAULT;

    private int pointLimit = POINT_LIMIT_DEFAULT;

    private GameContainer game;

    public void setGame(GameContainer game) {
        this.game = game;
    }

    public GameContainer getGame() {
        return game;
    }

    public void setExpansionSetting(ExpansionSetting expansionSetting) {
        this.expansionSetting = expansionSetting;
    }

    public ExpansionSetting getExpansionSetting() {
        return expansionSetting;
    }

    public int getStartGoals() {
        return startGoals;
    }

    public int getStartLocations() {
        return startLocations;
    }

    public void setStartGoals(int startGoals) {
        this.startGoals = startGoals;
    }

    public void setStartLocations(int startLocations) {
        this.startLocations = startLocations;
    }

    public GoalTypes getGoalTypes() {
        return goalTypes;
    }

    public int getPointLimit() {
        return pointLimit;
    }

    public void setGoalTypes(GoalTypes goalTypes) {
        this.goalTypes = goalTypes;
    }

    public void setPointLimit(int pointLimit) {
        this.pointLimit = pointLimit;
    }

    @Override
    public NT_LobbySettings getSettings() {
        NT_LobbySettings settings = new NT_LobbySettings();
        settings.expansionSetting = expansionSetting.ordinal();
        settings.startGoals = startGoals;
        settings.startLocations = startLocations;
        settings.pointLimit = pointLimit;
        settings.goalTypes = goalTypes.ordinal();
        return settings;
    }
}
