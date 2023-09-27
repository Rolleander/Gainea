package com.broll.gainea.client.ui.ingame.battle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.components.Popup;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.ingame.unit.UnitRender;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_Battle_Damage;
import com.broll.gainea.net.NT_Battle_Reaction;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.networklib.server.impl.ConnectionSite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BattleHandler {

    private final static Logger Log = LoggerFactory.getLogger(BattleHandler.class);
    private Gainea game;
    private Skin skin;
    private BattleBoard battleBoard;
    private List<NT_Unit> attackers;
    private List<NT_Unit> defenders;
    private boolean allowRetreat = false;

    public BattleHandler(Gainea game, Skin skin) {
        this.game = game;
        this.skin = skin;
    }

    public BattleBoard startBattle(List<NT_Unit> attackers, List<NT_Unit> defenders, Location location, boolean allowRetreat) {
        if (battleBoard != null) {
            battleBoard.remove();
        }
        AudioPlayer.playSound("battle.ogg");
        this.allowRetreat = allowRetreat;
        this.attackers = attackers;
        this.defenders = defenders;
        battleBoard = new BattleBoard(location);
        battleBoard.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.3f)));
        return battleBoard;
    }

    public void updateBattle(int[] attackRolls, int[] defenderRolls, Stack<NT_Battle_Damage> damage, int state) {
        battleBoard.attackRolls(attackRolls, defenderRolls, new IRollAnimationListener() {
            @Override
            public void diceSet(boolean attackerWon) {
                if (!damage.empty()) {
                    damage(damage.pop());
                }
            }

            @Override
            public void rollingDone() {
                while (!damage.empty()) {
                    damage(damage.pop());
                }
                removeDeadUnits();
                checkBattleState(state);
            }

            private void removeDeadUnits() {
                battleBoard.attackerRenders.forEach(UnitRender::removeIfDead);
                battleBoard.defenderRenders.forEach(UnitRender::removeIfDead);
            }

            private UnitRender getUnit(int id) {
                return Stream.concat(battleBoard.attackerRenders.stream(), battleBoard.defenderRenders.stream())
                        .filter(it -> it.getObject().id == id).findFirst().orElse(null);
            }

            private void damage(NT_Battle_Damage damage) {
                UnitRender source = getUnit(damage.source);
                UnitRender target = getUnit(damage.target);
                target.takeDamage(1);
                source.attack(target);
            }
        });
    }

    private void checkBattleState(int state) {
        battleBoard.addAction(Actions.delay(2, Actions.run(() -> {
            if (state == NT_Battle_Update.STATE_FIGHTING) {
                if (allowRetreat) {
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
                String text = "Keine Überlebenden!";
                if (state == NT_Battle_Update.STATE_ATTACKER_WON) {
                    text = "Sieg der Angreifer!";
                }
                if (state == NT_Battle_Update.STATE_DEFENDER_WON) {
                    text = "Sieg der Verteidiger!";
                }
                battleEnd(text);
            }
        })));
    }

    private void battleEnd(String text) {
        Popup popup = new Popup(skin, LabelUtils.title(game.ui.skin, text));
        popup.addAction(Actions.delay(com.broll.gainea.server.core.battle.BattleHandler.BATTLE_ANIMATION_DELAY, Actions.run(() -> {
            clearBattleScreen();
            game.state.turnIdle();
        })));
        battleBoard.add(popup).padTop(100).top();
    }

    public void clearBattleScreen() {
        if (battleBoard != null) {
            battleBoard.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.removeActor()));
        }
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
        private boolean place = true;

        public BattleBoard(Location location) {
            super(skin);
            setSize(1000, 800);
            TableUtils.consumeClicks(this);
            int backgroundNr = 0;
            if (location instanceof Area) {
                backgroundNr = ((Area) location).type.ordinal() + 1;
            }
            setBackground(new TextureRegionDrawable(TextureUtils.battleBackground(game, backgroundNr)));
            attackerRenders = attackers.stream().map(it -> (UnitRender) MapObjectRender.createRender(game, skin, it)).sorted((a, b) -> Integer.compare(a.getRank(), b.getRank())).collect(Collectors.toList());
            defenderRenders = defenders.stream().map(it -> (UnitRender) MapObjectRender.createRender(game, skin, it)).sorted((a, b) -> Integer.compare(a.getRank(), b.getRank())).collect(Collectors.toList());
            attackerRenders.forEach(it -> {
                it.setAlwaysDrawPlate(true);
            });
            defenderRenders.forEach(it -> {
                it.setAlwaysDrawPlate(true);
                it.flip();
            });
            this.rollRender = new BattleRollRender(game, skin);
            placeUnits();
        }

        public void attackRolls(int[] attackRolls, int[] defenderRolls, IRollAnimationListener listener) {
            place = true;
            rollRender.start(attackRolls, defenderRolls, listener);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (place) {
                placeUnits();
                place = false;
            }
            rollRender.update(delta);
            attackerRenders.forEach(it -> it.act(delta));
            defenderRenders.forEach(it -> it.act(delta));
        }

        private void placeUnits() {
            float lx = 150;
            float startY = +getHeight() - 100;
            placeUnits(attackerRenders, lx, startY, 175);
            float rx = +getWidth() - 150;
            placeUnits(defenderRenders, rx, startY, -175);
        }

        private void placeUnits(List<UnitRender> units, float x, float y, float dx) {
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
        }

        @Override
        protected void drawChildren(Batch batch, float parentAlpha) {
            float startY = getY() + getHeight() - 100;
            renderUnits(batch, attackerRenders);
            renderUnits(batch, defenderRenders);
            float rollY = startY + 70;
            rollRender.render(batch, getX() + getWidth() / 2, rollY, parentAlpha);
            super.drawChildren(batch, parentAlpha);
        }

        private void renderUnits(Batch batch, List<UnitRender> units) {
            int count = units.size();
            //draw reverse order
            for (int i = count - 1; i >= 0; i--) {
                UnitRender render = units.get(i);
                float x = render.getX();
                float y = render.getY();
                render.setX(x + getX());
                render.setY(y + getY());
                render.draw(batch, 1);
                render.setX(x);
                render.setY(y);
            }
        }
    }
}
