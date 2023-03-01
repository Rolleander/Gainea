package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Continent;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.GodDragon;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class G_KillAllMonsters extends Goal {

    private Continent continent;

    public G_KillAllMonsters() {
        super(GoalDifficulty.MEDIUM, "");
    }

    @Override
    public boolean init(GameContainer game, Player player) {
        List<Continent> continets = game.getMap().getAllContinents();
        Collections.shuffle(continets);
        for (Continent continent : continets) {
            this.continent = continent;
            int monsterCount = getMonsterCount();
            if (monsterCount >= 2) {
                this.difficulty = monsterCount >= 5 ? GoalDifficulty.MEDIUM : GoalDifficulty.EASY;
                this.setExpansionRestriction(continent.getExpansion().getType());
                this.text = "Auf dem Kontinent " + continent.getName() + " darf es keine Monster mehr geben";
                return super.init(game, player);
            }
        }
        return false;
    }

    private int getMonsterCount() {
        return (int) continent.getAreas().stream().flatMap(it -> LocationUtils.getMonsters(it).stream())
                .filter(BattleObject::isAlive).count();
    }

    @Override
    public void check() {
        if(getMonsterCount()==0){
            success();
        }
    }

    @Override
    public void killed(BattleObject unit, BattleResult throughBattle) {
        if (unit instanceof Monster && unit.getOwner() == null) {
            check();
        }
    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        strategy.setSpreadUnits(false);
        strategy.setPrepareStrategy(()->{
            List<Monster> monsters = continent.getAreas().stream().flatMap(it->LocationUtils.getMonsters(it).stream()).collect(Collectors.toList());
            int stars = monsters.stream().mapToInt(it->it.getStars()).sum();
            strategy.updateTargets(monsters.stream().map(Monster::getLocation).collect(Collectors.toSet()));
            strategy.setRequiredUnits(stars);
        });
    }
}