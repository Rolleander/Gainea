package com.broll.gainea.client.ui.ingame.actions;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.game.GameStateListener;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.ui.utils.LabelUtils;

public class EndTurnButton extends Button implements GameStateListener {

    private PlayerPerformOptionalAction playerPerformOptionalAction;

    public EndTurnButton(Skin skin) {
        super(skin);
        this.add(LabelUtils.label(skin, "Zug beenden"));
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioPlayer.playSound("button.ogg");
                playerPerformOptionalAction.none();
                event.stop();
            }
        });
        setVisible(false);
    }

    @Override
    public void gameBusy() {
        setVisible(false);
    }

    @Override
    public void playerTurnIdle() {
        setVisible(true);
    }

    public void update(PlayerPerformOptionalAction playerPerformAction) {
        this.playerPerformOptionalAction = playerPerformAction;
    }
}
