package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.TeleportCardimport

com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.utils.LocationUtilsimport java.util.stream.Collectors
class C_TeleportSwamp : TeleportCard(45, "Sumpferkundung", "Bewegt eine Truppe zu einem beliebigen Sumpf auf der gleichen Karte") {
    override fun getTeleportTargets(from: Location?): List<Location?>? {
        return LocationUtils.filterByType(from.getContainer().expansion.allLocations, AreaType.BOG).collect(Collectors.toList())
    }
}
