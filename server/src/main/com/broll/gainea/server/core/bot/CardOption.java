package com.broll.gainea.server.core.bot;

import com.broll.gainea.server.core.bot.impl.BotSelect;
import com.broll.gainea.server.core.objects.BattleObject;

import java.util.ArrayList;
import java.util.List;

public class CardOption extends BotOptionalAction.BotOption {

    private List<BotSelect.Selection> selectOptions = new ArrayList<>();

    public CardOption(float score) {
        super(score);
    }

    public CardOption() {
        super(20);
    }

    public void select(BotSelect.Selection option) {
        selectOptions.add(option);
    }

    public void selectStrongestUnit(BotSelect select) {
        selectOptions.add(select.selectUnit(units ->
                BotUtils.getHighestScoreEntry(units, BattleObject::getBattleStrength)));
    }
    
    public List<BotSelect.Selection> getSelectOptions() {
        return selectOptions;
    }
}
