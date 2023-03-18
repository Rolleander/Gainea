package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Event;
import com.broll.gainea.server.core.bot.CardOption;
import com.broll.gainea.server.core.bot.impl.BotSelect;
import com.broll.gainea.server.core.bot.strategy.BotStrategy;
import com.broll.gainea.server.core.bot.strategy.ICardStrategy;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_Buff extends Card implements ICardStrategy {
    public C_Buff() {
        super(30, "Aufstieg", "Verleiht einer eurer Einheiten +1/+1");
    }

    @Override
    public boolean isPlayable() {
        return !owner.getUnits().isEmpty();
    }

    @Override
    protected void play() {
        Unit unit = SelectionUtils.selectPlayerUnit(game, owner, "Welche Einheit soll gestärkt werden?");
        if (unit != null) {
            unit.getPower().addValue(1);
            unit.changeHealth(1);
            UnitControl.focus(game, unit, NT_Event.EFFECT_BUFF);
        }
    }

    @Override
    public CardOption strategy(BotStrategy strategy, BotSelect select) {
        CardOption option = new CardOption();
        option.selectStrongestUnit(select);
        return option;
    }
}
