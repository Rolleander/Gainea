package com.broll.gainea.net;

import java.util.Objects;

public class NT_Goal {

    public final static int NO_PROGRESSION_GOAL = -1;
    public String description;
    public String restriction;
    public int points;
    public int progression;
    public int progressionGoal = NO_PROGRESSION_GOAL;

    public int id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NT_Goal nt_goal = (NT_Goal) o;
        return id == nt_goal.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, restriction, points);
    }
}
