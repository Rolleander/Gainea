package com.broll.gainea.server.core.map.impl;

import com.broll.gainea.server.core.map.AreaCollection;
import com.broll.gainea.server.core.map.Continent;
import com.broll.gainea.server.core.map.Island;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.client.render.ExpansionRender;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.ExpansionType;

import java.util.ArrayList;
import java.util.List;

public class GaineaMap extends ExpansionFactory {

    public GaineaMap() {
        super(ExpansionType.GAINEA);
    }

    private Area uferland, manma, kuestengebiet, gruenland, vulkaninsel, grossewueste, grossesteppe, kiesstrand, moorwueste, hiengland, mistra, moorkueste, moor, xomdelta, totem;

    @Override
    public ExpansionRender createRender() {
        return new ExpansionRender("expansion_0.png");
    }

    @Override
    protected void init(List<AreaCollection> contents) {
        Continent gainea = gainea();
        Continent zuba = zuba();
        Continent moor = moor();
        Island vulkan = vulkanInsel();
        Island totemIsland = totemInsel();
        Island mistraIsland = mistraInsel();
        contents.add(gainea);
        contents.add(zuba);
        contents.add(moor);
        contents.add(vulkan);
        contents.add(totemIsland);
        contents.add(mistraIsland);
        //ships
        gainea.setShips(list(ship(grossewueste, vulkaninsel, 32.9f, 47.5f), ship(grossewueste, totem, 29.2f, 67.1f), ship(hiengland, moorwueste, 65.4f, 58.3f)));
        zuba.setShips(list(ship(grossesteppe, kuestengebiet, 33.7f, 25.7f)));
        List<Ship> moorShips = new ArrayList<>();
        moorShips.add(ship(this.moor, mistra, 85.9f, 55.4f));
        moorShips.addAll(ships(moorkueste, xomdelta, new float[]{65, 60}, new float[]{46.3f, 43.9f}));
        moorShips.addAll(ships(moorwueste, kiesstrand, new float[]{72.6f, 69.4f, 63.6f, 59.5f}, new float[]{70f, 75.8f, 77, 74.8f}));
        moor.setShips(moorShips);
        vulkan.setShips(list(ship(vulkaninsel, manma, 22.1f, 50.3f), ship(vulkaninsel, gruenland, 34.5f, 42.6f)));
        totemIsland.setShips(list(ship(totem, kiesstrand, 42.7f, 70.7f)));
        mistraIsland.setShips(list(ship(mistra, moorwueste, 79.7f, 63.7f)));
    }

    private Continent gainea() {
        kuestengebiet = area("Küstengebiet", AreaType.PLAINS, 41.4f, 23.6f);
        Area felswald = area("Felswald", AreaType.MOUNTAIN, 55.9f, 20.7f);
        Area felswueste = area("Felsenwüste", AreaType.DESERT, 72.1f, 23.5f);
        gruenland = area("Grünland", AreaType.PLAINS, 46.7f, 35.8f);
        xomdelta = area("Xom-Delta", AreaType.PLAINS, 57.2f, 37.3f);
        Area weidesteppe = area("Weidesteppe", AreaType.PLAINS, 45.7f, 54.2f);
        grossewueste = area("Große Wüste", AreaType.DESERT, 31.2f, 57.6f);
        Area dunklesMeer = area("Dunkles Meer", AreaType.LAKE, 42.2f, 60.3f);
        kiesstrand = area("Kiesstrand", AreaType.PLAINS, 37, 65.1f);
        hiengland = area("Hiengland", AreaType.DESERT, 56.6f, 61.5f);
        Area zwingsee = area("Zwingsee", AreaType.LAKE, 50.4f, 64.9f);
        Area korberg = area("Korberg", AreaType.MOUNTAIN, 54.8f, 69.3f);
        kuestengebiet.addAdjacentLocation(felswald);
        kuestengebiet.addAdjacentLocation(felswueste);
        kuestengebiet.addAdjacentLocation(gruenland);
        felswueste.addAdjacentLocation(felswald);
        felswueste.addAdjacentLocation(gruenland);
        felswald.addAdjacentLocation(gruenland);
        gruenland.addAdjacentLocation(xomdelta);
        gruenland.addAdjacentLocation(weidesteppe);
        xomdelta.addAdjacentLocation(weidesteppe);
        weidesteppe.addAdjacentLocation(grossewueste);
        weidesteppe.addAdjacentLocation(dunklesMeer);
        weidesteppe.addAdjacentLocation(hiengland);
        grossewueste.addAdjacentLocation(kiesstrand);
        dunklesMeer.addAdjacentLocation(kiesstrand);
        dunklesMeer.addAdjacentLocation(hiengland);
        kiesstrand.addAdjacentLocation(zwingsee);
        kiesstrand.addAdjacentLocation(hiengland);
        kiesstrand.addAdjacentLocation(korberg);
        zwingsee.addAdjacentLocation(hiengland);
        korberg.addAdjacentLocation(hiengland);
        return continent("Gainea", list(kuestengebiet, felswald, felswueste, gruenland, xomdelta, weidesteppe, grossewueste, dunklesMeer, kiesstrand, hiengland, zwingsee, korberg));
    }

