package com.broll.gainea.server.core.goals.impl.all;

import static com.broll.gainea.server.core.goals.GoalDifficulty.HARD;
import static com.broll.gainea.server.core.goals.GoalDifficulty.MEDIUM;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;

import java.util.stream.Collectors;

public class G_KillPlayer extends Goal {

    private Player target;

    public G_KillPlayer() {
        super(GoalDifficulty.MEDIUM, "");
    }

    @Override
    public boolean init(GameContainer game, Player player) {
        target =  RandomUtils.pickRandom(game.getPlayers().stream().filter(it->
                it != player && !it.getUnits().isEmpty()).collect(Collectors.toList()));
        if(target==null){
            return false;
        }
        this.text = target.getServerPlayer().getName()+" darf keine Einheiten mehr besitzen";
        this.difficulty = target.getUnits().size() >= 8 ? HARD : MEDIUM;
        return super.init(game, player);
    }

    @Override
    public void killed(BattleObject unit, BattleResult throughBattle) {
        if(unit.getOwner() == target){
            check();
        }
    }

    @Override
    public void check() {
        if(target.getUnits().isEmpty()){
            success();
        }
    }
}
