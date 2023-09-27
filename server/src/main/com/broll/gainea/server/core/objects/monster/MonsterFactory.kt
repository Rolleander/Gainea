package com.broll.gainea.server.core.objects.monster

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.map.AreaType
import org.apache.commons.lang3.ArrayUtils
import java.util.Collections
import java.util.stream.Collectors

class MonsterFactory {
    private val monsters: MutableList<MonsterInit?> = ArrayList()

    init {
        init()
    }

    private fun init() {
        add("Kriegselefant", 50, 2, 4, MonsterBehavior.FRIENDLY, MonsterActivity.RARELY, arrayOf(AreaType.DESERT))
        add("Zombie", 51, 2, 1, MonsterBehavior.RANDOM, arrayOf(AreaType.BOG))
        add("Wendigo", 52, 3, 2, MonsterBehavior.AGGRESSIVE, MonsterActivity.RARELY, arrayOf(AreaType.SNOW))
        add("Picker", 53, 1, 1, MonsterBehavior.RANDOM, MonsterActivity.OFTEN, arrayOf(AreaType.DESERT))
        add("Waldwolf", 54, 2, 1, MonsterBehavior.RANDOM, MonsterActivity.OFTEN, arrayOf(AreaType.PLAINS, AreaType.MOUNTAIN))
        add("Wyvern", 55, MonsterMotion.AIRBORNE, 2, 2, MonsterBehavior.FLEEING, arrayOf(AreaType.PLAINS, AreaType.MOUNTAIN))
        add("Sumpfkrokodil", 56, 2, 2, MonsterBehavior.FLEEING, MonsterActivity.RARELY, arrayOf(AreaType.BOG, AreaType.LAKE, AreaType.PLAINS))
        add("Adlerdrache", 57, MonsterMotion.AIRBORNE, 2, 2, MonsterBehavior.FLEEING, MonsterActivity.OFTEN, arrayOf(AreaType.MOUNTAIN, AreaType.DESERT))
        add("Eberkrieger", 59, 2, 2, MonsterBehavior.AGGRESSIVE, arrayOf(AreaType.MOUNTAIN, AreaType.DESERT, AreaType.PLAINS))
        add("Dämonenlord", 60, 4, 3, MonsterBehavior.AGGRESSIVE, MonsterActivity.OFTEN, arrayOf(AreaType.BOG))
        add("Keiler", 61, 1, 2, MonsterBehavior.RANDOM, arrayOf(AreaType.PLAINS, AreaType.SNOW))
        add("Riesenkröte", 62, MonsterMotion.AMPHIBIAN, 1, 1, MonsterBehavior.FRIENDLY, arrayOf(AreaType.BOG, AreaType.PLAINS))
        add("Donnerdrache", 63, MonsterMotion.AIRBORNE, 5, 4, MonsterBehavior.RANDOM, arrayOf(AreaType.MOUNTAIN, AreaType.DESERT))
        add("Bergriese", 64, 3, 4, MonsterBehavior.AGGRESSIVE, arrayOf(AreaType.MOUNTAIN, AreaType.SNOW))
        add("Ork", 65, 2, 2, MonsterBehavior.AGGRESSIVE, ALL_BUT_LAKE)
        add("Dämonenhyäne", 66, 2, 1, MonsterBehavior.FLEEING, MonsterActivity.OFTEN, arrayOf(AreaType.MOUNTAIN, AreaType.DESERT))
        add("Geisterpirat", 67, 2, 1, MonsterBehavior.AGGRESSIVE, arrayOf(AreaType.LAKE))
        add("Mantikor", 68, 3, 3, MonsterBehavior.RANDOM, arrayOf(AreaType.DESERT))
        add("Tundranashorn", 69, 2, 4, MonsterBehavior.FRIENDLY, MonsterActivity.RARELY, arrayOf(AreaType.SNOW))
        add("Grizzly", 70, 2, 3, MonsterBehavior.RANDOM, arrayOf(AreaType.PLAINS, AreaType.SNOW, AreaType.MOUNTAIN))
        add("Urkrabbe", 71, MonsterMotion.AMPHIBIAN, 2, 3, MonsterBehavior.RANDOM, MonsterActivity.RARELY, arrayOf(AreaType.LAKE))
        add("Smaragddrache", 72, MonsterMotion.AIRBORNE, 4, 2, MonsterBehavior.RANDOM, arrayOf(AreaType.PLAINS))
        add("Raptor", 73, 2, 2, MonsterBehavior.AGGRESSIVE, MonsterActivity.OFTEN, arrayOf(AreaType.PLAINS, AreaType.DESERT, AreaType.MOUNTAIN))
        add("Gigasaurus", 74, 5, 4, MonsterBehavior.AGGRESSIVE, arrayOf(AreaType.PLAINS))
        add("Dornviper", 75, 5, 2, MonsterBehavior.FLEEING, arrayOf(AreaType.BOG, AreaType.DESERT, AreaType.MOUNTAIN))
        add("Giftkobra", 76, 3, 1, MonsterBehavior.FLEEING, arrayOf(AreaType.BOG, AreaType.DESERT, AreaType.PLAINS))
        add("Blutwurm", 77, 1, 4, MonsterBehavior.AGGRESSIVE, arrayOf(AreaType.BOG, AreaType.DESERT))
        addAquarian("Hydra", 78, 6, 5)
        addAquarian("Riffhai", 79, 3, 2)
        add("Yeti", 80, 3, 4, MonsterBehavior.AGGRESSIVE, MonsterActivity.RARELY, arrayOf(AreaType.SNOW, AreaType.MOUNTAIN))
        add("Eismammut", 81, 2, 6, MonsterBehavior.FRIENDLY, MonsterActivity.RARELY, arrayOf(AreaType.SNOW))
        add("Säbeltiger", 82, 4, 2, MonsterBehavior.AGGRESSIVE, MonsterActivity.OFTEN, arrayOf(AreaType.SNOW))
        addAquarian("Tiefseewal", 83, 2, 8)
        add("Panzerschildkröte", 84, MonsterMotion.AMPHIBIAN, 1, 5, MonsterBehavior.FRIENDLY, MonsterActivity.RARELY, arrayOf(AreaType.PLAINS, AreaType.DESERT))
        addAquarian("Kraken", 85, 5, 3)
        add("Yotun", 86, 4, 4, MonsterBehavior.RANDOM, arrayOf(AreaType.SNOW))
        add("Waldriese", 87, 4, 4, MonsterBehavior.AGGRESSIVE, MonsterActivity.RARELY, arrayOf(AreaType.PLAINS))
        add("Felsgolem", 88, 4, 4, MonsterBehavior.AGGRESSIVE, arrayOf(AreaType.MOUNTAIN))
        add("Skelett", 89, 1, 1, MonsterBehavior.AGGRESSIVE, arrayOf(AreaType.BOG))
        add("Schnitter", 90, 3, 2, MonsterBehavior.AGGRESSIVE, arrayOf(AreaType.BOG))
        add("Zombiekrieger", 91, 2, 2, MonsterBehavior.RANDOM, arrayOf(AreaType.BOG))
        add("Skelettdrache", 92, 5, 5, MonsterBehavior.AGGRESSIVE, arrayOf(AreaType.BOG, AreaType.MOUNTAIN))
        add("Wüstenkoloss", 97, 6, 6, MonsterBehavior.RANDOM, MonsterActivity.RARELY, arrayOf(AreaType.DESERT))
        add("Titanschnapper", 98, 2, 6, MonsterBehavior.FLEEING, MonsterActivity.RARELY, arrayOf(AreaType.LAKE))
        add("Phönix", 124, MonsterMotion.AIRBORNE, 2, 2, MonsterBehavior.FRIENDLY, MonsterActivity.OFTEN, arrayOf(AreaType.PLAINS, AreaType.MOUNTAIN, AreaType.DESERT))
    }

