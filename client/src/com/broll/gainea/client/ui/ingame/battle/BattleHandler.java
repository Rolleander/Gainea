package com.broll.gainea.client.ui.ingame.battle;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.ingame.map.MapAction;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.ingame.unit.MonsterRender;
import com.broll.gainea.client.ui.ingame.unit.UnitRender;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_Battle_Damage;
import com.broll.gainea.net.NT_Battle_Reaction;
import com.broll.gainea.net.NT_Battle_Roll;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
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

    private Sound rollSound;
    private boolean allowRetreat = false;

    public BattleHandler(Gainea game, Skin skin) {
        this.game = game;
        this.skin = skin;
    }

    public BattleBoard startBattle(List<NT_Unit> attackers, List<NT_Unit> defenders, Location location, boolean allowRetreat) {
        if (battleBoard != null) {
            battleBoard.remove();
        }
        this.allowRetreat = allowRetreat;
        this.attackers = attackers;
        this.defenders = defenders;
        battleBoard = new BattleBoard(location);
        battleBoard.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.3f)));
        return battleBoard;
    }

    public void updateBattle(NT_Battle_Roll[] attackRolls, NT_Battle_Roll[] defenderRolls, Stack<NT_Battle_Damage> damage, int state) {
        rollSound = AudioPlayer.loopSound("roll.ogg");
        battleBoard.attackRolls(attackRolls, defenderRolls, new IRollAnimationListener() {
            @Override
            public void diceSet() {
                AudioPlayer.playSound("roll_end.ogg");
                if (!damage.empty()) {
                    damage(damage.pop());
                }
            }

            @Override
            public void rollingDone() {
                AudioPlayer.playSound("roll_end.ogg");
                rollSound.stop();
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
                if (source instanceof MonsterRender) {
                    AudioPlayer.playSound("hit_monster.ogg");
                } else {
                    AudioPlayer.playSound("hit.ogg");
                }
                target.takeDamage(1);
                source.attack(target);
            }
        });
    }

    public void retreated() {
        battleEnd("Rückzug der Angreifer!");
    }

    private void checkBattleState(int state) {
        battleBoard.addAction(Actions.delay(0.3f, Actions.run(() -> {
            if (state == NT_Battle_Update.STATE_FIGHTING) {
                if (allowRetreat) {
                    Table table = new Table(skin);
                    MapAction action = new MapAction(game, 0, 0, () -> sendBattleResponse(table, false));
                    action.setRotation(90);
                    action.setVisible(true);
                    table.add(action).spaceRight(75);
                    action = new MapAction(game, 1, 0, () -> sendBattleResponse(table, true));
                    action.setRotation(-90);
                    action.setVisible(true);
                    table.add(action);
                    table.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.2f)));
                    table.pack();
                    battleBoard.add(table).center();
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
        Table overlay = new Table(skin);
        overlay.pad(15);
        overlay.setBackground("info-msg");
        overlay.add(LabelUtils.title(game.ui.skin, text));
        overlay.pack();
        overlay.addAction(
                Actions.sequence(
                        Actions.alpha(0),
                        Actions.fadeIn(0.2f),
                        Actions.delay(1.2f),
                        Actions.fadeOut(0.2f),
                        Actions.run(() -> {
                            clearBattleScreen();
                            game.state.turnIdle();
                        })
                ));
        battleBoard.add(overlay).center();
    }

    public void clearBattleScreen() {
        if (battleBoard != null) {
            battleBoard.addAction(Actions.sequence(Actions.fadeOut(0.3f), Actions.removeActor()));
        }
    }

    private void sendBattleResponse(Table dialog, boolean continueFight) {
        dialog.remove();
        NT_Battle_Reaction reaction = new NT_Battle_Reaction();
        reaction.keepAttacking = continueFight;
        game.client.getClient().sendTCP(reaction);
    }

    private class BattleBoard extends Table {
        private List<UnitRender> attackerRenders;
        private List<UnitRender> defenderRenders;
        private BattleRollRender rollRender;

        public BattleBoard(Location location) {
            setSize(1000, 800);
            TableUtils.consumeClicks(this);
            int backgroundNr = 0;
            if (location instanceof Area) {
                backgroundNr = ((Area) location).getType().ordinal() + 1;
            }
            setBackground(new TextureRegionDrawable(TextureUtils.battleBackground(game, backgroundNr)));
            attackerRenders = attackers.stream().map(it -> (UnitRender) MapObjectRender.createRender(game, skin, it)).sorted(Comparator.comparingInt(MapObjectRender::getRank)).collect(Collectors.toList());
            defenderRenders = defenders.stream().map(it -> (UnitRender) MapObjectRender.createRender(game, skin, it)).sorted(Comparator.comparingInt(MapObjectRender::getRank)).collect(Collectors.toList());
            attackerRenders.forEach(it -> {
                it.setAlwaysDrawPlate(true);
                it.showDescription = false;
                addActor(it);
            });
            defenderRenders.forEach(it -> {
                it.setAlwaysDrawPlate(true);
                it.showDescription = false;
                it.flip();
                addActor(it);
            });
            this.rollRender = new BattleRollRender(game, attackerRenders, defenderRenders, skin);
            addActor(this.rollRender);
            rollRender.setWidth(getWidth());
            rollRender.setHeight(getHeight());
            placeUnits();
        }

        public void attackRolls(NT_Battle_Roll[] attackRolls, NT_Battle_Roll[] defenderRolls, IRollAnimationListener listener) {
            placeUnits();
            rollRender.start(attackRolls, defenderRolls, listener);
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
    }
}
