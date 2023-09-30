package com.broll.gainea.client.ui.ingame.hud;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.utils.LabelUtils;

public class RoundInformation extends Table {

    private Label roundLabel, currentPlayerLabel;

    private Gainea game;

    public RoundInformation(Gainea game) {
        this.game = game;
        setFillParent(true);
        bottom();
        right();
        roundLabel = LabelUtils.markup(game.ui.skin, "");
        currentPlayerLabel = LabelUtils.markup(game.ui.skin, "");
        Table content = new Table();
        content.pad(10);
        content.setSkin(game.ui.skin);
        content.setBackground("info-bg");
        content.defaults().space(10);
        content.add(currentPlayerLabel).padRight(40);
        content.add(roundLabel);
        update();
        add(content);
    }

    public void update() {
        int round = game.state.getRound();
        int turn = game.state.getTurn();
        int roundLimit = game.state.getRoundLimit();
        String roundEnd = "";
        if (roundLimit > 0) {
            roundEnd = " / " + roundLimit;
        }
        roundLabel.setText("[BLACK]Runde:[] [BLUE]" + round + roundEnd + "[]");
        if (turn > -1 && game.state.getPlayers() != null) {
            String name = game.state.getPlayers().get(turn).name;
            currentPlayerLabel.setText("[BLACK]Am Zug:[] [BLUE]" + name + "[]");
        }
    }
}
