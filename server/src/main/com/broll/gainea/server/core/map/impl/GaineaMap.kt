package com.broll.gainea.server.core.map.impl

import com.broll.gainea.server.core.map.AreaID
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.ContinentID
import com.broll.gainea.server.core.map.ExpansionFactory
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.map.IslandID

class GaineaMap : ExpansionFactory(ExpansionType.GAINEA, "expansion_0.png") {
    enum class Continents : ContinentID {
        GAINEA,
        MOOR,
        ZUBA
    }

    enum class Islands : IslandID {
        VULKANINSEL,
        MISTRAINSEL,
        TOTEMINSEL
    }

    enum class Areas : AreaID {
        KUESTENGEBIET,
        FELSWALD,
        FELSENWUESTE,
        GRUENLAND,
        XOMDELTA,
        WEIDESTEPPE,
        GROSSEWUESTE,
        DUNKLESMEER,
        KIESSTRAND,
        HIENGLAND,
        ZWINGSEE,
        KORBERG,
        MOORKUESTE,
        MOOR,
        MOORWUESTE,
        MOORTEICH,
        GROSSES_FELSGEBIRGE,
        UFERLAND,
        LANDSTRAND,
        MANMAWUESTE,
        GROSSE_STEPPE,
        MITSUMA_SEE,
        VULKANINSEL,
        VULKANBERG,
        TOTEMGEBIRGE,
        MISTRAWUESTE
    }


    override fun init() {
        gainea()
        moor()
        zuba()
        vulkanInsel()
        totemInsel()
        mistraInsel()
        ships()
    }

    private fun gainea() {
        continent(
            Continents.GAINEA, "Gainea", listOf(
                area(Areas.KUESTENGEBIET, "Küstengebiet", AreaType.PLAINS, 41.4f, 23.6f),
                area(Areas.FELSWALD, "Felswald", AreaType.MOUNTAIN, 55.9f, 20.7f),
                area(Areas.FELSENWUESTE, "Felsenwüste", AreaType.DESERT, 72.1f, 23.5f),
                area(Areas.GRUENLAND, "Grünland", AreaType.PLAINS, 46.7f, 35.8f),
                area(Areas.XOMDELTA, "Xom-Delta", AreaType.PLAINS, 57.2f, 37.3f),
                area(Areas.WEIDESTEPPE, "Weidesteppe", AreaType.PLAINS, 45.7f, 54.2f),
                area(Areas.GROSSEWUESTE, "Große Wüste", AreaType.DESERT, 31.2f, 57.6f),
                area(Areas.DUNKLESMEER, "Dunkles Meer", AreaType.LAKE, 42.2f, 60.3f),
                area(Areas.KIESSTRAND, "Kiesstrand", AreaType.PLAINS, 37f, 65.1f),
                area(Areas.HIENGLAND, "Hiengland", AreaType.DESERT, 56.6f, 61.5f),
                area(Areas.ZWINGSEE, "Zwingsee", AreaType.LAKE, 50.4f, 64.9f),
                area(Areas.KORBERG, "Korberg", AreaType.MOUNTAIN, 54.8f, 69.3f)
            )
        )
        connect(Areas.KUESTENGEBIET, Areas.FELSWALD)
        connect(Areas.KUESTENGEBIET, Areas.FELSENWUESTE)
        connect(Areas.KUESTENGEBIET, Areas.GRUENLAND)
        connect(Areas.FELSENWUESTE, Areas.FELSWALD)
        connect(Areas.FELSENWUESTE, Areas.GRUENLAND)
        connect(Areas.FELSWALD, Areas.GRUENLAND)
        connect(Areas.GRUENLAND, Areas.XOMDELTA)
        connect(Areas.GRUENLAND, Areas.WEIDESTEPPE)
        connect(Areas.XOMDELTA, Areas.WEIDESTEPPE)
        connect(Areas.WEIDESTEPPE, Areas.GROSSEWUESTE)
        connect(Areas.WEIDESTEPPE, Areas.DUNKLESMEER)
        connect(Areas.WEIDESTEPPE, Areas.HIENGLAND)
        connect(Areas.GROSSEWUESTE, Areas.KIESSTRAND)
        connect(Areas.DUNKLESMEER, Areas.KIESSTRAND)
        connect(Areas.DUNKLESMEER, Areas.HIENGLAND)
        connect(Areas.KIESSTRAND, Areas.ZWINGSEE)
        connect(Areas.KIESSTRAND, Areas.HIENGLAND)
        connect(Areas.KIESSTRAND, Areas.KORBERG)
        connect(Areas.ZWINGSEE, Areas.HIENGLAND)
        connect(Areas.KORBERG, Areas.HIENGLAND)
    }

    private fun moor() {
        continent(
            Continents.MOOR, "Moor", listOf(
                area(Areas.MOORKUESTE, "Moorküste", AreaType.PLAINS, 74.2f, 46.8f),
                area(Areas.MOOR, "Moor", AreaType.PLAINS, 81.3f, 49.2f),
                area(Areas.MOORWUESTE, "Moorwüste", AreaType.DESERT, 71.8f, 63.8f),
                area(Areas.MOORTEICH, "Moorteich", AreaType.LAKE, 73.5f, 56f)
            )
        )
        connect(Areas.MOORKUESTE, Areas.MOOR)
        connect(Areas.MOORKUESTE, Areas.MOORWUESTE)
        connect(Areas.MOOR, Areas.MOORTEICH)
        connect(Areas.MOORWUESTE, Areas.MOORTEICH)
        connect(Areas.MOORWUESTE, Areas.MOOR)
    }

