package com.broll.gainea.server.core.map.impl;

import com.broll.gainea.server.core.map.AreaID;
import com.broll.gainea.server.core.map.ContinentID;
import com.broll.gainea.server.core.map.IslandID;
import com.broll.gainea.client.ui.ingame.map.ExpansionRender;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.ExpansionType;

import static com.broll.gainea.server.core.map.impl.GaineaMap.Areas.*;
import static com.broll.gainea.server.core.map.impl.GaineaMap.Islands.*;
import static com.broll.gainea.server.core.map.impl.GaineaMap.Continents.*;

public class GaineaMap extends ExpansionFactory {

    public enum Continents implements ContinentID {
        GAINEA, MOOR, ZUBA
    }

    public enum Islands implements IslandID {
        VULKANINSEL, MISTRAINSEL, TOTEMINSEL
    }

    public enum Areas implements AreaID {
        KUESTENGEBIET, FELSWALD, FELSENWUESTE, GRUENLAND, XOMDELTA, WEIDESTEPPE, GROSSEWUESTE, DUNKLESMEER, KIESSTRAND, HIENGLAND, ZWINGSEE, KORBERG, MOORKUESTE, MOOR, MOORWUESTE, MOORTEICH, GROSSES_FELSGEBIRGE, UFERLAND, LANDSTRAND, MANMAWUESTE, GROSSE_STEPPE, MITSUMA_SEE, VULKANINSEL, VULKANBERG, TOTEMGEBIRGE, MISTRAWUESTE
    }

    public GaineaMap() {
        super(ExpansionType.GAINEA);
    }

    @Override
    public ExpansionRender createRender() {
        return new ExpansionRender("expansion_0.png");
    }

    @Override
    protected void init() {
        gainea();
        moor();
        zuba();
        vulkanInsel();
        totemInsel();
        mistraInsel();
        ships();
    }

    @Override
    protected void connectWithExpansion(ExpansionFactory expansion) {

    }

    private void gainea() {
        continent(GAINEA, "Gainea", list(
                area(KUESTENGEBIET, "Küstengebiet", AreaType.PLAINS, 41.4f, 23.6f),
                area(FELSWALD, "Felswald", AreaType.MOUNTAIN, 55.9f, 20.7f),
                area(FELSENWUESTE, "Felsenwüste", AreaType.DESERT, 72.1f, 23.5f),
                area(GRUENLAND, "Grünland", AreaType.PLAINS, 46.7f, 35.8f),
                area(XOMDELTA, "Xom-Delta", AreaType.PLAINS, 57.2f, 37.3f),
                area(WEIDESTEPPE, "Weidesteppe", AreaType.PLAINS, 45.7f, 54.2f),
                area(GROSSEWUESTE, "Große Wüste", AreaType.DESERT, 31.2f, 57.6f),
                area(DUNKLESMEER, "Dunkles Meer", AreaType.LAKE, 42.2f, 60.3f),
                area(KIESSTRAND, "Kiesstrand", AreaType.PLAINS, 37, 65.1f),
                area(HIENGLAND, "Hiengland", AreaType.DESERT, 56.6f, 61.5f),
                area(ZWINGSEE, "Zwingsee", AreaType.LAKE, 50.4f, 64.9f),
                area(KORBERG, "Korberg", AreaType.MOUNTAIN, 54.8f, 69.3f)
        ));
        connect(KUESTENGEBIET, FELSWALD);
        connect(KUESTENGEBIET, FELSENWUESTE);
        connect(KUESTENGEBIET, GRUENLAND);
        connect(FELSENWUESTE, FELSWALD);
        connect(FELSENWUESTE, GRUENLAND);
        connect(FELSWALD, GRUENLAND);
        connect(GRUENLAND, XOMDELTA);
        connect(GRUENLAND, WEIDESTEPPE);
        connect(XOMDELTA, WEIDESTEPPE);
        connect(WEIDESTEPPE, GROSSEWUESTE);
        connect(WEIDESTEPPE, DUNKLESMEER);
        connect(WEIDESTEPPE, HIENGLAND);
        connect(GROSSEWUESTE, KIESSTRAND);
        connect(DUNKLESMEER, KIESSTRAND);
        connect(DUNKLESMEER, HIENGLAND);
        connect(KIESSTRAND, ZWINGSEE);
        connect(KIESSTRAND, HIENGLAND);
        connect(KIESSTRAND, KORBERG);
        connect(ZWINGSEE, HIENGLAND);
        connect(KORBERG, HIENGLAND);
    }

