package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_BoardEffect;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapEffect;
import com.broll.gainea.server.core.objects.buffs.TimedEffect;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;

public class C_Fire extends Card {

    private final static int ROUNDS = 5;

    public C_Fire() {
        super(75, "Drachenfeuer", "Wählt ein unbesetztes Gebiet (ausser Seen), dieses kann für " + ROUNDS + " Runden nicht besetzt werden.");
    }

    @Override
    public boolean isPlayable() {
        return !getTargets().isEmpty();
    }

    private List<Location> getTargets() {
        return game.getMap().getAllAreas().stream().filter(it -> !LocationUtils.isAreaType(it, AreaType.LAKE) && it.isFree()).collect(Collectors.toList());
    }

    @Override
    protected void play() {
        Location target = selectHandler.selectLocation(owner, "Welches Gebiet blockieren?", getTargets());
        MapEffect effect = new MapEffect(NT_BoardEffect.EFFECT_FIRE, "", target);
        MapEffect.spawn(game, effect);
        target.setTraversable(false);
        TimedEffect.forPlayerRounds(game, owner, ROUNDS, new TimedEffect() {
            @Override
            protected void unregister() {
                super.unregister();
                MapEffect.despawn(game, effect);
                target.setTraversable(true);
            }
        });
    }
}