    private fun addAquarian(name: String, icon: Int, power: Int, health: Int) {
        add(name, icon, MonsterMotion.AQUARIAN, power, health, MonsterBehavior.RESIDENT, null, arrayOf(AreaType.LAKE))
    }

    private fun add(name: String, icon: Int, motion: MonsterMotion, power: Int, health: Int, behavior: MonsterBehavior, spawnAreas: Array<AreaType>) {
        add(name, icon, motion, power, health, behavior, MonsterActivity.SOMETIMES, spawnAreas)
    }

    private fun add(name: String, icon: Int, power: Int, health: Int, behavior: MonsterBehavior, spawnAreas: Array<AreaType>) {
        add(name, icon, MonsterMotion.TERRESTRIAL, power, health, behavior, MonsterActivity.SOMETIMES, spawnAreas)
    }

    private fun add(name: String, icon: Int, motion: MonsterMotion, power: Int, health: Int, behavior: MonsterBehavior, activity: MonsterActivity?, spawnAreas: Array<AreaType>) {
        monsters.add(MonsterInit(name, icon, motion, power, health, behavior, activity, spawnAreas))
    }

    private fun add(name: String, icon: Int, power: Int, health: Int, behavior: MonsterBehavior, activity: MonsterActivity, spawnAreas: Array<AreaType>) {
        monsters.add(MonsterInit(name, icon, MonsterMotion.TERRESTRIAL, power, health, behavior, activity, spawnAreas))
    }

