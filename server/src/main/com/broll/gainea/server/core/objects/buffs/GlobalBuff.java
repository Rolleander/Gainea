package com.broll.gainea.server.core.objects.buffs;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GlobalBuff {

    private Buff buff;
    private Consumer<BattleObject> applier;
    private List<Player> targets;

    private GlobalBuff(List<Player> targets, Buff buff, Consumer<BattleObject> applier) {
        this.buff = buff;
        this.applier = applier;
        this.targets = targets;
    }

    public static void createForPlayer(GameContainer game, Player target, Buff buff, Consumer<BattleObject> applier, int effect) {
        register(game, new GlobalBuff(Lists.newArrayList(target), buff, applier), effect);
    }

    public static void createForAllPlayers(GameContainer game, Buff buff, Consumer<BattleObject> applier, int effect) {
        register(game, new GlobalBuff(new ArrayList<>(game.getPlayers()), buff, applier), effect);
    }

    public static void createForPlayers(GameContainer game, List<Player> targets, Buff buff, Consumer<BattleObject> applier, int effect) {
        register(game, new GlobalBuff(new ArrayList<>(targets), buff, applier), effect);
    }

    public static void createForNeutral(GameContainer game, Buff buff, Consumer<BattleObject> applier, int effect) {
        register(game, new GlobalBuff(Lists.newArrayList((Player) null), buff, applier), effect);
    }

    public static void createForAll(GameContainer game, Buff buff, Consumer<BattleObject> applier, int effect) {
        List<Player> targets = new ArrayList<>();
        targets.addAll(game.getPlayers());
        targets.add(null);
        register(game, new GlobalBuff(targets, buff, applier), effect);
    }

    private static void register(GameContainer game, GlobalBuff buff, int effect) {
        game.getBuffProcessor().addGlobalBuff(buff, effect);
    }

    public Buff getBuff() {
        return buff;
    }

    public List<Player> getTargets() {
        return targets;
    }

    public void apply(BattleObject object) {
        applier.accept(object);
    }
}
