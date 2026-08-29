package com.broll.gainea.client.game;

import com.broll.gainea.net.proto.NT_Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Client-owned mutable counters and units backed by a player protobuf snapshot. */
public final class PlayerState {
    private NT_Player message;
    private int points;
    private int cards;
    private int stars;
    private final List<BoardObjectState> units = new ArrayList<>();

    public PlayerState(NT_Player message) { update(message); }
    public void update(NT_Player message) {
        this.message = message;
        points = message.getPoints(); cards = message.getCards(); stars = message.getStars();
        units.clear();
        message.getUnitsList().forEach(unit -> units.add(new BoardObjectState(
            com.broll.gainea.net.proto.NT_BoardObject.newBuilder().setUnit(unit).build())));
    }
    public NT_Player message() { return message; }
    public int id() { return message.getId(); }
    public int fraction() { return message.getFraction(); }
    public int color() { return message.getColor(); }
    public String name() { return message.getName(); }
    public int points() { return points; }
    public int cards() { return cards; }
    public int stars() { return stars; }
    public void addPoints(int value) { points += value; }
    public void addCards(int value) { cards += value; }
    public void addStars(int value) { stars += value; }
    public List<BoardObjectState> units() { return units; }
}
