package com.broll.gainea.server.core.objects;

import com.broll.gainea.server.core.map.AreaType;
import com.esotericsoftware.minlog.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MonsterFactory {

    private List<MonsterInit> monsters = new ArrayList<>();
    private int lastSpawned;
    private static AreaType[] ALL_BUT_LAKE = new AreaType[]{AreaType.SNOW, AreaType.MOUNTAIN, AreaType.DESERT, AreaType.PLAINS, AreaType.BOG};

    public MonsterFactory() {
        init();
        Collections.shuffle(monsters);
    }

    private void init() {
        add("Kriegselefant", 50, 2, 4, new AreaType[]{AreaType.DESERT});
        add("Zombie", 51, 2, 1, new AreaType[]{AreaType.BOG});
        add("Wendigo", 52, 3, 2, new AreaType[]{AreaType.SNOW});
        add("Picker", 53, 1, 1, new AreaType[]{AreaType.DESERT});
        add("Waldwolf", 54, 2, 1, new AreaType[]{AreaType.PLAINS, AreaType.MOUNTAIN});
        add("Wyvern", 55, 2, 2, new AreaType[]{AreaType.BOG, AreaType.MOUNTAIN});
        add("Sumpfkrokodil", 56, 2, 3, new AreaType[]{AreaType.BOG, AreaType.LAKE});
        add("Adlerdrache", 57, 2, 2, new AreaType[]{AreaType.MOUNTAIN, AreaType.DESERT});
        //58-götterdrache
        add("Eberkrieger", 59, 2, 2, new AreaType[]{AreaType.MOUNTAIN, AreaType.DESERT, AreaType.PLAINS});
        add("Dämonenlord", 60, 4, 3, new AreaType[]{AreaType.BOG});
        add("Keiler", 61, 1, 2, new AreaType[]{AreaType.PLAINS, AreaType.SNOW});
        add("Riesenkröte", 62, 1, 1, new AreaType[]{AreaType.BOG, AreaType.PLAINS});
        add("Donnerdrache", 63, 5, 4, new AreaType[]{AreaType.MOUNTAIN, AreaType.DESERT});
        add("Bergriese", 64, 3, 4, new AreaType[]{AreaType.MOUNTAIN, AreaType.SNOW});
        add("Ork", 65, 2, 2, ALL_BUT_LAKE);
        add("Dämonenhyäne", 66, 2, 1, new AreaType[]{AreaType.BOG, AreaType.DESERT});
        add("Geisterpirat", 67, 3, 1, new AreaType[]{AreaType.LAKE});
        add("Mantikor", 68, 3, 3, new AreaType[]{AreaType.DESERT});
        add("Tundranashorn", 69, 2, 4, new AreaType[]{AreaType.SNOW});
        add("Grizzly", 70, 2, 3, new AreaType[]{AreaType.PLAINS, AreaType.SNOW, AreaType.MOUNTAIN});
        add("Urkrabbe", 71, 2, 3, new AreaType[]{AreaType.LAKE});
        add("Smaragddrache", 72, 4, 2, new AreaType[]{AreaType.PLAINS});
        add("Raptor", 73, 2, 2, new AreaType[]{AreaType.PLAINS, AreaType.DESERT, AreaType.MOUNTAIN});
        add("Gigasaurus", 74, 4, 4, new AreaType[]{AreaType.PLAINS});
        add("Dornviper", 75, 5, 2, new AreaType[]{AreaType.BOG, AreaType.DESERT, AreaType.MOUNTAIN});
        add("Giftkobra", 76, 3, 1, new AreaType[]{AreaType.BOG, AreaType.DESERT, AreaType.PLAINS});
        add("Blutwurm", 77, 1, 4, new AreaType[]{AreaType.BOG, AreaType.DESERT});
        add("Hydra", 78, 6, 5, new AreaType[]{AreaType.LAKE});
        add("Riffhai", 79, 3, 2, new AreaType[]{AreaType.LAKE});
        add("Yeti", 80, 3, 4, new AreaType[]{AreaType.SNOW, AreaType.MOUNTAIN});
        add("Eismammut", 81, 2, 6, new AreaType[]{AreaType.SNOW});
        add("Säbeltiger", 82, 4, 2, new AreaType[]{AreaType.SNOW});
        add("Tiefseewal", 83, 2, 7, new AreaType[]{AreaType.LAKE});
        add("Panzerschildkröte", 84, 1, 6, new AreaType[]{AreaType.PLAINS, AreaType.DESERT});
        add("Kraken", 85, 5, 3, new AreaType[]{AreaType.LAKE});
        add("Yotun", 86, 4, 4, new AreaType[]{AreaType.SNOW});
        add("Titan", 87, 4, 6, new AreaType[]{AreaType.PLAINS});
    }

    public static void main(String[] args) {
        Log.INFO();
        new MonsterFactory().statistic();
    }

    private void statistic() {
        int[] areas = new int[AreaType.values().length];
        float power = 0;
        float health = 0;
        float stars = 0;
        MonsterFactory mf = new MonsterFactory();
        for (MonsterInit m : mf.monsters) {
            power += m.power;
            health += m.health;
            stars += Monster.stars(m.power, m.health);
            for (AreaType t : m.spawnAreas) {
                areas[ArrayUtils.indexOf(AreaType.values(), t)] += 1;
            }
        }
        int c = mf.monsters.size();
        Log.info("count: " + c);
        Log.info("power: " + power / c);
        Log.info("health: " + health / c);
        Log.info("stars: " + stars / c);
        for (int i = 0; i < AreaType.values().length; i++) {
            AreaType area = AreaType.values()[i];
            Log.info("area " + area + " : " + areas[i]);
        }
    }


    private void add(String name, int icon, int power, int health, AreaType[] spawnAreas) {
        monsters.add(new MonsterInit(name, icon, power, health, spawnAreas));
    }

    private MonsterInit get() {
        MonsterInit init = monsters.get(lastSpawned);
        lastSpawned++;
        if (lastSpawned >= monsters.size()) {
            lastSpawned = 0;
            Collections.shuffle(monsters);
        }
        return init;
    }

    public Monster spawn(AreaType areaType) {
        MonsterInit init;
        do {
            init = get();
        } while (ArrayUtils.contains(init.spawnAreas, areaType));
        Monster monster = new Monster();
        monster.setName(init.name);
        monster.setIcon(init.icon);
        monster.setPower(init.power);
        monster.setHealth(init.health);
        monster.setMaxHealth(init.health);
        return monster;
    }

    private class MonsterInit {
        private String name;
        private int icon;
        private int power = 1;
        private int health = 1;
        private AreaType[] spawnAreas;

        public MonsterInit(String name, int icon, int power, int health, AreaType[] spawnAreas) {
            this.name = name;
            this.icon = icon;
            this.power = power;
            this.health = health;
            this.spawnAreas = spawnAreas;
        }
    }

}
