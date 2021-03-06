package com.broll.gainea.client.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.ui.utils.LabelUtils;

public class RoundInformation extends Table {


    private Label rounds, player;

    public RoundInformation(Skin skin) {
        rounds = LabelUtils.color(LabelUtils.info(skin, ""), Color.BLUE);
        player = LabelUtils.color(LabelUtils.info(skin, ""), Color.BLUE);
        add(LabelUtils.info(skin, "Runde:")).spaceRight(7);
        add(rounds).spaceRight(50);
        add(LabelUtils.info(skin, "Am Zug:")).spaceRight(7);
        add(player).spaceRight(50);
    }

    public void setRound(int round) {
        rounds.setText("" + round);
    }

    public void setPlayer(String player) {
        this.player.setText(player);
    }
}
