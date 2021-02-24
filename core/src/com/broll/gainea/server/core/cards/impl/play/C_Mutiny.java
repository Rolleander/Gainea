package com.broll.gainea.server.core.cards.impl.play;

import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class C_Mutiny extends AbstractCard {

    private static float DAMAGE_CHANCE = 0.5f;

    public C_Mutiny() {
        super(15, "Meuterei", "Verursacht 1 Schaden an zufälligen Einheiten auf Schiffen");
        setDrawChance(0.6f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        List<BattleObject> units = game.getMap().getAllShips().stream().flatMap(it -> it.getInhabitants().stream()).
                filter(it -> it instanceof BattleObject).map(it -> (BattleObject) it).collect(Collectors.toList());
        Collections.shuffle(units);
        units.forEach(unit -> {
            if (MathUtils.randomBoolean(DAMAGE_CHANCE)) {
                UnitControl.damage(game, unit, 1);
            }
        });
    }

}
