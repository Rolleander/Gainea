package com.broll.gainea.server.core.map.impl

import com.broll.gainea.server.core.map.AreaID
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.ContinentID
import com.broll.gainea.server.core.map.ExpansionFactory
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.map.IslandID

class IcelandMap : ExpansionFactory(ExpansionType.ICELANDS, "expansion_1.png") {
    enum class Continents : ContinentID {
        TERON,
        SEISM,
        TOTEM
    }

    enum class Islands : IslandID {
        SCHELL,
        PACK,
        KORTOD,
        SCHOLL
    }

    enum class Areas : AreaID {
        KAPEIS,
        TERGEBIRGE,
        KAELTESTEPPE,
        SCOLLSEE,
        EISLAND,
        KAHL,
        GRASSUMPF,
        SCHLAMMSUMPF,
        SCHNEEBERG,
        SCHNEEWALD,
        SCHNEEFELDER,
        FRIERMEER,
        WEISSESGEBIET,
        WEISSESMEER,
        TOTEM,
        GRUENESMEER,
        SCHELLEIS,
        GLETSCHER,
        PACKEIS,
        KORTOD,
        EISSEE,
        SCHOLL,
        GLETSCHERPFUETZE,
        FREILAND
    }

    init {
        baseCoordinates.set(-0.82f, 0.235f)
    }


    override fun init() {
        teron()
        seism()
        totem()
        schell()
        pack()
        scholl()
        kortod()
        ships()
    }

    private fun teron() {
        continent(Continents.TERON, "Teron", listOf(
                area(Areas.KAPEIS, "Kap Eis", AreaType.SNOW, 44.8f, 14.1f),
                area(Areas.TERGEBIRGE, "Tergebirge", AreaType.MOUNTAIN, 59.9f, 17.4f),
                area(Areas.KAELTESTEPPE, "Kältesteppe", AreaType.SNOW, 56f, 24f),
                area(Areas.KAHL, "Kahl", AreaType.PLAINS, 69.1f, 25.3f),
                area(Areas.SCOLLSEE, "Scoll See", AreaType.LAKE, 62.3f, 29.7f),
                area(Areas.EISLAND, "Eisland", AreaType.SNOW, 76f, 31.3f),
                area(Areas.GRASSUMPF, "Grassumpf", AreaType.PLAINS, 74.8f, 48.1f),
                area(Areas.SCHLAMMSUMPF, "Schlammsumpf", AreaType.LAKE, 78.5f, 41.4f)
        ))
        connect(Areas.KAPEIS, Areas.KAHL)
        connect(Areas.KAPEIS, Areas.TERGEBIRGE)
        connect(Areas.KAPEIS, Areas.KAELTESTEPPE)
        connect(Areas.TERGEBIRGE, Areas.KAELTESTEPPE)
        connect(Areas.TERGEBIRGE, Areas.KAHL)
        connect(Areas.KAELTESTEPPE, Areas.KAHL)
        connect(Areas.KAHL, Areas.SCOLLSEE)
        connect(Areas.KAHL, Areas.EISLAND)
        connect(Areas.EISLAND, Areas.GRASSUMPF)
        connect(Areas.GRASSUMPF, Areas.SCHLAMMSUMPF)
    }

    private fun seism() {
        continent(Continents.SEISM, "Seism", listOf(
                area(Areas.SCHNEEWALD, "Schneewald", AreaType.SNOW, 24.6f, 33.2f),
                area(Areas.SCHNEEBERG, "Schneeberg", AreaType.MOUNTAIN, 28.1f, 25.8f),
                area(Areas.SCHNEEFELDER, "Schneefelder", AreaType.SNOW, 42.1f, 38.1f),
                area(Areas.FRIERMEER, "Friermeer", AreaType.LAKE, 38.4f, 43.5f),
                area(Areas.WEISSESGEBIET, "Weißes Gebiert", AreaType.SNOW, 58.6f, 47.3f)
        ))
        connect(Areas.SCHNEEBERG, Areas.SCHNEEWALD)
        connect(Areas.SCHNEEWALD, Areas.SCHNEEFELDER)
        connect(Areas.SCHNEEFELDER, Areas.FRIERMEER)
        connect(Areas.SCHNEEFELDER, Areas.WEISSESGEBIET)
    }

