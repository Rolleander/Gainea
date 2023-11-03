package com.broll.gainea.test

import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.AreaID
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.DUNKLESMEER
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.FELSWALD
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.GROSSEWUESTE
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.GRUENLAND
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.HIENGLAND
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.KIESSTRAND
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.KORBERG
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.MISTRAWUESTE
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.MOORWUESTE
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.TOTEMGEBIRGE
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.WEIDESTEPPE
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.XOMDELTA
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.ZWINGSEE
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.getConnectedLocations
import com.broll.gainea.server.core.utils.getDistance
import com.broll.gainea.server.core.utils.getWalkingDistance
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain same`
import org.junit.jupiter.api.Test

class DistanceTest {

    val game = testGame()

    val unit = Soldier(game.neutralPlayer)

    fun area(area: AreaID) = game.map.getArea(area)!!

    @Test
    fun `distances are correct`() {
        area(HIENGLAND).getDistance(area(FELSWALD)) `should be equal to` 3
        area(KORBERG).getDistance(area(MISTRAWUESTE)) `should be equal to` 5
    }

    @Test
    fun `walking distances are correct`() {
        area(HIENGLAND).getWalkingDistance(area(FELSWALD), unit) `should be equal to` 3
        area(KORBERG).getWalkingDistance(area(MISTRAWUESTE), unit) `should be equal to` 6
    }
    
    @Test
    fun `connected distances are correct`() {
        area(KORBERG).getConnectedLocations(1)
            .filterIsInstance<Area>() `should contain same` listOf(
            area(HIENGLAND),
            area(KIESSTRAND),
        )
        area(KORBERG).getConnectedLocations(2)
            .filterIsInstance<Area>() `should contain same` listOf(
            area(HIENGLAND),
            area(KIESSTRAND),
            area(GROSSEWUESTE),
            area(WEIDESTEPPE),
            area(ZWINGSEE),
            area(DUNKLESMEER),
        )
        area(KORBERG).getConnectedLocations(3)
            .filterIsInstance<Area>() `should contain same` listOf(
            area(HIENGLAND),
            area(KIESSTRAND),
            area(GROSSEWUESTE),
            area(WEIDESTEPPE),
            area(ZWINGSEE),
            area(DUNKLESMEER),
            area(TOTEMGEBIRGE),
            area(GRUENLAND),
            area(XOMDELTA),
            area(MOORWUESTE),
        )
    }

}