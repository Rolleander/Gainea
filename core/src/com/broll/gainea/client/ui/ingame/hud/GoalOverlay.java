package com.broll.gainea.client.ui.ingame.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.server.core.map.Location;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GoalOverlay extends Table {

    private Table content = new Table();
    private Gainea game;

    private List<TargetIndicator> indicators;
    private NT_Goal selectedGoal;

    public GoalOverlay(Gainea game) {
        this.game = game;
        setFillParent(true);
        top();
        left();
        add(content);
        content.pad(10);
        content.defaults().space(10);
        update();
    }

    public void update() {
        content.clear();
        if (selectedGoal != null) {
            indicators.forEach(it -> it.remove());
            selectedGoal = null;
        }
        game.state.getGoals().forEach(goal -> {
            Table goalRender = renderGoal(game, goal);
            addClickIndicator(goalRender, goal);
            content.add(goalRender).left().row();
        });
    }

    private void addClickIndicator(Table render, NT_Goal goal) {
        if (goal.locations != null) {
            render.setTouchable(Touchable.enabled);
            render.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    event.stop();
                    if (indicators != null) {
                        indicators.forEach(it -> it.remove());
                    }
                    content.getChildren().forEach(child -> {
                        Table table = (Table) child;
                        table.setBackground("info-bg");
                    });
                    if (selectedGoal != goal) {
                        selectedGoal = goal;
                        render.setBackground("menu-bg");
                        indicators = Arrays.stream(goal.locations).mapToObj(TargetIndicator::new).collect(Collectors.toList());
                        indicators.forEach(it -> game.gameStage.addActor(it));
                    } else {
                        selectedGoal = null;
                    }
                }
            });
        }
    }

    public static Table renderGoal(Gainea game, NT_Goal goal) {
        Skin skin = game.ui.skin;
        Table table = new Table(skin);
        int w = 500;
        table.left();
        table.setBackground("info-bg");
        table.add(new Image(TextureUtils.icon(game, 8 + goal.points)));
        String info = "";
        if (goal.progressionGoal != NT_Goal.NO_PROGRESSION_GOAL) {
            info = "   [BLUE](" + goal.progression + "/" + goal.progressionGoal + ")[]";
        }
        if (StringUtils.isNotEmpty(goal.restriction)) {
            info += "   [BROWN]" + goal.restriction + "[]";
        }
        table.add(LabelUtils.autoWrap(LabelUtils.markup(skin, goal.description + info), w)).width(w);
        return table;
    }

    private class TargetIndicator extends Actor {

        private final static int SIZE = 128;
        private TextureRegion texture;

        private float animationTime;

        private int locationNumber;

        private TargetIndicator(int locationNumber) {
            this.locationNumber = locationNumber;
            texture = new TextureRegion(game.assets.get("textures/indicator.png", Texture.class));
            Location location = game.state.getMap().getLocation(locationNumber);
            setVisible(true);
            setSize(SIZE, SIZE);
            setX(location.coordinates.getDisplayX());
            setY(location.coordinates.getDisplayY());
            setZIndex(1000);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            animationTime += Gdx.graphics.getDeltaTime();
            Color color = getColor();
            setRotation((animationTime + locationNumber) * 100);
            setScale((float) (1.25f + Math.sin(animationTime) * 0.25f));
            color.a = (float) (0.75f + Math.cos(animationTime) * 0.25f);
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            float xc = texture.getRegionWidth() / 2;
            float yc = texture.getRegionHeight() / 2;
            batch.draw(texture, getX() - xc, getY() - yc, xc, yc, xc * 2, yc * 2, getScaleX(), getScaleY(), getRotation());
            batch.setColor(color.r, color.g, color.b, 1f);
        }
    }
}
