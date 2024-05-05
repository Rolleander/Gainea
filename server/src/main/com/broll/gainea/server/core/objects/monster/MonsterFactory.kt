package com.broll.gainea.server.core.objects.monster

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.AreaType.BOG
import com.broll.gainea.server.core.map.AreaType.DESERT
import com.broll.gainea.server.core.map.AreaType.LAKE
import com.broll.gainea.server.core.map.AreaType.MOUNTAIN
import com.broll.gainea.server.core.map.AreaType.PLAINS
import com.broll.gainea.server.core.map.AreaType.SNOW
import com.broll.gainea.server.core.objects.monster.MonsterActivity.OFTEN
import com.broll.gainea.server.core.objects.monster.MonsterActivity.RARELY
import com.broll.gainea.server.core.objects.monster.MonsterActivity.SOMETIMES
import com.broll.gainea.server.core.objects.monster.MonsterBehavior.AGGRESSIVE
import com.broll.gainea.server.core.objects.monster.MonsterBehavior.FLEEING
import com.broll.gainea.server.core.objects.monster.MonsterBehavior.FRIENDLY
import com.broll.gainea.server.core.objects.monster.MonsterBehavior.RANDOM
import com.broll.gainea.server.core.objects.monster.MonsterBehavior.RESIDENT
import com.broll.gainea.server.core.objects.monster.MonsterMotion.AIRBORNE
import com.broll.gainea.server.core.objects.monster.MonsterMotion.AMPHIBIAN
import com.broll.gainea.server.core.objects.monster.MonsterMotion.AQUARIAN
import com.broll.gainea.server.core.objects.monster.MonsterMotion.TERRESTRIAL
import com.broll.gainea.server.core.player.Player


class MonsterFactory {
    private val monsters = mutableListOf<MonsterInit>()

    init {
        init()
    }

    private fun init() {
        add("Kriegselefant", 50, 2, 4, FRIENDLY, RARELY, listOf(DESERT))
        add("Zombie", 51, 2, 1, RANDOM, listOf(BOG))
        add("Wendigo", 52, 3, 2, AGGRESSIVE, RARELY, listOf(SNOW))
        add("Picker", 53, 1, 1, RANDOM, OFTEN, listOf(DESERT))
        add("Waldwolf", 54, 2, 1, RANDOM, OFTEN, listOf(PLAINS, MOUNTAIN))
        add("Wyvern", 55, AIRBORNE, 2, 2, FLEEING, listOf(PLAINS, MOUNTAIN))
        add("Sumpfkrokodil", 56, 2, 2, FLEEING, RARELY, listOf(BOG, LAKE, PLAINS))
        add("Adlerdrache", 57, AIRBORNE, 2, 2, FLEEING, OFTEN, listOf(MOUNTAIN, DESERT))
        add("Eberkrieger", 59, 2, 2, AGGRESSIVE, listOf(MOUNTAIN, DESERT, PLAINS))
        add("Dämonenlord", 60, 4, 3, AGGRESSIVE, OFTEN, listOf(BOG))
        add("Keiler", 61, 1, 2, RANDOM, listOf(PLAINS, SNOW))
        add("Riesenkröte", 62, AMPHIBIAN, 1, 1, FRIENDLY, listOf(BOG, PLAINS))
        add("Donnerdrache", 63, AIRBORNE, 5, 4, RANDOM, listOf(MOUNTAIN, DESERT))
        add("Bergriese", 64, 3, 4, AGGRESSIVE, listOf(MOUNTAIN, SNOW))
        add("Ork", 65, 2, 2, AGGRESSIVE, ALL_BUT_LAKE)
        add("Dämonenhyäne", 66, 2, 1, FLEEING, OFTEN, listOf(MOUNTAIN, DESERT))
        add("Geisterpirat", 67, 2, 1, AGGRESSIVE, listOf(LAKE))
        add("Mantikor", 68, 3, 3, RANDOM, listOf(DESERT))
        add("Tundranashorn", 69, 2, 4, FRIENDLY, RARELY, listOf(SNOW))
        add("Grizzly", 70, 2, 3, RANDOM, listOf(PLAINS, SNOW, MOUNTAIN))
        add("Urkrabbe", 71, AMPHIBIAN, 2, 3, RANDOM, RARELY, listOf(LAKE))
        add("Smaragddrache", 72, AIRBORNE, 4, 2, RANDOM, listOf(PLAINS))
        add("Raptor", 73, 2, 2, AGGRESSIVE, OFTEN, listOf(PLAINS, DESERT, MOUNTAIN))
        add("Gigasaurus", 74, 5, 4, AGGRESSIVE, listOf(PLAINS))
        add("Dornviper", 75, 5, 2, FLEEING, listOf(BOG, DESERT, MOUNTAIN))
        add("Giftkobra", 76, 3, 1, FLEEING, listOf(BOG, DESERT, PLAINS))
        add("Blutwurm", 77, 1, 4, AGGRESSIVE, listOf(BOG, DESERT))
        addAquarian("Hydra", 78, 6, 5)
        addAquarian("Riffhai", 79, 3, 2)
        add("Yeti", 80, 3, 4, AGGRESSIVE, RARELY, listOf(SNOW, MOUNTAIN))
        add("Eismammut", 81, 2, 6, FRIENDLY, RARELY, listOf(SNOW))
        add("Säbeltiger", 82, 4, 2, AGGRESSIVE, OFTEN, listOf(SNOW))
        addAquarian("Tiefseewal", 83, 2, 8)
        add("Panzerschildkröte", 84, AMPHIBIAN, 1, 5, FRIENDLY, RARELY, listOf(PLAINS, DESERT))
        addAquarian("Kraken", 85, 5, 3)
        add("Yotun", 86, 4, 4, RANDOM, listOf(SNOW))
        add("Waldriese", 87, 4, 4, AGGRESSIVE, RARELY, listOf(PLAINS))
        add("Felsgolem", 88, 4, 4, AGGRESSIVE, listOf(MOUNTAIN))
        add("Skelett", 89, 1, 1, AGGRESSIVE, listOf(BOG))
        add("Schnitter", 90, 3, 2, AGGRESSIVE, listOf(BOG))
        add("Zombiekrieger", 91, 2, 2, RANDOM, listOf(BOG))
        add("Skelettdrache", 92, 5, 5, AGGRESSIVE, listOf(BOG, MOUNTAIN))
        add("Wüstenkoloss", 97, 6, 6, RANDOM, RARELY, listOf(DESERT))
        add("Titanschnapper", 98, 2, 6, FLEEING, RARELY, listOf(LAKE))
        add("Phönix", 124, AIRBORNE, 2, 2, FRIENDLY, OFTEN, listOf(PLAINS, MOUNTAIN, DESERT))
    }

