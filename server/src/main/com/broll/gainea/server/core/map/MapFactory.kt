package com.broll.gainea.server.core.map

import com.broll.gainea.server.core.map.impl.BoglandMap
import com.broll.gainea.server.core.map.impl.GaineaMap
import com.broll.gainea.server.core.map.impl.IcelandMap
import com.broll.gainea.server.core.map.impl.MountainsMap
import com.broll.gainea.server.init.ExpansionSetting
import java.util.concurrent.atomic.AtomicInteger

object MapFactory {
    private const val SIZE = 3120f

    @JvmStatic
    fun create(settings: ExpansionSetting): List<Pair<ExpansionFactory, Expansion>> {
        val expansions = mutableListOf<ExpansionFactory>()
        for (type in settings.maps) {
            when (type) {
                ExpansionType.GAINEA -> expansions.add(GaineaMap())
                ExpansionType.ICELANDS -> expansions.add(IcelandMap())
                ExpansionType.BOGLANDS -> expansions.add(BoglandMap())
                ExpansionType.MOUNTAINS -> expansions.add(MountainsMap())
            }
        }
        val set = expansions.map { it to it.create() }
        expansions.forEach { map1 ->
            expansions.forEach { map2 ->
                if (map1 !== map2) {
                    map1.connectExpansion(map2)
                }
            }
        }
        val locationNumber = AtomicInteger(0)
        set.flatMap { it.second.allLocations }.sortedBy { it.coordinates }
                .forEach { it.number = locationNumber.getAndIncrement() }
        set.forEach {
            it.second.coordinates.calcDisplayLocation(SIZE)
            it.second.allLocations.map { it.coordinates }.forEach { coords ->
                coords.shift(-0.5f, 0f)
                coords.mirrorY(0.5f)
                coords.calcDisplayLocation(SIZE)
            }
        }
        return set
    }

    fun createRenderless(settings: ExpansionSetting) =
            create(settings).map { it.second }

}
