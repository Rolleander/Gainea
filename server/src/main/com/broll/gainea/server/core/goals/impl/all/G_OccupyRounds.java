package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.goals.RoundGoal;
import com.broll.gainea.server.core.map.AreaCollection;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class G_OccupyRounds extends RoundGoal {

    private final static int ROUND_TARGET = 5;
    private AreaCollection container;

    public G_OccupyRounds() {
        super(GoalDifficulty.MEDIUM, "", ROUND_TARGET);
    }

    @Override
    public boolean init(GameContainer game, Player player) {
        container = RandomUtils.pickRandom(game.getMap().getAllAreas()).getContainer();
        this.text = "Sei für " + ROUND_TARGET + " Runden der einzige Spieler mit Einheiten auf " + container.getName();
        int containerSize = container.getAreas().size();
        if (containerSize <= 5) {
            this.difficulty = GoalDifficulty.EASY;
        } else {
            this.difficulty = GoalDifficulty.MEDIUM;
        }
        return super.init(game, player);
    }

    @Override
    public void moved(List<MapObject> units, Location location) {
        if(units.stream().anyMatch(object-> object.getOwner()!=null && object.getOwner()!= player) && location.getContainer() == container){
            resetRounds();
        }
    }

    @Override
    public void spawned(MapObject object, Location location) {
        if(object.getOwner()!=null && object.getOwner()!= player && location.getContainer() == container){
            resetRounds();
        }
    }

    @Override
    public void check() {
        List<Player> owners = container.getAreas().stream().flatMap(it -> it.getInhabitants().stream().map(object -> object.getOwner())).filter(it -> it != null).distinct().collect(Collectors.toList());
        if (owners.size() == 1 && owners.get(0) == player) {
            progressRound();
        } else {
            resetRounds();
        }
    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        //todo
    }
}
