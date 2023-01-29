package com.broll.gainea.server.core.map.impl;

import com.broll.gainea.client.ui.ingame.map.ExpansionRender;
import com.broll.gainea.server.core.map.AreaID;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ContinentID;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.IslandID;

public class BoglandMap extends ExpansionFactory {

    public enum Continents implements ContinentID {
        GOMIX, TOD
    }

    public enum Islands implements IslandID {
        TODESINSEL, KLEINSPALT, STACHELINSEL
    }

    public enum Areas implements AreaID {
        TODESINSEL, SUED_OST_PLATTE, SUED_WEST_PLATTE, TODESSCHLAMM, NORD_OST_PLATTE, NORD_WEST_PLATTE, SKELETTWALD,
        MORDBRUECKE, DUNKELFORST, FROSTKAPP, KNOCHENTAL, MAHNSCHLAMM, SCHAEDELTEICH, LEBLSOER_GIPFEL, GRUSELSUMPF,
        TROCKENSCHAEDEL, UNDURCHDRINGBAR, TODESSE, GRAUENSWALD, OBERE_INSEL, UNTERE_INSEL, SCHRECKENHORN, STACHELWUESTE,
        ZOMBIESUMPF
    }

    public BoglandMap() {
        super(ExpansionType.BOGLANDS);
        setBaseCoordinates(-0.25f, 0.9f);
    }

    @Override
    public ExpansionRender createRender() {
        return new ExpansionRender("expansion_2.png");
    }

    @Override
    protected void init() {
        gomix();
        tod();
    }

    private void gomix() {
        continent(Continents.GOMIX, "Gomix", list(
                area(Areas.SUED_OST_PLATTE, "Süd Ost Platte", AreaType.PLAINS, 17.6f, 27.6f),
                area(Areas.SUED_WEST_PLATTE, "Süd West Platte", AreaType.SNOW, 32.4f, 33.5f),
                area(Areas.TODESSCHLAMM, "Todes-Schlamm", AreaType.BOG, 23.9f, 38.5f),
                area(Areas.SUED_WEST_PLATTE, "Nord Ost Platte", AreaType.DESERT, 13.8f, 46.1f),
                area(Areas.NORD_WEST_PLATTE, "Nord West Platte", AreaType.MOUNTAIN, 28.4f, 48.7f),
                area(Areas.SKELETTWALD, "Skelettwald", AreaType.PLAINS, 22.2f, 58.5f),
                area(Areas.DUNKELFORST, "Dunkelforst", AreaType.PLAINS, 34.9f, 63.3f),
                area(Areas.MORDBRUECKE, "Mordbrücke", AreaType.PLAINS, 38.3f, 39.8f),
                area(Areas.SUED_WEST_PLATTE, "Knochental", AreaType.DESERT, 44.7f, 36f),
                area(Areas.MAHNSCHLAMM, "Mahnschlamm", AreaType.BOG, 46.3f, 45.3f),
                area(Areas.FROSTKAPP, "Frostkapp", AreaType.SNOW, 50.1f, 32.8f)
        ));
        connect(Areas.SUED_OST_PLATTE, Areas.TODESSCHLAMM);
        connect(Areas.SUED_OST_PLATTE, Areas.NORD_OST_PLATTE);
        connect(Areas.SUED_OST_PLATTE, Areas.SUED_WEST_PLATTE);
        connect(Areas.SUED_WEST_PLATTE, Areas.MORDBRUECKE);
        connect(Areas.SUED_WEST_PLATTE, Areas.NORD_WEST_PLATTE);
        connect(Areas.SUED_WEST_PLATTE, Areas.TODESSCHLAMM);
        connect(Areas.MORDBRUECKE, Areas.NORD_WEST_PLATTE);
        connect(Areas.MORDBRUECKE, Areas.KNOCHENTAL);
        connect(Areas.KNOCHENTAL, Areas.MAHNSCHLAMM);
        connect(Areas.KNOCHENTAL, Areas.FROSTKAPP);
        connect(Areas.NORD_OST_PLATTE, Areas.TODESSCHLAMM);
        connect(Areas.NORD_OST_PLATTE, Areas.NORD_WEST_PLATTE);
        connect(Areas.NORD_OST_PLATTE, Areas.SKELETTWALD);
        connect(Areas.TODESSCHLAMM, Areas.NORD_WEST_PLATTE);
        connect(Areas.NORD_WEST_PLATTE, Areas.SKELETTWALD);
        connect(Areas.NORD_WEST_PLATTE, Areas.DUNKELFORST);
        connect(Areas.SKELETTWALD, Areas.DUNKELFORST);
    }

    private void tod(){
        continent(Continents.TOD, "Tod", list(
                area(Areas.SCHAEDELTEICH, "Schädelteil", AreaType.BOG, 61.7f, 24.9f),
                area(Areas.LEBLSOER_GIPFEL, "Lebloser Gipfel", AreaType.MOUNTAIN, 67.7f, 22.4f),
                area(Areas.TROCKENSCHAEDEL, "Trockenschädel", AreaType.DESERT, 76.2f, 40.8f),
                area(Areas.GRUSELSUMPF, "Gruselsumpf", AreaType.BOG, 78.8f, 29.3f),
                area(Areas.UNDURCHDRINGBAR, "Undurchdringbar", AreaType.PLAINS, 66.8f, 45.4f),
                area(Areas.GRAUENSWALD, "Grauenswald", AreaType.PLAINS, 85.1f, 59.2f),
                area(Areas.TODESSE, "Todessee", AreaType.LAKE, 79.8f, 60.6f)
        ));
        connect(Areas.SCHAEDELTEICH, Areas.LEBLSOER_GIPFEL);
        connect(Areas.SCHAEDELTEICH, Areas.UNDURCHDRINGBAR);
        connect(Areas.SCHAEDELTEICH, Areas.TROCKENSCHAEDEL);
        connect(Areas.TROCKENSCHAEDEL, Areas.UNDURCHDRINGBAR);
        connect(Areas.TROCKENSCHAEDEL, Areas.GRUSELSUMPF);
        connect(Areas.TROCKENSCHAEDEL, Areas.GRAUENSWALD);
        connect(Areas.GRAUENSWALD, Areas.TODESSE);
    }

    @Override
    protected void connectWithExpansion(ExpansionFactory expansion) {

    }


}
