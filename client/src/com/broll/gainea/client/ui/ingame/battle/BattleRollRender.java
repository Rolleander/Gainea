package com.broll.gainea.client.ui.ingame.battle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.ingame.unit.UnitRender;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.net.NT_Battle_Roll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BattleRollRender extends WidgetGroup {


    private final static Logger Log = LoggerFactory.getLogger(BattleRollRender.class);
    private final static float ROLL_TIME = 0.05f;
    private final static float SHOW_NUMBER_TIME = 0.6f;
    private final List<UnitRender> attackerRenders;
    private final List<UnitRender> defenderRenders;
    private final TextureRegion rollback, rollwin, rolllose;
    private Gainea game;
    private List<BattleRoll> attackerRolls;
    private List<BattleRoll> defenderRolls;
    private int showRolls = 0;
    private float rollAnimation;
    private float showAnimation;
    private IRollAnimationListener listener;

    private int rollRounds;


    public BattleRollRender(Gainea game, List<UnitRender> attackerRenders, List<UnitRender> defenderRenders, Skin skin) {
        this.game = game;
        int rs = 20;
        this.attackerRenders = attackerRenders;
        this.defenderRenders = defenderRenders;
        Texture rolls = game.assets.get("textures/roll_back.png", Texture.class);
        rollback = new TextureRegion(rolls, 0, 0, rs, rs);
        rollwin = new TextureRegion(rolls, rs, 0, rs, rs);
        rolllose = new TextureRegion(rolls, 2 * rs, 0, rs, rs);
    }

    public void start(NT_Battle_Roll[] attackRolls, NT_Battle_Roll[] defenderRolls, IRollAnimationListener listener) {
        this.listener = listener;
        if (this.attackerRolls != null) {
            this.attackerRolls.forEach(BattleRoll::hide);
        }
        if (this.defenderRolls != null) {
            this.defenderRolls.forEach(BattleRoll::hide);
        }
        this.attackerRolls = Arrays.stream(attackRolls).map(it -> new BattleRoll(it, true)).collect(Collectors.toList());
        this.defenderRolls = Arrays.stream(defenderRolls).map(it -> new BattleRoll(it, false)).collect(Collectors.toList());
        this.attackerRolls.forEach(this::addActor);
        this.defenderRolls.forEach(this::addActor);
        this.rollRounds = Math.min(attackerRolls.size(), this.defenderRolls.size());
        this.placeInCircle(this.attackerRolls);
        this.placeInCircle(this.defenderRolls);
        this.attackerRolls.forEach(BattleRoll::showBuffs);
        this.defenderRolls.forEach(BattleRoll::showBuffs);
        showRolls = 0;
        rollAnimation = 0;
        showAnimation = 0;
        AudioPlayer.playSound("roll.ogg");
    }

    private void placeInCircle(List<BattleRoll> rolls) {
        for (int sourceUnit : rolls.stream().map(it -> it.nt.sourceUnit).distinct().collect(Collectors.toList())) {
            List<BattleRoll> unitRolls = rolls.stream().filter(it -> it.nt.sourceUnit == sourceUnit).collect(Collectors.toList());
            int total = unitRolls.size();
            if (total == 1) {
                continue;
            }
            for (int i = 0; i < total; i++) {
                unitRolls.get(i).placeInCircle(i + 1, total);
            }
        }
    }

    private void rollSet() {
        if (showRolls <= rollRounds) {
            if (showRolls < rollRounds) {
                attackerRolls.get(showRolls).move(showRolls);
                defenderRolls.get(showRolls).move(showRolls);
            }
            if (showRolls > 0) {
                finishRollRound(showRolls - 1);
            }
            showRolls++;
            if (showRolls > rollRounds) {
                showRolls = Integer.MAX_VALUE;
                attackerRolls.forEach(it -> it.currentRoll = it.nt.number);
                defenderRolls.forEach(it -> it.currentRoll = it.nt.number);
                listener.rollingDone();
            } else if (showRolls > 1) {
                AudioPlayer.playSound("roll.ogg");
                listener.diceSet();
            }
        }
    }

    private void finishRollRound(int nr) {
        BattleRoll aroll = attackerRolls.get(nr);
        BattleRoll droll = defenderRolls.get(nr);
        boolean awon = aroll.nt.number > droll.nt.number;
        aroll.finish(awon);
        droll.finish(!awon);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (attackerRolls != null && defenderRolls != null && listener != null) {
            rollAnimation += delta;
            if (rollAnimation >= ROLL_TIME && (showRolls <= rollRounds + 1)) {
                rollAnimation = 0;
                attackerRolls.forEach(BattleRoll::roll);
                defenderRolls.forEach(BattleRoll::roll);
            }
            showAnimation += delta;
            if (showAnimation >= SHOW_NUMBER_TIME) {
                showAnimation = 0;
                rollSet();
            }
        }
    }

    private enum RollState {
        ROLLING, WON, LOSS
    }

    private class BattleRoll extends Actor {

        private final static float BUFF_DURATION = 3f;
        private final static int MIDDLE_DISTANCE = 30;
        private final static int CIRCLE_DISTANCE = 25;

        private NT_Battle_Roll nt;
        private int currentRoll = 1;
        private RollState state = RollState.ROLLING;

        private boolean attacker;

        private Label numberLabel = LabelUtils.info(game.ui.skin, "");


        public BattleRoll(NT_Battle_Roll nt, boolean attacker) {
            this.nt = nt;
            this.attacker = attacker;
            this.place();
        }

        public void showBuffs() {
            if (nt.plus != 0) {
                String t;
                Color c;
                if (nt.plus > 0) {
                    t = "+" + nt.plus;
                    c = Color.GREEN;
                } else {
                    t = String.valueOf(nt.plus);
                    c = Color.RED;
                }
                Label label = LabelUtils.color(LabelUtils.label(game.ui.skin, t), c);
                label.setFontScale(0.6f);
                label.setColor(c);
                label.pack();
                addActor(label);
                label.setPosition(getX() - label.getWidth() / 2, getY());
                label.addAction(Actions.sequence(Actions.parallel(
                        Actions.moveBy(0, 30, BUFF_DURATION),
                        Actions.alpha(0, BUFF_DURATION)
                ), Actions.removeActor()));
            }
        }

        public void roll() {
            if (state == RollState.ROLLING) {
                this.currentRoll = MathUtils.random(nt.min, nt.max);
            }
        }

        public void finish(boolean won) {
            this.currentRoll = nt.number;
            this.state = won ? RollState.WON : RollState.LOSS;
        }

        public void move(int nr) {
            float y = BattleRollRender.this.getY() + BattleRollRender.this.getHeight() - 30;
            int total = Math.max(attackerRolls.size(), defenderRolls.size());
            float deltaY = 760 / (float) total;
            y -= deltaY * nr;
            float x = BattleRollRender.this.getX() + BattleRollRender.this.getWidth() / 2;
            if (attacker) {
                x -= MIDDLE_DISTANCE;
            } else {
                x += MIDDLE_DISTANCE;
            }
            addAction(Actions.moveTo(x, y, 0.7f, Interpolation.pow2Out));
        }

        private void place() {
            List<UnitRender> source = defenderRenders;
            if (attacker) {
                source = attackerRenders;
            }
            UnitRender unit = source.stream().filter(it -> it.getUnit().id == nt.sourceUnit).findFirst().get();
            setPosition(unit.getX() - 4, unit.getY());
        }

        public void placeInCircle(int nr, int total) {
            float angle = ((float) nr / total) * MathUtils.PI2;
            this.setX(getX() + MathUtils.cos(angle) * CIRCLE_DISTANCE);
            this.setY(getY() + MathUtils.sin(angle) * CIRCLE_DISTANCE);
        }

        public void hide() {
            addAction(Actions.sequence(Actions.alpha(0, 0.5f),
                    Actions.removeActor()));
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            TextureRegion back = rollback;
            if (state == RollState.WON) {
                back = rollwin;
            } else if (state == RollState.LOSS) {
                back = rolllose;
            }
            batch.setColor(1, 1, 1, parentAlpha);
            batch.draw(back, getX() - 6, getY() - 10);
            numberLabel.setText(String.valueOf(currentRoll));
            numberLabel.setPosition(getX(), getY());
            numberLabel.draw(batch, parentAlpha);
        }

    }

}
