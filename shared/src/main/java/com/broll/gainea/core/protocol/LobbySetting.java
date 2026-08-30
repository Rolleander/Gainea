package com.broll.gainea.core.protocol;

/** Stable values used by NT_UpdateLobbySettings.setting. */
public enum LobbySetting {
    EXPANSIONS(0), START_LOCATIONS(1), START_GOALS(2), POINT_LIMIT(3),
    GOAL_TYPES(4), MONSTERS(5), ROUND_LIMIT(6);

    public final int value;
    LobbySetting(int value) { this.value = value; }
}