    private fun totem() {
        continent(Continents.TOTEM, "Totem", listOf(
                area(Areas.TOTEM, "Totem", AreaType.SNOW, 23.8f, 65.1f),
                area(Areas.WEISSESMEER, "Weißes Meer", AreaType.LAKE, 21.4f, 53.6f),
                area(Areas.GRUENESMEER, "Grünes Meer", AreaType.PLAINS, 23.5f, 73.3f)
        ))
        connect(Areas.TOTEM, Areas.WEISSESMEER)
        connect(Areas.TOTEM, Areas.GRUENESMEER)
    }

    private fun schell() {
        island(Islands.SCHELL, "Schell", listOf(
                area(Areas.SCHELLEIS, "Schell Eis", AreaType.SNOW, 35.1f, 60.6f),
                area(Areas.GLETSCHER, "Gletscher", AreaType.MOUNTAIN, 38.9f, 65.2f)
        ))
        connect(Areas.SCHELLEIS, Areas.GLETSCHER)
    }

    private fun pack() {
        island(Islands.PACK, "Pack", listOf(
                area(Areas.PACKEIS, "Pack Eis", AreaType.SNOW, 50f, 74.6f)
        ))
    }

    private fun kortod() {
        island(Islands.KORTOD, "Kortod", listOf(
                area(Areas.KORTOD, "Kortod", AreaType.SNOW, 53.3f, 58.5f),
                area(Areas.EISSEE, "Eis See", AreaType.LAKE, 59.2f, 58.6f)
        ))
        connect(Areas.KORTOD, Areas.EISSEE)
    }

    private fun scholl() {
        island(Islands.SCHOLL, "Scholl", listOf(
                area(Areas.SCHOLL, "Scholl", AreaType.SNOW, 63.6f, 72.3f),
                area(Areas.FREILAND, "Freiland", AreaType.PLAINS, 67.7f, 80.1f),
                area(Areas.GLETSCHERPFUETZE, "Gletscherpfütze", AreaType.LAKE, 69f, 71.8f)
        ))
        connect(Areas.SCHOLL, Areas.GLETSCHERPFUETZE)
        connect(Areas.SCHOLL, Areas.FREILAND)
    }

    private fun ships() {
        //from kap eis
        ship(Areas.KAPEIS, Areas.SCHNEEWALD, 36.7f, 20.7f)
        //from schneefelder
        ships(Areas.SCHNEEFELDER, Areas.KAELTESTEPPE, floatArrayOf(44.7f, 47f), floatArrayOf(31.3f, 27.2f))
        ship(Areas.SCHNEEFELDER, Areas.TOTEM, 23.2f, 43f)
        //from weissesgebiet
        ship(Areas.WEISSESGEBIET, Areas.KORTOD, 61.4f, 52.8f)
        //from totem
        ships(Areas.TOTEM, Areas.SCHNEEFELDER, floatArrayOf(31.8f, 35.5f), floatArrayOf(50.7f, 49.1f))
        ship(Areas.TOTEM, Areas.SCHELLEIS, 30.6f, 61.2f)
        //from schell
        ship(Areas.SCHELLEIS, Areas.GRUENESMEER, 33.4f, 68.1f)
        ship(Areas.SCHELLEIS, Areas.KORTOD, 46.8f, 55.4f)
        //from kortod
        ship(Areas.KORTOD, Areas.SCHNEEFELDER, 49.1f, 47.7f)
        ship(Areas.KORTOD, Areas.PACKEIS, 53.2f, 68.2f)
        //from pack
        ships(Areas.PACKEIS, Areas.GRUENESMEER, floatArrayOf(37.3f, 33.6f), floatArrayOf(74.3f, 73.4f))
        ship(Areas.PACKEIS, Areas.SCHOLL, 58.3f, 72.7f)
        //from scholl
        ship(Areas.SCHOLL, Areas.KORTOD, 63f, 66f)
    }

    override fun connectWithExpansion(expansion: ExpansionFactory) {
        if (expansion is GaineaMap) {
            ships(Areas.GRASSUMPF, GaineaMap.Areas.UFERLAND, floatArrayOf(84.2f, 92.1f), floatArrayOf(45.1f, 48.2f))
            ships(Areas.FREILAND, GaineaMap.Areas.GROSSEWUESTE, floatArrayOf(76f, 83f, 91f, 100f), floatArrayOf(79f, 79.5f, 81f, 83f))
        }
        if (expansion is BoglandMap) {
            ships(Areas.EISLAND, BoglandMap.Areas.SCHRECKENHORN, floatArrayOf(89.4f, 92.3f, 95.5f), floatArrayOf(29.9f, 24.9f, 20f))
        }
    }
}
