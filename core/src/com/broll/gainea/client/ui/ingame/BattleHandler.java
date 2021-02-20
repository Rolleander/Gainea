package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.client.ui.elements.Popup;
import com.broll.gainea.client.ui.elements.render.MapObjectRender;
import com.broll.gainea.client.ui.elements.render.UnitRender;
import com.broll.gainea.client.ui.elements.BattleRollRender;
import com.broll.gainea.client.ui.elements.IRollAnimationListener;
import com.broll.gainea.client.ui.elements.TableUtils;
import com.broll.gainea.client.ui.elements.TextureUtils;
import com.broll.gainea.net.NT_Battle_Reaction;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.esotericsoftware.minlog.Log;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class BattleHandler {

    private Gainea game;
    private Skin skin;
    private BattleBoard battleBoard;
    private List<NT_Unit> attackers;
    private List<NT_Unit> defenders;

    public BattleHandler(Gainea game, Skin skin) {
        this.game = game;
        this.skin = skin;
    }

    public BattleBoard startBattle(List<NT_Unit> attackers, List<NT_Unit> defenders, Location location) {
        if (battleBoard != null) {
            battleBoard.remove();
        }
        this.attackers = attackers;
        this.defenders = defenders;
        battleBoard = new BattleBoard(location);
        battleBoard.setSize(1000, 800);
        return battleBoard;
    }

    public void updateBattle(int[] attackRolls, int[] defenderRolls, Stack<NT_Unit> damageOrderAttacker, Stack<NT_Unit> damageOrderDefender, int state) {
        Log.info("damage to attackers: " + damageOrderAttacker.stream().map(it -> it.name).collect(Collectors.joining(",")));
        Log.info("damage to defenders: " + damageOrderDefender.stream().map(it -> it.name).collect(Collectors.joining(",")));
        battleBoard.attackRolls(attackRolls, defenderRolls, new IRollAnimationListener() {
            @Override
            public void diceSet(boolean attackerWon) {
                Stack<NT_Unit> damageOrder = damageOrderAttacker;
                if (attackerWon) {
                    damageOrder = damageOrderDefender;
                }
                if (!damageOrder.empty()) {
                    damage(damageOrder.pop());
                }
            }

            @Override
            public void rollingDone() {
                doRemainingDamage(damageOrderAttacker);
                doRemainingDamage(damageOrderDefender);
                checkBattleState(state);
            }

            private void doRemainingDamage(Stack<NT_Unit> units) {
                while (!units.empty()) {
                    damage(units.pop());
                }
            }

            private void damage(NT_Unit unit) {
                if (unit != null) {
                    battleBoard.attackerRenders.stream().filter(it -> it.getObject().id == unit.id).findFirst().ifPresent(it -> it.takeDamage(1));
                    battleBoard.defenderRenders.stream().filter(it -> it.getObject().id == unit.id).findFirst().ifPresent(it -> it.takeDamage(1));
                }
            }
        });
    }

    private void checkBattleState(int state) {
        battleBoard.addAction(Actions.delay(2, Actions.run(() -> {
            if (state == NT_Battle_Update.STATE_FIGHTING) {
                //if we are attacker we can decide if keep on attacking
                if (game.state.getPlayer().getId() == attackers.get(0).owner) {
                    Table dialog = new Table(game.ui.skin);
                    dialog.setBackground("menu-bg");
                    dialog.pad(10, 20, 10, 20);
                    dialog.defaults().space(20);
                    dialog.add(LabelUtils.label(game.ui.skin, "Eure Truppe erwartet Befehle!")).row();
                    dialog.add(TableUtils.textButton(game.ui.skin, "Anrgiff fortfahren", () -> sendBattleResponse(dialog, true))).left();
                    dialog.add(TableUtils.textButton(game.ui.skin, "Rückzug", () -> sendBattleResponse(dialog, false))).right();
                    Popup.show(game, dialog);
                }
            } else {
                //battle done
                String text = "Sieg der Verteidiger!";
                if (state == NT_Battle_Update.STATE_ATTACKER_WON) {
                    text = "Sieg der Angreifer!";
                }
                battleEnd(text);
            }
        })));
    }

    private void battleEnd(String text) {
        Label label = LabelUtils.title(game.ui.skin, text);
        label.addAction(Actions.delay(2, Actions.run(() -> {
            battleBoard.remove();
            game.state.turnIdle();
        })));
        battleBoard.add(label).padTop(100).top();
//        game.ui.inGameUI.showCenterOverlay(label).padTop(100).top();
    }

    private void sendBattleResponse(Table dialog, boolean continueFight) {
        dialog.remove();
        NT_Battle_Reaction reaction = new NT_Battle_Reaction();
        reaction.keepAttacking = continueFight;
        game.client.getClient().sendTCP(reaction);
        if (!continueFight) {
            battleEnd("Rückzug der Angreifer!");
        }
    }

    private class BattleBoard extends Table {
        private List<UnitRender> attackerRenders;
        private List<UnitRender> defenderRenders;
        private BattleRollRender rollRender;

        public BattleBoard(Location location) {
            super(skin);
            TableUtils.consumeClicks(this);
            int backgroundNr = 0;
            if (location instanceof Area) {
                backgroundNr = ((Area) location).getType().ordinal() + 1;
            }
            setBackground(new TextureRegionDrawable(TextureUtils.battleBackground(game, backgroundNr)));
            attackerRenders = attackers.stream().map(it -> (UnitRender) MapObjectRender.createRender(game, skin, it)).collect(Collectors.toList());
            defenderRenders = defenders.stream().map(it -> (UnitRender) MapObjectRender.createRender(game, skin, it)).collect(Collectors.toList());
            attackerRenders.forEach(it -> {
                it.setAlwaysDrawPlate(true);
                it.setDeathListener(() -> it.setVisible(false));
            });
            defenderRenders.forEach(it -> {
                it.setAlwaysDrawPlate(true);
                it.flip();
                it.setDeathListener(() -> it.setVisible(false));
            });
            this.rollRender = new BattleRollRender(game, skin);
        }

        public void attackRolls(int[] attackRolls, int[] defenderRolls, IRollAnimationListener listener) {
            rollRender.start(attackRolls, defenderRolls, listener);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            rollRender.update(delta);
        }

        @Override
        protected void drawChildren(Batch batch, float parentAlpha) {
            super.drawChildren(batch, parentAlpha);
            float lx = 150 + getX();
            float startY = getY() + getHeight() - 100;
            renderUnits(batch, attackerRenders, lx, startY, 175);
            float rx = getX() + getWidth() - 150;
            renderUnits(batch, defenderRenders, rx, startY, -175);
            float rollY = startY + 70;
            rollRender.render(batch, getX() + getWidth() / 2, rollY);
        }

        private void renderUnits(Batch batch, List<UnitRender> units, float x, float y, float dx) {
            int count = units.size();
            float deltaY = 650 / (float) count;
            if (count == 1) {
                y -= 330;
            }
            for (int i = 0; i < count; i++) {
                UnitRender unit = units.get(i);
                if (i % 2 == 0) {
                    unit.setPosition(x, y);
                } else {
                    unit.setPosition(x + dx, y);
                }
                y -= deltaY;
            }
            //draw reverse order
            for (int i = count - 1; i >= 0; i--) {
                UnitRender render = units.get(i);
                if (render.isVisible()) {
                    render.draw(batch, 1);
                }
            }
        }
    }
}
