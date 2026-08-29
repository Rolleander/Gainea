package com.broll.gainea.server.core.map

import com.broll.gainea.core.map.ExpansionType
import com.broll.gainea.net.proto.NT_Expansion
import com.broll.gainea.net.proto.NT_Location
import com.broll.gainea.net.proto.NT_LocationContainer
import com.broll.gainea.net.proto.NT_Ship

class Expansion(
    val type: ExpansionType,
    val coordinates: Coordinates
) {
    val contents = mutableListOf<AreaCollection>()
    val allLocations: List<Location>
        get() = listOf(allAreas, allShips).flatten()
    val allAreas: List<Area>
        get() = contents.flatMap { it.areas }
    val allShips: List<Ship>
        get() = contents.flatMap { it.ships }
    val islands: List<Island>
        get() = contents.filterIsInstance(Island::class.java)

    val continents: List<Continent>
        get() = contents.filterIsInstance(Continent::class.java)

    fun getIsland(id: IslandID) = islands.find { it.id == id }

    fun getContinent(id: ContinentID) = continents.find { it.id == id }

    fun nt(): NT_Expansion =
        NT_Expansion.newBuilder()
            .setName(type.expansionName)
            .setTexture(type.textureName)
            .addAllLocationContainers(
                contents.map { it.nt() }
            ).addAllShips(
                allShips.map {
                    NT_Ship.newBuilder().setLocation(it.nt()).setFrom(it.from.number)
                        .setTo(it.to.number)
                        .build()
                }
            ).build()

    private fun AreaCollection.nt() =
        NT_LocationContainer.newBuilder().setName(name).addAllLocations(areas.map { it.nt() })
            .build()

    private fun Location.nt() =
        NT_Location.newBuilder().setNumber(number).setX(coordinates.displayX)
            .setY(coordinates.displayY).setName(name).build()
}
