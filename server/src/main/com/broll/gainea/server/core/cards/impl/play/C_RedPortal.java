package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_BoardEffect;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapEffect;
import com.broll.gainea.server.core.objects.buffs.TimedEffect;

import java.util.stream.Collectors;

public class C_RedPortal extends Card {

    private final static int ROUNDS = 4;

    public C_RedPortal() {
        super(1, "Dunkles Portal",
                "Wählt zwei freie Gebiete von der gleichen Karte. Stellt ein Portal zwischen diesen Gebieten für " + ROUNDS + " Runden her.");
        setDrawChance(0.6f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Location from = selectHandler.selectLocation("Wähle den Startort für das Portal", game.getMap().getAllAreas().stream().filter(Location::isFree).collect(Collectors.toList()));
        MapEffect startPortal = new MapEffect(NT_BoardEffect.EFFECT_PORTAL, "", from);
        MapEffect.spawn(game, startPortal);
        Location to = selectHandler.selectLocation("Wähle den Zielort für das Portal", from.getContainer().getExpansion().getAllAreas().stream()
                .filter(it -> it.isFree() && it != from && !from.getConnectedLocations().contains(it)).collect(Collectors.toList()));
        if (from != null && to != null) {
            MapEffect endPortal = new MapEffect(NT_BoardEffect.EFFECT_PORTAL, "", to);
            MapEffect.spawn(game, endPortal);
            from.getConnectedLocations().add(to);
            to.getConnectedLocations().add(from);
            TimedEffect.forPlayerRounds(game, owner, ROUNDS, new TimedEffect() {
                @Override
                protected void unregister() {
                    super.unregister();
                    MapEffect.despawn(game, startPortal);
                    MapEffect.despawn(game, endPortal);
                    from.getConnectedLocations().remove(to);
                    to.getConnectedLocations().remove(from);
                }
            });
        } else {
            MapEffect.despawn(game, startPortal);
        }
    }

}