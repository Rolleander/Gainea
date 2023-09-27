package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.TeleportCardimport

com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.utils.LocationUtilsimport java.util.stream.Collectors
class C_TeleportLake : TeleportCard(47, "Küstenreise", "Bewegt eine Truppe zu einem beliebigem Meer auf der gleichen Karte") {
    override fun getTeleportTargets(from: Location?): List<Location?>? {
        return LocationUtils.filterByType(from.getContainer().expansion.allLocations, AreaType.LAKE).collect(Collectors.toList())
    }
}
