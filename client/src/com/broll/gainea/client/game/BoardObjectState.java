package com.broll.gainea.client.game;

import com.broll.gainea.net.proto.NT_BoardObject;
import com.broll.gainea.net.proto.NT_Monster;
import com.broll.gainea.net.proto.NT_Unit;

/**
 * Mutable client identity backed by the latest immutable protobuf snapshot.
 */
public final class BoardObjectState {
    private NT_BoardObject message;

    public BoardObjectState(NT_BoardObject message) {
        update(message);
    }

    public static BoardObjectState unit(NT_Unit value) {
        return new BoardObjectState(NT_BoardObject.newBuilder().setUnit(value).build());
    }

    public static BoardObjectState monster(NT_Monster value) {
        return new BoardObjectState(NT_BoardObject.newBuilder().setMonster(value).build());
    }

    private static int idOf(NT_BoardObject value) {
        if (value.hasMonster()) return value.getMonster().getId();
        if (value.hasUnit()) return value.getUnit().getId();
        return value.getId();
    }

    public void update(NT_BoardObject message) {
        if (this.message != null && idOf(this.message) != idOf(message))
            throw new IllegalArgumentException("Cannot change a board object's identity");
        this.message = message;
    }

    public NT_BoardObject message() {
        return message;
    }

    public int id() {
        return idOf(message);
    }

    public int owner() {
        return unit() != null ? unit().getOwner() : monster() != null ? monster().getOwner() : message.getOwner();
    }

    public int location() {
        return unit() != null ? unit().getLocation() : monster() != null ? monster().getLocation() : message.getLocation();
    }

    public String name() {
        return unit() != null ? unit().getName() : monster() != null ? monster().getName() : message.getName();
    }

    public String description() {
        return unit() != null ? unit().getDescription() : monster() != null ? monster().getDescription() : message.getDescription();
    }

    public int icon() {
        return unit() != null ? unit().getIcon() : monster() != null ? monster().getIcon() : message.getIcon();
    }

    public float scale() {
        return unit() != null ? unit().getScale() : monster() != null ? monster().getScale() : message.getScale();
    }

    public boolean building() {
        return unit() != null ? unit().getBuilding() : monster() != null ? monster().getBuilding() : message.getBuilding();
    }

    public boolean isUnit() {
        return message.hasUnit() || message.hasMonster();
    }

    public boolean isMonster() {
        return message.hasMonster() || (message.hasUnit() && message.getUnit().hasMonster());
    }

    public int health() {
        return monster() != null ? monster().getHealth() : unit() != null ? unit().getHealth() : 0;
    }

    public int maxHealth() {
        return monster() != null ? monster().getMaxHealth() : unit() != null ? unit().getMaxHealth() : 0;
    }

    public int power() {
        return monster() != null ? monster().getPower() : unit() != null ? unit().getPower() : 0;
    }

    public int kills() {
        return monster() != null ? monster().getKills() : unit() != null ? unit().getKills() : 0;
    }

    public int type() {
        return monster() != null ? monster().getType() : unit() != null ? unit().getType() : 0;
    }

    public int stars() {
        return monster() == null ? 0 : monster().getStars();
    }

    public int actionTimer() {
        return monster() == null ? -1 : monster().getActionTimer();
    }

    public int behavior() {
        return monster() == null ? 0 : monster().getBehavior();
    }

    private NT_Unit unit() {
        return message.hasUnit() ? message.getUnit() : null;
    }

    private NT_Monster monster() {
        if (message.hasMonster()) return message.getMonster();
        return message.hasUnit() && message.getUnit().hasMonster() ? message.getUnit().getMonster() : null;
    }
}