    fun spawn(areaType: AreaType?, activeMonsters: List<Monster?>): Monster? {
        Collections.shuffle(monsters)
        val options = monsters.stream().filter { init: MonsterInit? -> ArrayUtils.contains(init!!.spawnAreas, areaType) }.collect(Collectors.toList())
        val unspawnedOptions = options.stream().filter { init: MonsterInit? ->
            activeMonsters.stream()
                    .noneMatch { monster: Monster? -> monster.getName() == init!!.name }
        }.collect(Collectors.toList())
        var init = RandomUtils.pickRandom(unspawnedOptions)
        if (init == null) {
            init = RandomUtils.pickRandom(options)
        }
        if (init == null) {
            return null
        }
        val monster = Monster()
        monster.name = init.name
        monster.icon = init.icon
        monster.setStats(init.power, init.health)
        monster.setBehavior(init.behavior)
        monster.setActivity(init.activity)
        monster.setMotion(init.motion)
        return monster
    }

    private inner class MonsterInit(val name: String, val icon: Int, val motion: MonsterMotion, power: Int, health: Int, behavior: MonsterBehavior, occurs: MonsterActivity?, spawnAreas: Array<AreaType>) {
        val power = 1
        val health = 1
        val spawnAreas: Array<AreaType>
        val behavior: MonsterBehavior
        val activity: MonsterActivity?

        init {
            this.power = power
            this.health = health
            this.spawnAreas = spawnAreas
            this.behavior = behavior
            activity = occurs
        }
    }

    private fun statistic() {
        val areas = arrayOfNulls<Stat>(AreaType.entries.size)
        for (i in AreaType.entries.toTypedArray().indices) {
            areas[i] = Stat()
        }
        val mf = MonsterFactory()
        for (m in mf.monsters) {
            for (t in m!!.spawnAreas) {
                areas[ArrayUtils.indexOf(AreaType.entries.toTypedArray(), t)]!!.power += m.power.toFloat()
                areas[ArrayUtils.indexOf(AreaType.entries.toTypedArray(), t)]!!.health += m.health.toFloat()
                areas[ArrayUtils.indexOf(AreaType.entries.toTypedArray(), t)]!!.stars += Monster.Companion.stars(m.power, m.health).toFloat()
                areas[ArrayUtils.indexOf(AreaType.entries.toTypedArray(), t)]!!.count += 1f
            }
        }
        for (i in AreaType.entries.toTypedArray().indices) {
            val area = AreaType.entries[i]
            println("AREA " + area + "  count: " + areas[i]!!.count.toInt() + "  power: " + areas[i]!!.power / areas[i]!!.count + "  health: " + areas[i]!!.health / areas[i]!!.count + "  stars: " + areas[i]!!.stars / areas[i]!!.count)
        }
    }

    private inner class Stat {
        var count = 0f
        var power = 0f
        var health = 0f
        var stars = 0f
    }

    companion object {
        private val ALL_BUT_LAKE = arrayOf(AreaType.SNOW, AreaType.MOUNTAIN, AreaType.DESERT, AreaType.PLAINS, AreaType.BOG)
        @JvmStatic
        fun main(args: Array<String>) {
            MonsterFactory().statistic()
        }
    }
}
