package com.broll.gainea.net;

import java.util.Objects;

public class NT_Goal {

    public String description;
    public String restriction;
    public int points;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NT_Goal nt_goal = (NT_Goal) o;
        return points == nt_goal.points &&
                Objects.equals(description, nt_goal.description) &&
                Objects.equals(restriction, nt_goal.restriction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, restriction, points);
    }
}
