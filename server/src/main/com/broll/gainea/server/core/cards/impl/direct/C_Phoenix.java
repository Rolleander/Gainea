package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.objects.monster.MonsterMotion;

public class C_Phoenix extends DirectlyPlayedCard {

    public C_Phoenix() {
        super(65, "Beschworener Phönix", "Beschwört einen Phönix (2/2), dieser kann 2 Felder pro Zug bewegt werden aber kann nicht angreifen.");
        setDrawChance(0.8f);
    }

    @Override
    protected void play() {
        Monster monster = new Monster();
        monster.setName("Phönix");
        monster.getAttacksPerTurn().setValue(0);
        monster.getMovesPerTurn().setValue(2);
        monster.setHealth(2);
        monster.setPower(2);
        monster.setIcon(124);
        monster.setMotion(MonsterMotion.AIRBORNE);
        monster.setOwner(owner);
        placeUnitHandler.placeUnit(owner, monster, owner.getControlledLocations(), "Ort der Beschwörung wählen");
    }

}