    private Continent moor() {
        moorkueste = area("Moorküste", AreaType.PLAINS, 74.2f, 46.8f);
        moor = area("Moor", AreaType.PLAINS, 81.3f, 49.2f);
        moorwueste = area("Moorwüste", AreaType.DESERT, 71.8f, 63.8f);
        Area moorteich = area("Moorteich", AreaType.LAKE, 73.5f, 56f);
        moorkueste.addAdjacentLocation(moor);
        moorkueste.addAdjacentLocation(moorwueste);
        moor.addAdjacentLocation(moorteich);
        moorwueste.addAdjacentLocation(moorteich);
        return continent("Moor", list(moorteich, moor, moorkueste, moorwueste));
    }

    private Continent zuba() {
        Area grFelsGeb = area("Großes Feslgebirge", AreaType.MOUNTAIN, 25.2f, 24.4f);
        uferland = area("Uferland", AreaType.PLAINS, 16.7f, 29.8f);
        Area landstrand = area("Landstrand", AreaType.PLAINS, 15, 36.6f);
        manma = area("Manma Wüste", AreaType.DESERT, 17.9f, 45.6f);
        grossesteppe = area("Große Steppe", AreaType.PLAINS, 25.8f, 30.8f);
        Area mitsuma = area("Mitsuma See", AreaType.LAKE, 26.5f, 36.6f);
        grFelsGeb.addAdjacentLocation(uferland);
        grFelsGeb.addAdjacentLocation(grossesteppe);
        uferland.addAdjacentLocation(landstrand);
        uferland.addAdjacentLocation(grossesteppe);
        grossesteppe.addAdjacentLocation(mitsuma);
        landstrand.addAdjacentLocation(manma);
        landstrand.addAdjacentLocation(grossesteppe);
        manma.addAdjacentLocation(grossesteppe);
        return continent("Zuba", list(grFelsGeb, uferland, landstrand, manma, grossesteppe, mitsuma));
    }

    private Island vulkanInsel() {
        vulkaninsel = area("Vulkaninsel", AreaType.PLAINS, 25.8f, 49.2f);
        Area vulkanberg = area("Vulkanberg", AreaType.MOUNTAIN, 29.1f, 44.9f);
        vulkaninsel.addAdjacentLocation(vulkanberg);
        return island("Vulkaninsel", list(vulkaninsel, vulkanberg));
    }

    private Island totemInsel() {
        totem = area("Totemgebirge / Insel", AreaType.MOUNTAIN, 34.6f, 71.2f);
        return island("Toteminsel", list(totem));
    }

    private Island mistraInsel() {
        mistra = area("Mistra Insel / Wüste", AreaType.DESERT, 86.3f, 62.8f);
        return island("Mistrainsel", list(mistra));
    }

    public Area getUferland() {
        return uferland;
    }

    public Area getManma() {
        return manma;
    }

    public Area getKuestengebiet() {
        return kuestengebiet;
    }

    @Override
    protected void connectExpansion(ExpansionFactory expansion) {

    }
}
