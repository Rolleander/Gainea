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
import com.broll.gainea.net.proto.*;
import com.broll.gainea.core.cards.EffectType;
import com.broll.gainea.core.goals.GoalDifficulty;
import com.broll.gainea.client.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryWindow extends MenuWindow {

    public LibraryWindow(Gainea game) {
        super(game, "Info", game.ui.skin);
        TableUtils.consumeClicks(this);
        List<Pair<String, Actor>> tabs = Arrays.asList(
                Pair.of("Fraktionen", fractions()),
                Pair.of("Karten", cards()),
                Pair.of("Ziele", goals()),
                Pair.of("Monster", monsters()));
        add(new ListSelectionPane(game.ui.skin, tabs)).grow().top();
        center(1050, 750);
    }


    private Actor fractions() {
        return new ListSelectionPane(getSkin(), game.state.library.getFractionsList().stream().map(fraction -> Pair.of(fraction.getName(), (Actor) new FractionTable(game, fraction))).collect(Collectors.toList()), true);
    }

    private Actor cards() {
        return new ListSelectionPane(getSkin(), Arrays.stream(EffectType.values()).map(type -> {
            Table cards = new Table();
            cards.top();
            cards.defaults().pad(5);
            List<NT_Lib_Card> cardData = game.state.library.getCardsList().stream().filter(it -> it.getType() == type.ordinal()).collect(Collectors.toList());
            cardData.stream().map(it -> {
                return NT_Card.newBuilder().setPicture(it.getPicture()).setTitle(it.getTitle())
                    .setText(it.getText()).setPlayable(false).setEvent(it.getDirectlyPlayed()).build();
            }).forEach(card -> cards.add(CardWindow.renderCard(game, card)).row());
            return Pair.of(type.getDisplayName() + " (" + cardData.size() + ")", (Actor) new VerticalScrollPane(cards, skin));
        }).collect(Collectors.toList()), true);
    }

    private Actor monsters() {
        Table table = new Table();
        table.defaults().pad(10);
        int cols = 2;
        for (int i = 0; i < game.state.library.getMonstersCount(); i++) {
            NT_Lib_Monster monster = game.state.library.getMonsters(i);
            NT_Monster nt = NT_Monster.newBuilder().setStars(monster.getStars()).setBehavior(monster.getBehavior())
                .setIcon(monster.getIcon()).setDescription(monster.getDescription()).setPower(monster.getPower())
                .setHealth(monster.getHealth()).setMaxHealth(monster.getHealth()).setName(monster.getName()).build();
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
            List<NT_Lib_Goal> goals = game.state.library.getGoalsList().stream().filter(it -> it.getPoints() == difficulty.getPoints()).collect(Collectors.toList());
            goals.stream().forEach(goal -> {
                NT_Goal nt = NT_Goal.newBuilder().setDescription(goal.getDescription()).setPoints(goal.getPoints())
                    .setRestriction(goal.getRestriction()).build();
                table.add(GoalOverlay.renderGoal(game, nt)).row();
            });
            return Pair.of(difficulty.getLabel() + " (" + goals.size() + ")", (Actor) new VerticalScrollPane(table, skin));
        }).collect(Collectors.toList()), true);
    }


    @Override
    public void update() {

    }
}