    private fun addAquarian(name: String, icon: Int, power: Int, health: Int) {
        add(name, icon, AQUARIAN, power, health, RESIDENT, SOMETIMES, listOf(LAKE))
    }

    private fun add(
        name: String,
        icon: Int,
        motion: MonsterMotion,
        power: Int,
        health: Int,
        behavior: MonsterBehavior,
        spawnAreas: List<AreaType>
    ) {
        add(name, icon, motion, power, health, behavior, SOMETIMES, spawnAreas)
    }

    private fun add(
        name: String,
        icon: Int,
        power: Int,
        health: Int,
        behavior: MonsterBehavior,
        spawnAreas: List<AreaType>
    ) {
        add(name, icon, TERRESTRIAL, power, health, behavior, SOMETIMES, spawnAreas)
    }

    private fun add(
        name: String,
        icon: Int,
        motion: MonsterMotion,
        power: Int,
        health: Int,
        behavior: MonsterBehavior,
        activity: MonsterActivity,
        spawnAreas: List<AreaType>
    ) {
        monsters.add(MonsterInit(name, icon, motion, power, health, behavior, activity, spawnAreas))
    }

    private fun add(
        name: String,
        icon: Int,
        power: Int,
        health: Int,
        behavior: MonsterBehavior,
        activity: MonsterActivity,
        spawnAreas: List<AreaType>
    ) {
        monsters.add(
            MonsterInit(
                name,
                icon,
                TERRESTRIAL,
                power,
                health,
                behavior,
                activity,
                spawnAreas
            )
        )
    }

    fun spawn(owner: Player, areaType: AreaType, activeMonsters: List<Monster>): Monster? {
        monsters.shuffle()
        val options = monsters.filter { it.spawnAreas.contains(areaType) }
        val unspawnedOptions = options.filter { init ->
            activeMonsters.none { monster -> monster.name == init.name }
        }
        var init = RandomUtils.pickRandom(unspawnedOptions)
        if (init == null) {
            init = RandomUtils.pickRandom(options)
        }
        if (init == null) {
            return null
        }
        return init.create(owner)
    }

    fun createAll(owner: Player) = monsters.map { it.create(owner) }

    private fun MonsterInit.create(owner: Player) = Monster(owner).also { monster ->
        monster.name = name
        monster.icon = icon
        monster.setStats(power, health)
        monster.behavior = behavior
        monster.activity = activity
        monster.motion = motion
    }

    private data class MonsterInit(
        val name: String,
        val icon: Int,
        val motion: MonsterMotion,
        val power: Int = 1,
        val health: Int = 1,
        val behavior: MonsterBehavior,
        val activity: MonsterActivity,
        val spawnAreas: List<AreaType>
    )

    private fun statistic() {
        val areas = AreaType.entries.associateWith { Stat() }
        val mf = MonsterFactory()
        for (m in mf.monsters) {
            for (t in m.spawnAreas) {
                areas[t]!!.power += m.power.toFloat()
                areas[t]!!.health += m.health.toFloat()
                areas[t]!!.stars += Monster.stars(m.power, m.health).toFloat()
                areas[t]!!.count += 1f
            }
        }
        areas.forEach {
            val stats = it.value
            println("AREA " + it.key + "  count: " + stats.count + "  power: " + stats.power / stats.count + "  health: " + stats.health / stats.count + "  stars: " + stats.stars / stats.count)
        }
    }

    private data class Stat(
        var count: Float = 0f,
        var power: Float = 0f,
        var health: Float = 0f,
        var stars: Float = 0f
    )

    companion object {
        private val ALL_BUT_LAKE = listOf(SNOW, MOUNTAIN, DESERT, PLAINS, BOG)

        @JvmStatic
        fun main(args: Array<String>) {
            MonsterFactory().statistic()
        }
    }
}
