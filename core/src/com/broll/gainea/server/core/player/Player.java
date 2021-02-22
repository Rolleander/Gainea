package com.broll.gainea.server.core.player;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.cards.CardHandler;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.goals.GoalHandler;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.objects.buffs.BuffableBoolean;
import com.broll.gainea.server.init.PlayerData;
import com.broll.gainea.server.core.map.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Player {

    private Fraction fraction;
    private com.broll.networklib.server.impl.Player<PlayerData> serverPlayer;
    private List<BattleObject> units = new ArrayList<>();
    private GoalHandler goalHandler;
    private CardHandler cardHandler;
    private int skipRounds;
    private int color;
    private BuffableBoolean<Player> attackingAllowed = new BuffableBoolean<>(this, true);

    public Player(GameContainer game, Fraction fraction, com.broll.networklib.server.impl.Player<PlayerData> serverPlayer) {
        this.fraction = fraction;
        this.serverPlayer = serverPlayer;
        this.goalHandler = new GoalHandler(game, this);
        this.cardHandler = new CardHandler(game, this);
        serverPlayer.getData().joinedGame(this);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void skipRounds(int rounds) {
        this.skipRounds += rounds;
    }

    public int getSkipRounds() {
        return skipRounds;
    }

    public void consumeSkippedRound() {
        skipRounds--;
    }

    public List<Location> getControlledLocations() {
        return units.stream().map(BattleObject::getLocation).distinct().collect(Collectors.toList());
    }

    public NT_Player nt() {
        NT_Player player = new NT_Player();
        player.cards = cardHandler.getCardCount();
        player.fraction = fraction.getType().ordinal();
        player.id = serverPlayer.getId();
        player.color = color;
        player.stars = goalHandler.getStars();
        player.name = serverPlayer.getName();
        player.points = goalHandler.getScore();
        player.units = units.stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
        return player;
    }

    public com.broll.networklib.server.impl.Player<PlayerData> getServerPlayer() {
        return serverPlayer;
    }

    public GoalHandler getGoalHandler() {
        return goalHandler;
    }

    public List<BattleObject> getUnits() {
        return units;
    }

    public Fraction getFraction() {
        return fraction;
    }

    public CardHandler getCardHandler() {
        return cardHandler;
    }

    public BuffableBoolean<Player> getAttackingAllowed() {
        return attackingAllowed;
    }
}
