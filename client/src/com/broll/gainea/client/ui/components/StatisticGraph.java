package com.broll.gainea.client.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.utils.LabelUtils;

public class StatisticGraph extends Table {


    private Gainea game;
    private int[][] data;

    private int dataPoints;
    private int maxTotal;
    private int total[];

    public final static Color[] PLAYER_COLORS = new Color[]{
            Color.valueOf("ea0000"),
            Color.valueOf("001cea"),
            Color.valueOf("00ff06)"),
            Color.valueOf("ead100"),
            Color.valueOf("cd00ec"),
            Color.valueOf("00ecea"),
            Color.valueOf("eb5300"),
            Color.valueOf("f8f8f8"),
            Color.valueOf("525252"),
    };


    public StatisticGraph(Gainea game, int[][] data) {
        super(game.ui.skin);
        this.game = game;
        this.data = data;
        dataPoints = data[0].length;
        total = new int[dataPoints];
        for (int i = 0; i < dataPoints; i++) {
            int sum = 0;
            for (int h = 0; h < data.length; h++) {
                sum += data[h][i];
            }
            total[i] = Math.max(1, sum);
            maxTotal = Math.max(maxTotal, sum);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.end();
        float b = (getWidth()) / (float) dataPoints;
        float vh = (getHeight()) / (float) maxTotal;
        game.uiShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.uiShapeRenderer.setColor(Color.BLACK);
        game.uiShapeRenderer.rect(getX()-2, getY()-2, getWidth()+4, getHeight()+4);
        float sx = getX();
        float sy = getY();
        float xp = sx;
        for (int x = 0; x < dataPoints; x++) {
            float yp = sy;
            vh = (getHeight()) / (float) total[x];
            for (int i = 0; i < data.length; i++) {
                //draw bar
                float v = ((float) data[i][x]) * vh;
                if (v > 0) {
                    game.uiShapeRenderer.setColor(PLAYER_COLORS[i]);
                    game.uiShapeRenderer.rect(xp, yp, b, v);
                }
                yp += v;
            }
            xp += b;
        }
        game.uiShapeRenderer.end();
        batch.begin();
    }

    public static class PlayerLegend extends Widget {
        private Gainea game;
        private Label label;
        private String[] names;

        public PlayerLegend(Gainea game, String[] names) {
            this.game = game;
            this.names = names;
            label = LabelUtils.info(game.ui.skin, "");
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            batch.end();
            float sx = getX();
            float sy = getY();
            float lb = getWidth() / (float) names.length;
            game.uiShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < names.length; i++) {
                game.uiShapeRenderer.setColor(Color.BLACK);
                game.uiShapeRenderer.circle(sx + i * lb + 10, sy+10 , 12);
                game.uiShapeRenderer.setColor(PLAYER_COLORS[i]);
                game.uiShapeRenderer.circle(sx + i * lb + 10, sy+10 , 10);
            }
            game.uiShapeRenderer.end();
            batch.begin();
            for (int i = 0; i < names.length; i++) {
                label.setText(names[i]);
                label.setPosition(sx + i * lb + 30, sy +10);
                label.draw(batch, parentAlpha);
            }
        }
    }

}