    private void moor() {
        continent(Continents.MOOR, "Moor", list(
                area(MOORKUESTE, "Moorküste", AreaType.PLAINS, 74.2f, 46.8f),
                area(Areas.MOOR, "Moor", AreaType.PLAINS, 81.3f, 49.2f),
                area(MOORWUESTE, "Moorwüste", AreaType.DESERT, 71.8f, 63.8f),
                area(MOORTEICH, "Moorteich", AreaType.LAKE, 73.5f, 56f)
        ));
        connect(MOORKUESTE, Areas.MOOR);
        connect(MOORKUESTE, MOORWUESTE);
        connect(Areas.MOOR, MOORTEICH);
        connect(MOORWUESTE, MOORTEICH);
        connect(MOORWUESTE, Areas.MOOR);
    }

    private void zuba() {
        continent(ZUBA, "Zuba", list(
                area(GROSSES_FELSGEBIRGE, "Großes Feslgebirge", AreaType.MOUNTAIN, 25.2f, 24.4f),
                area(UFERLAND, "Uferland", AreaType.PLAINS, 16.7f, 29.8f),
                area(LANDSTRAND, "Landstrand", AreaType.PLAINS, 15, 36.6f),
                area(MANMAWUESTE, "Manma Wüste", AreaType.DESERT, 17.9f, 45.6f),
                area(GROSSE_STEPPE, "Große Steppe", AreaType.PLAINS, 25.8f, 30.8f),
                area(MITSUMA_SEE, "Mitsuma See", AreaType.LAKE, 26.5f, 36.6f)));
        connect(GROSSES_FELSGEBIRGE, UFERLAND);
        connect(GROSSES_FELSGEBIRGE, GROSSE_STEPPE);
        connect(UFERLAND, LANDSTRAND);
        connect(UFERLAND, GROSSE_STEPPE);
        connect(GROSSE_STEPPE, MITSUMA_SEE);
        connect(LANDSTRAND, MANMAWUESTE);
        connect(LANDSTRAND, GROSSE_STEPPE);
        connect(MANMAWUESTE, GROSSE_STEPPE);
    }

    private void vulkanInsel() {
        island(Islands.VULKANINSEL, "Vulkaninsel", list(
                area(Areas.VULKANINSEL, "Vulkaninsel", AreaType.PLAINS, 25.8f, 49.2f),
                area(VULKANBERG, "Vulkanberg", AreaType.MOUNTAIN, 29.1f, 44.9f)
        ));
        connect(Areas.VULKANINSEL, VULKANBERG);
    }

    private void totemInsel() {
        island(TOTEMINSEL, "Toteminsel", list(
                area(TOTEMGEBIRGE, "Totemgebirge / Insel", AreaType.MOUNTAIN, 34.6f, 71.2f)
        ));
    }

    private void mistraInsel() {
        island(MISTRAINSEL, "Mistrainsel", list(
                area(MISTRAWUESTE, "Mistra Insel / Wüste", AreaType.DESERT, 86.3f, 62.8f)
        ));
    }

    private void ships() {
        //from gainea
        ship(GROSSEWUESTE, Areas.VULKANINSEL, 32.9f, 47.5f);
        ship(GROSSEWUESTE, TOTEMGEBIRGE, 29.2f, 67.1f);
        ship(HIENGLAND, MOORWUESTE, 65.4f, 58.3f);
        //from moor
        ship(Areas.MOOR, MISTRAWUESTE, 85.9f, 55.4f);
        ships(MOORKUESTE, XOMDELTA, new float[]{65, 60}, new float[]{46.3f, 43.9f});
        ships(MOORWUESTE, KIESSTRAND, new float[]{72.6f, 69.4f, 63.6f, 59.5f}, new float[]{70f, 75.8f, 77, 74.8f});
        //from zuba
        ship(GROSSE_STEPPE, KUESTENGEBIET, 33.7f, 25.7f);
        //from vulkaninsel
        ship(Areas.VULKANINSEL, MANMAWUESTE, 22.1f, 50.3f);
        ship(Areas.VULKANINSEL, GRUENLAND, 34.5f, 42.6f);
        //from toteminsel
        ship(TOTEMGEBIRGE, KIESSTRAND, 42.7f, 70.7f);
        //from mistrainsel
        ship(MISTRAWUESTE, MOORWUESTE, 79.7f, 63.7f);
    }

}
