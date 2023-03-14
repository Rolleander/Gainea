package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.utils.GameUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;
import java.util.stream.Collectors;

public class C_TakeOver extends Card {
    public C_TakeOver() {
        super(17, "Treueschwur", "Übernehmt die Einheit eines anderen Spielers (Ausser Feldherr)" +
                " und teleportiert sie zu eurem Feldherr.");
        setDrawChance(0.5f);
    }

    @Override
    public boolean isPlayable() {
        return PlayerUtils.isCommanderAlive(owner) && !getTargets().isEmpty();
    }

    private List<Unit> getTargets() {
        return game.getAllPlayers().stream().flatMap(it -> it.getUnits().stream()).filter(it -> !PlayerUtils.isCommander(it)).collect(Collectors.toList());
    }

    @Override
    protected void play() {
        Unit unit = SelectionUtils.selectUnit(game, owner, "Welche Einheit soll übernommen werden?", getTargets());
        unit.setOwner(owner);
        UnitControl.move(game, unit, PlayerUtils.getCommander(owner).get().getLocation());
        GameUtils.sendUpdate(game, game.nt());
    }
}
