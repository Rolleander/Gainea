package com.broll.gainea.client.game;

import com.broll.gainea.net.proto.NT_Goal;

public final class GoalState {
    private NT_Goal message;
    public GoalState(NT_Goal message) { this.message = message; }
    public NT_Goal message() { return message; }
    public int id() { return message.getId(); }
    public void setProgression(int value) { message = message.toBuilder().setProgression(value).build(); }
    @Override public boolean equals(Object other) { return other instanceof GoalState && ((GoalState) other).id() == id(); }
    @Override public int hashCode() { return id(); }
}
