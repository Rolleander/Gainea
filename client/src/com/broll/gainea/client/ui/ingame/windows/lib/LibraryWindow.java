package com.broll.gainea.client.ui.ingame.windows.lib;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.components.ListSelectionPane;
import com.broll.gainea.client.ui.components.VerticalScrollPane;
import com.broll.gainea.client.ui.ingame.hud.GoalOverlay;
import com.broll.gainea.client.ui.ingame.unit.MenuUnit;
import com.broll.gainea.client.ui.ingame.windows.CardWindow;
import com.broll.gainea.client.ui.ingame.windows.MenuWindow;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.net.NT_Card;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.net.NT_Lib_Card;
import com.broll.gainea.net.NT_Lib_Goal;
import com.broll.gainea.net.NT_Lib_Monster;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.server.core.cards.EffectType;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryWindow extends MenuWindow {

    public LibraryWindow(Gainea game) {
        super(game, "Info", game.ui.skin);
        TableUtils.consumeClicks(this);
        List<Pair<String, Actor>> tabs = Lists.newArrayList(
                Pair.of("Fraktionen", fractions()),
                Pair.of("Karten", cards()),
                Pair.of("Ziele", goals()),
                Pair.of("Monster", monsters()));
        add(new ListSelectionPane(game.ui.skin, tabs)).grow().top();
        center(1050, 750);
    }


    private Actor fractions() {
        return new ListSelectionPane(getSkin(), Arrays.stream(game.state.library.fractions).map(fraction -> Pair.of(fraction.name, (Actor) new FractionTable(game, fraction))).collect(Collectors.toList()), true);
    }

    private Actor cards() {
        return new ListSelectionPane(getSkin(), Arrays.stream(EffectType.values()).map(type -> {
            Table cards = new Table();
            cards.top();
            cards.defaults().pad(5);
            List<NT_Lib_Card> cardData = Arrays.stream(game.state.library.cards).filter(it -> it.type == type.ordinal()).collect(Collectors.toList());
            cardData.stream().map(it -> {
                NT_Card ntC = new NT_Card();
                ntC.picture = it.picture;
                ntC.title = it.title;
                ntC.text = it.text;
                ntC.playable = false;
                ntC.event = it.directlyPlayed;
                return ntC;
            }).forEach(card -> cards.add(CardWindow.renderCard(game, card)).row());
            return Pair.of(type.getDisplayName() + " (" + cardData.size() + ")", (Actor) new VerticalScrollPane(cards, skin));
        }).collect(Collectors.toList()), true);
    }

    private Actor monsters() {
        Table table = new Table();
        table.defaults().pad(10);
        int cols = 2;
        for (int i = 0; i < game.state.library.monsters.length; i++) {
            NT_Lib_Monster monster = game.state.library.monsters[i];
            NT_Monster nt = new NT_Monster();
            nt.stars = monster.stars;
            nt.behavior = monster.behavior;
            nt.icon = monster.icon;
            nt.description = monster.description;
            nt.power = monster.power;
            nt.health = monster.health;
            nt.maxHealth = monster.health;
            nt.name = monster.name;
            Cell<MenuUnit> cell = table.add(new MenuUnit(game, nt, true));
            if (i % cols == cols - 1) {
                cell.row();
            }
        }
        return new VerticalScrollPane(table, skin);
    }

    private Actor goals() {
        return new ListSelectionPane(getSkin(), Arrays.stream(GoalDifficulty.values()).map(difficulty -> {
            Table table = new Table();
            table.top();
            table.defaults().pad(5);
            List<NT_Lib_Goal> goals = Arrays.stream(game.state.library.goals).filter(it -> it.points == difficulty.getPoints()).collect(Collectors.toList());
            goals.stream().forEach(goal -> {
                NT_Goal nt = new NT_Goal();
                nt.description = goal.description;
                nt.points = goal.points;
                nt.restriction = goal.restriction;
                table.add(GoalOverlay.renderGoal(game, nt)).row();
            });
            return Pair.of(difficulty.getLabel() + " (" + goals.size() + ")", (Actor) new VerticalScrollPane(table, skin));
        }).collect(Collectors.toList()), true);
    }


    @Override
    public void update() {

    }
}
