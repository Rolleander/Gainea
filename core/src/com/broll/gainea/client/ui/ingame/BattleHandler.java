package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.render.MapObjectRender;
import com.broll.gainea.client.render.UnitRender;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.client.ui.elements.MapAction;
import com.broll.gainea.client.ui.elements.TableUtils;
import com.broll.gainea.client.ui.elements.TextureUtils;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.esotericsoftware.minlog.Log;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public BattleBoard startBattle(Table table, List<NT_Unit> attackers, List<NT_Unit> defenders, Location location) {
        if (battleBoard != null) {
            battleBoard.remove();
        }
        this.attackers = attackers;
        this.defenders = defenders;
        battleBoard = new BattleBoard(location);
        battleBoard.setSize(1000, 800);
        return battleBoard;
    }

    public void updateBattle(int[] attackRolls, int[] defenderRolls, List<Pair<NT_Unit, Integer>> damagedAttackers, List<Pair<NT_Unit, Integer>> damagedDefenders, int state) {
        battleBoard.attackRolls(attackRolls, defenderRolls);
    }

    private class BattleBoard extends Table {
        private List<UnitRender> attackerRenders;
        private List<UnitRender> defenderRenders;
        private int[] attackRolls;
        private int[] defendRolls;
        private int[] currentAttackRolls;
        private int[] currentDefendRolls;
        private int showRolls;
        private float rollAnimation;
        private float showAnimation;
        private Label numberLabel;
        private final static float ROLL_TIME = 0.05f;
        private final static float SHOW_NUMBER_TIME = 0.5f;
        private TextureRegion rollback, rollwin, rolllose;

        public BattleBoard(Location location) {
            super(skin);
            int rs = 20;
            Texture rolls = game.assets.get("textures/roll_back.png", Texture.class);
            rollback = new TextureRegion(rolls, 0 * rs, 0, rs, rs);
            rollwin = new TextureRegion(rolls, 1 * rs, 0, rs, rs);
            rolllose = new TextureRegion(rolls, 2 * rs, 0, rs, rs);
            numberLabel = LabelUtils.info(skin, "");
            TableUtils.consumeClicks(this);
            int backgroundNr = 0;
            if (location instanceof Area) {
                backgroundNr = ((Area) location).getType().ordinal() + 1;
            }
            setBackground(new TextureRegionDrawable(TextureUtils.battleBackground(game, backgroundNr)));
            attackerRenders = attackers.stream().map(it -> (UnitRender) MapObjectRender.createRender(game,skin, it)).collect(Collectors.toList());
            defenderRenders = defenders.stream().map(it -> (UnitRender) MapObjectRender.createRender(game,skin, it)).collect(Collectors.toList());
            attackerRenders.forEach(it -> it.setAlwaysDrawPlate(true));
            defenderRenders.forEach(it -> {
                it.setAlwaysDrawPlate(true);
                it.flip();
            });
        }

        public void attackRolls(int[] attackRolls, int[] defenderRolls) {
            this.attackRolls = attackRolls;
            this.defendRolls = defenderRolls;
            this.currentAttackRolls = new int[attackRolls.length];
            this.currentDefendRolls = new int[defenderRolls.length];
            this.showRolls = 0;
            rollAnimation = 0;
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (attackRolls != null) {
                rollAnimation += Gdx.graphics.getDeltaTime();
                if (rollAnimation >= ROLL_TIME) {
                    rollAnimation = 0;
                    currentAttackRolls = Arrays.stream(currentAttackRolls).map(it -> MathUtils.random(1, 6)).toArray();
                    currentDefendRolls = Arrays.stream(currentDefendRolls).map(it -> MathUtils.random(1, 6)).toArray();
                }
                showAnimation += Gdx.graphics.getDeltaTime();
                if (showAnimation >= SHOW_NUMBER_TIME) {
                    showAnimation = 0;
                    if (showRolls < Math.min(attackRolls.length, defendRolls.length)-1) {
                        showRolls++;
                    } else {
                        showRolls = Integer.MAX_VALUE;
                    }
                }
            }
        }

        @Override
        protected void drawChildren(Batch batch, float parentAlpha) {
            super.drawChildren(batch, parentAlpha);
            float lx = 150 + getX();
            float startY = getY() + getHeight() - 100;
            renderUnits(batch, attackerRenders, lx, startY, 175);
            float rx = getX() + getWidth() - 150;
            renderUnits(batch, defenderRenders, rx, startY, -175);
            float rollY = startY + 50;
            //numbers
            if (attackRolls != null) {
                int total = Math.max(attackRolls.length, defendRolls.length);
                Pair<int[], int[]> ar = calc(currentAttackRolls, attackRolls, showRolls, defendRolls, false);
                Pair<int[], int[]> dr = calc(currentDefendRolls, defendRolls, showRolls, attackRolls, true);
                renderRolls(batch, ar.getLeft(), ar.getRight(), total, lx + 150 + 175, rollY);
                renderRolls(batch, dr.getLeft(), dr.getRight(), total, rx - 150 - 175, rollY);
            }
        }

        private Pair<int[], int[]> calc(int[] rollCurrent, int[] roll, int showRolls, int[] opposingRolls, boolean defender) {
            int[] displayRoll = new int[roll.length];
            int[] stateRoll = new int[roll.length];
            for (int i = 0; i < roll.length; i++) {
                displayRoll[i] = rollCurrent[i];
                if (i <showRolls) {
                    displayRoll[i] = roll[i];
                    if (i < opposingRolls.length) {
                        int diff = roll[i] - opposingRolls[i];
                        if (diff > 0 || diff >= 0 && defender) {
                            stateRoll[i] = 1;
                        } else {
                            stateRoll[i] = 2;
                        }
                    }
                }
            }
            return Pair.of(displayRoll, stateRoll);
        }

        private void showRoll(boolean[] showRolls) {
            int hidden = 0;
            for (boolean shown : showRolls) {
                if (!shown) {
                    hidden++;
                }
            }
            if (hidden > 0) {
                int unhide = MathUtils.random(0, hidden);
                int hc = 0;
                for (int i = 0; i < showRolls.length; i++) {
                    if (!showRolls[i]) {
                        if (hc == unhide) {
                            showRolls[i] = true;
                            return;
                        }
                        hc++;
                    }
                }
            }
        }

        private void renderRolls(Batch batch, int[] rolls, int[] state, int total, float x, float y) {
            float deltaY = 700 / (float) total;
            for (int i = 0; i < rolls.length; i++) {
                TextureRegion back = rollback;
                if (state[i] == 1) {
                    back = rollwin;
                } else if (state[i] == 2) {
                    back = rolllose;
                }
                batch.draw(back, x - 6, y - 10);
                numberLabel.setText("" + rolls[i]);
                numberLabel.setPosition(x, y);
                numberLabel.draw(batch, 1);
                y -= deltaY;
            }
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
                units.get(i).draw(batch, 1);
            }
        }
    }
}
