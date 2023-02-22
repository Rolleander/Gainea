package com.broll.gainea.server.core.bot.strategy;

import com.broll.gainea.server.core.bot.CardOption;
import com.broll.gainea.server.core.bot.impl.BotSelect;

public interface ICardStrategy {

    CardOption strategy(BotStrategy strategy, BotSelect select);
}
