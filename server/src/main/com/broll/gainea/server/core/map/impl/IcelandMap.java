package com.broll.gainea.server.core.map.impl;

import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.EISLAND;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.EISSEE;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.FREILAND;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.FRIERMEER;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.GLETSCHER;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.GLETSCHERPFUETZE;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.GRASSUMPF;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.GRUENESMEER;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.KAELTESTEPPE;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.KAHL;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.KAPEIS;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.PACKEIS;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.SCHELLEIS;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.SCHLAMMSUMPF;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.SCHNEEBERG;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.SCHNEEFELDER;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.SCHNEEWALD;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.SCOLLSEE;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.TERGEBIRGE;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.WEISSESGEBIET;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.WEISSESMEER;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Continents.SEISM;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Continents.TERON;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Islands.PACK;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Islands.SCHELL;

import com.broll.gainea.server.core.map.AreaID;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ContinentID;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.IslandID;

public class IcelandMap extends ExpansionFactory {
    public enum Continents implements ContinentID {
        TERON, SEISM, TOTEM
    }

    public enum Islands implements IslandID {
        SCHELL, PACK, KORTOD, SCHOLL
    }

    public enum Areas implements AreaID {
        KAPEIS, TERGEBIRGE, KAELTESTEPPE, SCOLLSEE, EISLAND, KAHL, GRASSUMPF, SCHLAMMSUMPF, SCHNEEBERG, SCHNEEWALD, SCHNEEFELDER, FRIERMEER, WEISSESGEBIET, WEISSESMEER,
        TOTEM, GRUENESMEER, SCHELLEIS, GLETSCHER, PACKEIS, KORTOD, EISSEE, SCHOLL, GLETSCHERPFUETZE, FREILAND
    }

    public IcelandMap() {
        super(ExpansionType.ICELANDS);
        setBaseCoordinates(-0.82f, 0.235f);
    }

    @Override
    public String getTexture() {
        return "expansion_1.png";
    }

    @Override
    protected void init() {
        teron();
        seism();
        totem();
        schell();
        pack();
        scholl();
        kortod();
        ships();
    }

    private void teron() {
        continent(TERON, "Teron", list(
                area(KAPEIS, "Kap Eis", AreaType.SNOW, 44.8f, 14.1f),
                area(TERGEBIRGE, "Tergebirge", AreaType.MOUNTAIN, 59.9f, 17.4f),
                area(KAELTESTEPPE, "Kältesteppe", AreaType.SNOW, 56f, 24f),
                area(KAHL, "Kahl", AreaType.PLAINS, 69.1f, 25.3f),
                area(SCOLLSEE, "Scoll See", AreaType.LAKE, 62.3f, 29.7f),
                area(EISLAND, "Eisland", AreaType.SNOW, 76f, 31.3f),
                area(GRASSUMPF, "Grassumpf", AreaType.PLAINS, 74.8f, 48.1f),
                area(SCHLAMMSUMPF, "Schlammsumpf", AreaType.LAKE, 78.5f, 41.4f)
        ));
        connect(KAPEIS, KAHL);
        connect(KAPEIS, TERGEBIRGE);
        connect(KAPEIS, KAELTESTEPPE);
        connect(TERGEBIRGE, KAELTESTEPPE);
        connect(TERGEBIRGE, KAHL);
        connect(KAELTESTEPPE, KAHL);
        connect(KAHL, SCOLLSEE);
        connect(KAHL, EISLAND);
        connect(EISLAND, GRASSUMPF);
        connect(GRASSUMPF, SCHLAMMSUMPF);
    }

    private void seism() {
        continent(SEISM, "Seism", list(
                area(SCHNEEWALD, "Schneewald", AreaType.SNOW, 24.6f, 33.2f),
                area(SCHNEEBERG, "Schneeberg", AreaType.MOUNTAIN, 28.1f, 25.8f),
                area(SCHNEEFELDER, "Schneefelder", AreaType.SNOW, 42.1f, 38.1f),
                area(FRIERMEER, "Friermeer", AreaType.LAKE, 38.4f, 43.5f),
                area(WEISSESGEBIET, "Weißes Gebiert", AreaType.SNOW, 58.6f, 47.3f)
        ));
        connect(SCHNEEBERG, SCHNEEWALD);
        connect(SCHNEEWALD, SCHNEEFELDER);
        connect(SCHNEEFELDER, FRIERMEER);
        connect(SCHNEEFELDER, WEISSESGEBIET);
    }

