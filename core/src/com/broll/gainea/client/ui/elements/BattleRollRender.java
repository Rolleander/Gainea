package com.broll.gainea.client.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

public class BattleRollRender {

    private Rolls attacker;
    private Rolls defender;
    private int showRolls = 0;
    private float rollAnimation;
    private float showAnimation;
    private Label numberLabel;
    private final static float ROLL_TIME = 0.05f;
    private final static float SHOW_NUMBER_TIME = 1.5f;
    private TextureRegion rollback, rollwin, rolllose;
    private IRollAnimationListener listener;
    private Sound rollSound;

    public BattleRollRender(Gainea game, Skin skin) {
        int rs = 20;
        Texture rolls = game.assets.get("textures/roll_back.png", Texture.class);
        rollback = new TextureRegion(rolls, 0 * rs, 0, rs, rs);
        rollwin = new TextureRegion(rolls, 1 * rs, 0, rs, rs);
        rolllose = new TextureRegion(rolls, 2 * rs, 0, rs, rs);
        numberLabel = LabelUtils.info(skin, "");
        rollSound = game.assets.get("sounds/roll.ogg", Sound.class);
    }

    public void start(int[] attackRolls, int[] defenderRolls, IRollAnimationListener listener) {
        this.attacker = new Rolls(attackRolls);
        this.defender = new Rolls(defenderRolls);
        this.listener = listener;
        showRolls = 0;
        rollAnimation = 0;
        rollSound.play();
    }

    public void update(float delta) {
        if (attacker != null && defender != null) {
            rollAnimation += delta;
            if (rollAnimation >= ROLL_TIME) {
                rollAnimation = 0;
                attacker.roll();
                defender.roll();
            }
            showAnimation += delta;
            if (showAnimation >= SHOW_NUMBER_TIME) {
                showAnimation = 0;
                if (showRolls < Math.min(attacker.rolls.length, defender.rolls.length)) {
                    //update state
                    boolean attackerWon = attacker.updateState(showRolls, defender, false);
                    defender.updateState(showRolls, attacker, true);
                    showRolls++;
                    if (showRolls >= Math.min(attacker.rolls.length, defender.rolls.length)) {
                        showRolls = Integer.MAX_VALUE;
                        listener.rollingDone();
                    } else {
                        rollSound.play();
                        listener.diceSet(attackerWon);
                    }
                }
            }
        }
    }

    public void render(Batch batch, float x, float y) {
        if (attacker != null && defender != null) {
            attacker.render(batch, defender, x - 50, y);
            defender.render(batch, attacker, x + 50, y);
        }
    }

    private class Rolls {
        private int[] rolls;
        private int[] currentRolls;
        private int[] state;

        private Rolls(int[] rolls) {
            this.rolls = rolls;
            this.currentRolls = new int[rolls.length];
            this.state = new int[rolls.length];
        }

        private boolean updateState(int nr, Rolls opposingRolls, boolean isDefender) {
            if (nr < rolls.length && nr < opposingRolls.rolls.length) {
                if (rolls[nr] > opposingRolls.rolls[nr] || rolls[nr] >= opposingRolls.rolls[nr] && isDefender) {
                    state[nr] = 1;
                    return true;
                } else {
                    state[nr] = 2;
                    return false;
                }
            }
            return false;
        }

        private void roll() {
            this.currentRolls = Arrays.stream(currentRolls).map(it -> MathUtils.random(1, 6)).toArray();
        }

        private void render(Batch batch, Rolls opposingRolls, float x, float y) {
            int total = Math.max(this.rolls.length, opposingRolls.rolls.length);
            float deltaY = 760 / (float) total;
            for (int i = 0; i < rolls.length; i++) {
                TextureRegion back = rollback;
                int roll = currentRolls[i];
                if (i < showRolls) {
                    roll = rolls[i];
                }
                if (state[i] == 1) {
                    back = rollwin;
                } else if (state[i] == 2) {
                    back = rolllose;
                }
                batch.draw(back, x - 7, y - 10);
                numberLabel.setText("" + roll);
                numberLabel.setPosition(x, y);
                numberLabel.draw(batch, 1);
                y -= deltaY;
            }
        }
    }

}
