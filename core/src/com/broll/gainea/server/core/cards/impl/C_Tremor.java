package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.RequiredActionContext;
import com.broll.gainea.server.core.actions.impl.SelectChoiceAction;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.IteratePlayersProcess;
import com.broll.gainea.server.core.utils.UnitUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class C_Tremor extends DirectlyPlayedCard {
    public C_Tremor() {
        super("Beben", "Verursacht 1 Schaden an einer zufälligen Einheit jedes Spielers");
    }

    @Override
    public void play(ActionHandlers actionHandlers) {
        IteratePlayersProcess.run(game, (player, iterator) -> {
            BattleObject unit = RandomUtils.pickRandom(player.getUnits());
            if (unit != null) {
                //deal 1 damage
                UnitUtils.damageUnit(game, unit, 1);
                iterator.nextPlayer();
            } else {
                iterator.continueNextPlayer();
            }
        });
    }

}