    private void totem() {
        continent(Continents.TOTEM, "Totem", list(
                area(Areas.TOTEM, "Totem", AreaType.SNOW, 23.8f, 65.1f),
                area(WEISSESMEER, "Weißes Meer", AreaType.LAKE, 21.4f, 53.6f),
                area(GRUENESMEER, "Grünes Meer", AreaType.PLAINS, 23.5f, 73.3f)
        ));
        connect(Areas.TOTEM, WEISSESMEER);
        connect(Areas.TOTEM, GRUENESMEER);
    }

    private void schell() {
        island(SCHELL, "Schell", list(
                area(SCHELLEIS, "Schell Eis", AreaType.SNOW, 35.1f, 60.6f),
                area(GLETSCHER, "Gletscher", AreaType.MOUNTAIN, 38.9f, 65.2f)
        ));
        connect(SCHELLEIS, GLETSCHER);
    }

    private void pack() {
        island(PACK, "Pack", list(
                area(PACKEIS, "Pack Eis", AreaType.SNOW, 50f, 74.6f)
        ));
    }

    private void kortod() {
        island(Islands.KORTOD, "Kortod", list(
                area(Areas.KORTOD, "Kortod", AreaType.SNOW, 53.3f, 58.5f),
                area(EISSEE, "Eis See", AreaType.LAKE, 59.2f, 58.6f)
        ));
        connect(Areas.KORTOD, EISSEE);
    }

    private void scholl() {
        island(Islands.SCHOLL, "Scholl", list(
                area(Areas.SCHOLL, "Scholl", AreaType.SNOW, 63.6f, 72.3f),
                area(FREILAND, "Freiland", AreaType.PLAINS, 67.7f, 80.1f),
                area(GLETSCHERPFUETZE, "Gletscherpfütze", AreaType.LAKE, 69, 71.8f)
        ));
        connect(Areas.SCHOLL, GLETSCHERPFUETZE);
        connect(Areas.SCHOLL, FREILAND);
    }

    private void ships() {
        //from kap eis
        ship(KAPEIS, SCHNEEWALD, 36.7f, 20.7f);
        //from schneefelder
        ships(SCHNEEFELDER, KAELTESTEPPE, new float[]{44.7f, 47}, new float[]{31.3f, 27.2f});
        ship(SCHNEEFELDER, Areas.TOTEM, 23.2f, 43);
        //from weissesgebiet
        ship(WEISSESGEBIET, Areas.KORTOD, 61.4f, 52.8f);
        //from totem
        ships(Areas.TOTEM, SCHNEEFELDER, new float[]{31.8f, 35.5f}, new float[]{50.7f, 49.1f});
        ship(Areas.TOTEM, SCHELLEIS, 30.6f, 61.2f);
        //from schell
        ship(SCHELLEIS, GRUENESMEER, 33.4f, 68.1f);
        ship(SCHELLEIS, Areas.KORTOD, 46.8f, 55.4f);
        //from kortod
        ship(Areas.KORTOD, SCHNEEFELDER, 49.1f, 47.7f);
        ship(Areas.KORTOD, PACKEIS, 53.2f, 68.2f);
        //from pack
        ships(PACKEIS, GRUENESMEER, new float[]{37.3f, 33.6f}, new float[]{74.3f, 73.4f});
        ship(PACKEIS, Areas.SCHOLL, 58.3f, 72.7f);
        //from scholl
        ship(Areas.SCHOLL, Areas.KORTOD, 63, 66);
    }

    @Override
    protected void connectWithExpansion(ExpansionFactory expansion) {
        if (expansion instanceof GaineaMap) {
            ships(GRASSUMPF, GaineaMap.Areas.UFERLAND, new float[]{84.2f, 92.1f}, new float[]{45.1f, 48.2f});
            ships(FREILAND, GaineaMap.Areas.GROSSEWUESTE, new float[]{76f, 83f, 91f, 100f}, new float[]{79f, 79.5f, 81f, 83f});
        }
        if (expansion instanceof BoglandMap) {
            ships(EISLAND, BoglandMap.Areas.SCHRECKENHORN, new float[]{89.4f, 92.3f, 95.5f}, new float[]{29.9f, 24.9f, 20f});
        }
    }

}
