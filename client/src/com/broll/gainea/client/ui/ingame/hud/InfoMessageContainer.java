package com.broll.gainea.client.ui.ingame.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.ingame.windows.CardWindow;
import com.broll.gainea.net.NT_Card;

public class InfoMessageContainer extends WidgetGroup {

    private final static float DURATION = 2.5f;
    private final static float FADE_OUT = 2f;
    private final Gainea game;
    private int stackHeight = 0;

    private int count = 0;

    public InfoMessageContainer(Gainea game) {
        this.game = game;
    }

    public void show(String test) {
        InfoMessage msg = new InfoMessage(game, test);
        spawnMessage(msg);
    }

    public void show(Table table, float duration) {
        InfoMessage msg = new InfoMessage(game, table);
        spawnMessage(msg, duration);
    }

    public void showCardReceived(NT_Card card) {
        InfoMessage msg = new InfoMessage(game, "'" + card.title + "' erhalten",
                CardWindow.previewCard(game, card), Color.CYAN.cpy().mul(0.4f, 0.4f, 0.4f, 1));
        spawnMessage(msg);
    }

    private void spawnMessage(InfoMessage msg) {
        spawnMessage(msg, DURATION);
    }

    private void spawnMessage(InfoMessage msg, float duration) {
        msg.index = count;
        count++;
        AudioPlayer.playSound("infomsg.ogg");
        msg.pack();
        addActor(msg);
        msg.toFront();
        stackHeight += msg.getHeight();
        msg.setPosition(msg.getX() - msg.getWidth() / 2, msg.getY() - 70 - stackHeight);
        stackHeight += +15;
        msg.addAction(Actions.delay(duration, Actions.moveBy(0, 50, FADE_OUT, Interpolation.sineIn)));
        msg.addAction(Actions.delay(duration, Actions.sequence(Actions.fadeOut(FADE_OUT, Interpolation.exp5In),
                Actions.run(() -> {
                    if (msg.index == 0) {
                        stackHeight = 0;
                        count = 0;
                    }
                }), Actions.removeActor())));
    }

}
