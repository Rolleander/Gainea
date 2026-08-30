package com.broll.gainea.client.ui.ingame.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.GoalState;
import com.broll.gainea.client.ui.ingame.DepthActor;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.proto.NT_Goal;
import com.broll.gainea.client.game.map.Location;

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

    public static Table renderGoal(Gainea game, NT_Goal goal) {
        Skin skin = game.ui.skin;
        Table table = new Table(skin);
        int w = 500;
        table.left();
        table.setBackground("info-bg");
        table.add(new Image(TextureUtils.icon(game, 8 + goal.getPoints())));
        String info = "";
        if (goal.getProgressionGoal() != -1) {
            info = "   [BLUE](" + goal.getProgression() + "/" + goal.getProgressionGoal() + ")[]";
        }
        if (!goal.getRestriction().isEmpty()) {
            info += "   [BROWN]" + goal.getRestriction() + "[]";
        }
        table.add(LabelUtils.autoWrap(LabelUtils.markup(skin, goal.getDescription() + info), w)).width(w);
        return table;
    }

    public void update() {
        content.clear();
        if (selectedGoal != null) {
            indicators.forEach(it -> it.remove());
            selectedGoal = null;
        }
        game.state.getGoals().forEach(goal -> {
            Table goalRender = renderGoal(game, goal.message());
            addClickIndicator(goalRender, goal.message());
            content.add(goalRender).left().row();
        });
    }

    private void addClickIndicator(Table render, NT_Goal goal) {
        if (goal.getLocationsCount() > 0) {
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
                        indicators = goal.getLocationsList().stream().map(TargetIndicator::new).collect(Collectors.toList());
                        indicators.forEach(it -> game.gameStage.addActor(it));
                    } else {
                        selectedGoal = null;
                    }
                }
            });
        }
    }

    private class TargetIndicator extends DepthActor {

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
            setX(location.getCoordinates().getDisplayX());
            setY(location.getCoordinates().getDisplayY());
            depth = 200;
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
