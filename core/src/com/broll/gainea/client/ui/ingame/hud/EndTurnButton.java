package com.broll.gainea.client.ui.ingame.hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.GameStateListener;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;

public class EndTurnButton extends RoundImageButton implements GameStateListener {

    private PlayerPerformOptionalAction playerPerformOptionalAction;


    public EndTurnButton(Gainea game) {
        super(game.ui.skin, new TextureRegion(game.assets.get("textures/endturn.png", Texture.class)));
        setVisible(false);
        whenClicked(this::endTurn);
    }

    private void endTurn() {
        playerPerformOptionalAction.none();
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
