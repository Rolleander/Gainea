package com.broll.gainea.server.core.objects;

import static com.broll.gainea.server.core.map.AreaType.BOG;
import static com.broll.gainea.server.core.map.AreaType.DESERT;
import static com.broll.gainea.server.core.map.AreaType.LAKE;
import static com.broll.gainea.server.core.map.AreaType.MOUNTAIN;
import static com.broll.gainea.server.core.map.AreaType.PLAINS;
import static com.broll.gainea.server.core.map.AreaType.SNOW;
import static com.broll.gainea.server.core.objects.MonsterActivity.*;
import static com.broll.gainea.server.core.objects.MonsterBehavior.*;

import com.broll.gainea.server.core.map.AreaType;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MonsterFactory {

    private List<MonsterInit> monsters = new ArrayList<>();
    private static AreaType[] ALL_BUT_LAKE = new AreaType[]{SNOW, MOUNTAIN, DESERT, PLAINS, BOG};

    public MonsterFactory() {
        init();
    }

    private void init() {
        add("Kriegselefant", 50, 2, 4, FRIENDLY, RARELY, new AreaType[]{DESERT});
        add("Zombie", 51, 2, 1, RANDOM, new AreaType[]{BOG});
        add("Wendigo", 52, 3, 2, AGGRESSIVE, RARELY, new AreaType[]{SNOW});
        add("Picker", 53, 1, 1, RANDOM, OFTEN, new AreaType[]{DESERT});
        add("Waldwolf", 54, 2, 1, RANDOM, OFTEN, new AreaType[]{PLAINS, MOUNTAIN});
        add("Wyvern", 55, 2, 2, FLEEING, new AreaType[]{PLAINS, MOUNTAIN});
        add("Sumpfkrokodil", 56, 2, 2, FLEEING, RARELY, new AreaType[]{BOG, LAKE, PLAINS});
        add("Adlerdrache", 57, 2, 2, FLEEING, OFTEN, new AreaType[]{MOUNTAIN, DESERT});
        add("Eberkrieger", 59, 2, 2, AGGRESSIVE, new AreaType[]{MOUNTAIN, DESERT, PLAINS});
        add("Dämonenlord", 60, 4, 3, AGGRESSIVE, OFTEN, new AreaType[]{BOG});
        add("Keiler", 61, 1, 2, RANDOM, new AreaType[]{PLAINS, SNOW});
        add("Riesenkröte", 62, 1, 1, FRIENDLY, new AreaType[]{BOG, PLAINS});
        add("Donnerdrache", 63, 5, 4, RANDOM, new AreaType[]{MOUNTAIN, DESERT});
        add("Bergriese", 64, 3, 4, AGGRESSIVE, new AreaType[]{MOUNTAIN, SNOW});
        add("Ork", 65, 2, 2, AGGRESSIVE, ALL_BUT_LAKE);
        add("Dämonenhyäne", 66, 2, 1, FLEEING, OFTEN, new AreaType[]{MOUNTAIN, DESERT});
        add("Geisterpirat", 67, 2, 1, AGGRESSIVE, new AreaType[]{LAKE});
        add("Mantikor", 68, 3, 3, RANDOM, new AreaType[]{DESERT});
        add("Tundranashorn", 69, 2, 4, FRIENDLY, RARELY, new AreaType[]{SNOW});
        add("Grizzly", 70, 2, 3, RANDOM, new AreaType[]{PLAINS, SNOW, MOUNTAIN});
        add("Urkrabbe", 71, 2, 3, RANDOM, RARELY, new AreaType[]{LAKE});
        add("Smaragddrache", 72, 4, 2, RANDOM, new AreaType[]{PLAINS});
        add("Raptor", 73, 2, 2, AGGRESSIVE, OFTEN, new AreaType[]{PLAINS, DESERT, MOUNTAIN});
        add("Gigasaurus", 74, 5, 4, AGGRESSIVE, new AreaType[]{PLAINS});
        add("Dornviper", 75, 5, 2, FLEEING, new AreaType[]{BOG, DESERT, MOUNTAIN});
        add("Giftkobra", 76, 3, 1, FLEEING, new AreaType[]{BOG, DESERT, PLAINS});
        add("Blutwurm", 77, 1, 4, AGGRESSIVE, new AreaType[]{BOG, DESERT});
        add("Hydra", 78, 6, 5, new AreaType[]{LAKE});
        add("Riffhai", 79, 3, 2, new AreaType[]{LAKE});
        add("Yeti", 80, 3, 4, AGGRESSIVE, RARELY, new AreaType[]{SNOW, MOUNTAIN});
        add("Eismammut", 81, 2, 6, FRIENDLY, RARELY, new AreaType[]{SNOW});
        add("Säbeltiger", 82, 4, 2, AGGRESSIVE, OFTEN, new AreaType[]{SNOW});
        add("Tiefseewal", 83, 2, 8, new AreaType[]{LAKE});
        add("Panzerschildkröte", 84, 1, 5, FRIENDLY, RARELY, new AreaType[]{PLAINS, DESERT});
        add("Kraken", 85, 5, 3, new AreaType[]{LAKE});
        add("Yotun", 86, 4, 4, RANDOM, new AreaType[]{SNOW});
        add("Waldriese", 87, 4, 4, AGGRESSIVE, RARELY, new AreaType[]{PLAINS});
        add("Felsgolem", 88, 4, 4, AGGRESSIVE, new AreaType[]{MOUNTAIN});
        add("Skelett", 89, 1, 1, AGGRESSIVE, new AreaType[]{BOG});
        add("Schnitter", 90, 3, 2, AGGRESSIVE, new AreaType[]{BOG});
        add("Zombiekrieger", 91, 2, 2, RANDOM, new AreaType[]{BOG});
        add("Skelettdrache", 92, 5, 5, AGGRESSIVE, new AreaType[]{BOG, MOUNTAIN});
        add("Wüstenkoloss", 97, 6, 6, RANDOM, RARELY, new AreaType[]{DESERT});
        add("Titanschnapper", 98, 2, 6, FLEEING, RARELY, new AreaType[]{LAKE});
        add("Phönix", 124, 2, 2, FRIENDLY, OFTEN, new AreaType[]{PLAINS, MOUNTAIN, DESERT});
    }

    public static void main(String[] args) {
        new MonsterFactory().statistic();
    }

    private void statistic() {
        Stat[] areas = new Stat[AreaType.values().length];
        for (int i = 0; i < AreaType.values().length; i++) {
            areas[i] = new Stat();
        }
        MonsterFactory mf = new MonsterFactory();
        for (MonsterInit m : mf.monsters) {
            for (AreaType t : m.spawnAreas) {
                areas[ArrayUtils.indexOf(AreaType.values(), t)].power += m.power;
                areas[ArrayUtils.indexOf(AreaType.values(), t)].health += m.health;
                areas[ArrayUtils.indexOf(AreaType.values(), t)].stars += Monster.stars(m.power, m.health);
                areas[ArrayUtils.indexOf(AreaType.values(), t)].count += 1;
            }
        }
        for (int i = 0; i < AreaType.values().length; i++) {
            AreaType area = AreaType.values()[i];
            System.out.println("AREA " + area + "  count: " + (int) areas[i].count + "  power: " + areas[i].power / areas[i].count + "  health: " + areas[i].health / areas[i].count + "  stars: " + areas[i].stars / areas[i].count);
        }
    }

    private class Stat {
        float count;
        float power = 0;
        float health = 0;
        float stars = 0;
    }

    private void add(String name, int icon, int power, int health, AreaType[] spawnAreas) {
        add(name, icon, power, health, RESIDENT, null, spawnAreas);
    }

    private void add(String name, int icon, int power, int health, MonsterBehavior behavior, AreaType[] spawnAreas) {
        add(name, icon, power, health, behavior, SOMETIMES, spawnAreas);
    }

    private void add(String name, int icon, int power, int health, MonsterBehavior behavior, MonsterActivity activity, AreaType[] spawnAreas) {
        monsters.add(new MonsterInit(name, icon, power, health, behavior, activity, spawnAreas));
    }

    public Monster spawn(AreaType areaType, List<Monster> activeMonsters) {
        Collections.shuffle(monsters);
        for (MonsterInit init : monsters) {
            if (ArrayUtils.contains(init.spawnAreas, areaType)) {
                //check if monster is not active right now
                if (!activeMonsters.stream().anyMatch(it -> Objects.equals(it.getName(), init.name))) {
                    Monster monster = new Monster();
                    monster.setName(init.name);
                    monster.setIcon(init.icon);
                    monster.setStats(init.power, init.health);
                    monster.setBehavior(init.behavior);
                    monster.setActivity(init.activity);
                    return monster;
                }
            }
        }
        return null;
    }

    private class MonsterInit {
        private String name;
        private int icon;
        private int power = 1;
        private int health = 1;
        private AreaType[] spawnAreas;
        private MonsterBehavior behavior;
        private MonsterActivity activity;

        public MonsterInit(String name, int icon, int power, int health, MonsterBehavior behavior, MonsterActivity occurs, AreaType[] spawnAreas) {
            this.name = name;
            this.icon = icon;
            this.power = power;
            this.health = health;
            this.spawnAreas = spawnAreas;
            this.behavior = behavior;
            this.activity = occurs;
        }
    }

}
