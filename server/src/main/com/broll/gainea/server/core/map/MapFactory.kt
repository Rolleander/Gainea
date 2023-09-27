package com.broll.gainea.server.core.map

import com.broll.gainea.server.core.map.impl.BoglandMapimport

com.broll.gainea.server.core.map.impl .GaineaMapimport com.broll.gainea.server.core.map.impl .IcelandMapimport com.broll.gainea.server.core.map.impl .MountainsMapimport com.broll.gainea.server.init.ExpansionSettingimport org.apache.commons.lang3.tuple.Pairimport java.util.concurrent.atomic.AtomicIntegerimport java.util.function.Consumerimport java.util.stream.Collectors
object MapFactory {
    const val SIZE = 3120f
    fun create(settings: ExpansionSetting?): List<Pair<ExpansionFactory, Expansion?>> {
        val expansions: MutableList<ExpansionFactory> = ArrayList()
        for (type in settings.getMaps()) {
            when (type) {
                ExpansionType.GAINEA -> expansions.add(GaineaMap())
                ExpansionType.ICELANDS -> expansions.add(IcelandMap())
                ExpansionType.BOGLANDS -> expansions.add(BoglandMap())
                ExpansionType.MOUNTAINS -> expansions.add(MountainsMap())
            }
        }
        val set = expansions.stream().map { it: ExpansionFactory -> Pair.of(it, it.create()) }.collect(Collectors.toList())
        expansions.forEach(Consumer { map1: ExpansionFactory ->
            expansions.forEach(Consumer { map2: ExpansionFactory ->
                if (map1 !== map2) {
                    map1.connectExpansion(map2)
                }
            })
        })
        val maps = set.stream().map { it: Pair<ExpansionFactory, Expansion?> -> it.right }.collect(Collectors.toList())
        val locationNumer = AtomicInteger(0)
        maps.stream().flatMap { it: Expansion? -> it.getAllLocations().stream() }.sorted { l1: Location?, l2: Location? -> l1.getCoordinates().compareTo(l2.getCoordinates()) }.forEach { it: Location? -> it.setNumber(locationNumer.getAndIncrement()) }
        set.forEach(Consumer { it: Pair<ExpansionFactory, Expansion?> ->
            it.right.getCoordinates().calcDisplayLocation(SIZE)
            it.right.getAllLocations().stream().map { obj: Location? -> obj.getCoordinates() }.forEach { coords: Coordinates? ->
                coords!!.shift(-0.5f, 0f)
                coords.mirrorY(0.5f)
                coords.calcDisplayLocation(SIZE)
            }
        })
        return set
    }

    fun createRenderless(settings: ExpansionSetting?): List<Expansion?> {
        return create(settings).stream().map { it: Pair<ExpansionFactory, Expansion?> -> it.right }.collect(Collectors.toList())
    }
}
