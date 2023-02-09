package com.broll.gainea.server.init;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.net.NT_LobbySettings;
import com.broll.networklib.server.impl.ILobbyData;

public class LobbyData implements ILobbyData {

    private final static int START_LOCATIONS_DEFAULT = 3;

    private final static int START_GOALS_DEFAULT = 3;

    private final static int POINT_LIMIT_DEFAULT = 5;

    private final static int MONSTERS_PER_MAP = 10;

    private ExpansionSetting expansionSetting = ExpansionSetting.BASIC_GAME;

    private GoalTypes goalTypes = GoalTypes.ALL;

    private int startLocations = START_LOCATIONS_DEFAULT;

    private int startGoals = START_GOALS_DEFAULT;

    private int pointLimit = POINT_LIMIT_DEFAULT;

    private int monsterCount = MONSTERS_PER_MAP;

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

    public void setMonsterCount(int monsterCount) {
        this.monsterCount = monsterCount;
    }

    public int getMonsterCount() {
        return monsterCount;
    }

    @Override
    public NT_LobbySettings nt() {
        NT_LobbySettings settings = new NT_LobbySettings();
        settings.expansionSetting = expansionSetting.ordinal();
        settings.startGoals = startGoals;
        settings.startLocations = startLocations;
        settings.pointLimit = pointLimit;
        settings.goalTypes = goalTypes.ordinal();
        settings.monsters = monsterCount;
        return settings;
    }
}