    private fun zuba() {
        continent(
            Continents.ZUBA, "Zuba", listOf(
                area(
                    Areas.GROSSES_FELSGEBIRGE,
                    "Großes Feslgebirge",
                    AreaType.MOUNTAIN,
                    25.2f,
                    24.4f
                ),
                area(Areas.UFERLAND, "Uferland", AreaType.PLAINS, 16.7f, 29.8f),
                area(Areas.LANDSTRAND, "Landstrand", AreaType.PLAINS, 15f, 36.6f),
                area(Areas.MANMAWUESTE, "Manma Wüste", AreaType.DESERT, 17.9f, 45.6f),
                area(Areas.GROSSE_STEPPE, "Große Steppe", AreaType.PLAINS, 25.8f, 30.8f),
                area(Areas.MITSUMA_SEE, "Mitsuma See", AreaType.LAKE, 26.5f, 36.6f)
            )
        )
        connect(Areas.GROSSES_FELSGEBIRGE, Areas.UFERLAND)
        connect(Areas.GROSSES_FELSGEBIRGE, Areas.GROSSE_STEPPE)
        connect(Areas.UFERLAND, Areas.LANDSTRAND)
        connect(Areas.UFERLAND, Areas.GROSSE_STEPPE)
        connect(Areas.GROSSE_STEPPE, Areas.MITSUMA_SEE)
        connect(Areas.LANDSTRAND, Areas.MANMAWUESTE)
        connect(Areas.LANDSTRAND, Areas.GROSSE_STEPPE)
        connect(Areas.MANMAWUESTE, Areas.GROSSE_STEPPE)
    }

    private fun vulkanInsel() {
        island(
            Islands.VULKANINSEL, "Vulkaninsel", listOf(
                area(Areas.VULKANINSEL, "Vulkaninsel", AreaType.PLAINS, 25.8f, 49.2f),
                area(Areas.VULKANBERG, "Vulkanberg", AreaType.MOUNTAIN, 29.1f, 44.9f)
            )
        )
        connect(Areas.VULKANINSEL, Areas.VULKANBERG)
    }

    private fun totemInsel() {
        island(
            Islands.TOTEMINSEL, "Toteminsel", listOf(
                area(Areas.TOTEMGEBIRGE, "Totemgebirge", AreaType.MOUNTAIN, 34.6f, 71.2f)
            )
        )
    }

    private fun mistraInsel() {
        island(
            Islands.MISTRAINSEL, "Mistrainsel", listOf(
                area(Areas.MISTRAWUESTE, "Mistrawüste", AreaType.DESERT, 86.3f, 62.8f)
            )
        )
    }

    private fun ships() {
        //from gainea
        ship(Areas.GROSSEWUESTE, Areas.VULKANINSEL, 32.9f, 47.5f)
        ship(Areas.GROSSEWUESTE, Areas.TOTEMGEBIRGE, 29.2f, 67.1f)
        ship(Areas.HIENGLAND, Areas.MOORWUESTE, 65.4f, 58.3f)
        //from moor
        ship(Areas.MOOR, Areas.MISTRAWUESTE, 85.9f, 55.4f)
        ships(Areas.MOORKUESTE, Areas.XOMDELTA, floatArrayOf(65f, 60f), floatArrayOf(46.3f, 43.9f))
        ships(
            Areas.MOORWUESTE,
            Areas.KIESSTRAND,
            floatArrayOf(72.6f, 69.4f, 63.6f, 59.5f),
            floatArrayOf(70f, 75.8f, 77f, 74.8f)
        )
        //from zuba
        ship(Areas.GROSSE_STEPPE, Areas.KUESTENGEBIET, 33.7f, 25.7f)
        //from vulkaninsel
        ship(Areas.VULKANINSEL, Areas.MANMAWUESTE, 22.1f, 50.3f)
        ship(Areas.VULKANINSEL, Areas.GRUENLAND, 34.5f, 42.6f)
        //from toteminsel
        ship(Areas.TOTEMGEBIRGE, Areas.KIESSTRAND, 42.7f, 70.7f)
        //from mistrainsel
        ship(Areas.MISTRAWUESTE, Areas.MOORWUESTE, 79.7f, 63.7f)
    }

    override fun connectWithExpansion(expansion: ExpansionFactory) {
        if (expansion is IcelandMap) {
            ships(
                Areas.UFERLAND,
                IcelandMap.Areas.GRASSUMPF,
                floatArrayOf(8f, -0.5f),
                floatArrayOf(29.4f, 27.4f)
            )
            ships(
                Areas.MANMAWUESTE,
                IcelandMap.Areas.SCHOLL,
                floatArrayOf(10.9f, 2.7f, -5.5f),
                floatArrayOf(45f, 46.5f, 48.1f)
            )
        }
        if (expansion is BoglandMap) {
            ships(
                Areas.KUESTENGEBIET,
                BoglandMap.Areas.STACHELWUESTE,
                floatArrayOf(38.2f, 33.8f, 34f),
                floatArrayOf(11.8f, 7f, 2f)
            )
        }
    }
}
