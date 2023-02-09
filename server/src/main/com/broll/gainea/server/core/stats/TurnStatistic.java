package com.broll.gainea.server.core.stats;

import com.broll.gainea.net.NT_RoundStatistic;

public class TurnStatistic {

    public PlayerStatistic[] players;

    private final static int POINTS_LOCATION = 2;
    private final static int POINTS_CARD = 3;
    private final static int POINTS_STAR = 1;
    private final static int POINTS_SCORE = 10;
    private final static int POINTS_POWER = 1;

    public NT_RoundStatistic get() {
        NT_RoundStatistic nt = new NT_RoundStatistic();
        nt.fightingPower = new short[players.length];
        nt.locations = new byte[players.length];
        nt.total = new short[players.length];
        nt.units = new byte[players.length];
        for (int i = 0; i < players.length; i++) {
            int fightingPower = players[i].totalPower + players[i].totalHealth;
            short units = players[i].units;
            short location = players[i].controlledLocations;
            nt.fightingPower[i] = (short) fightingPower;
            nt.units[i] = (byte) units;
            nt.locations[i] = (byte) location;
            int total = fightingPower * POINTS_POWER;
            total += players[i].cards * POINTS_CARD;
            total += players[i].stars * POINTS_STAR;
            total += players[i].points * POINTS_SCORE;
            total += location * POINTS_LOCATION;
            nt.total[i] = (short) total;
        }
        return nt;
    }
}
