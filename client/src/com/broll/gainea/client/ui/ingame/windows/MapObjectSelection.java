package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.components.IconLabel;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.ingame.unit.MenuUnit;
import com.broll.gainea.client.ui.ingame.unit.UnitRender;
import com.broll.gainea.client.ui.utils.ActionListener;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaCollection;
import com.broll.gainea.server.core.map.Continent;
import com.broll.gainea.server.core.map.Island;
import com.broll.gainea.server.core.map.Location;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MapObjectSelection extends Table {

    private Table view;
    private Gainea game;

    private List<ObjectPreview> objects;

    private boolean selectable = false;

    public MapObjectSelection(Gainea game, Location location, Collection<MapObjectRender> stack) {
        super(game.ui.skin);
        this.game = game;
        pad(3);
        padTop(10);
        defaults().pad(5);
        setBackground("menu-bg");
        top();


        add(LabelUtils.label(game.ui.skin, location.toString()));
        if (location instanceof Area) {
            AreaCollection container = location.container;
            String name = container.getName();
            if (container instanceof Island) {
                name = "Insel: " + name;
            } else if (container instanceof Continent) {
                name = "Kontinent: " + name;
            }
            add(LabelUtils.info(game.ui.skin, name)).spaceLeft(20).row();
        }
        row();
        selectable = stack.stream().anyMatch(it -> it.getObject() instanceof NT_Unit && ((NT_Unit) it.getObject()).owner == game.state.getPlayer().getId());

        objects = stack.stream().sorted(Comparator.comparingInt(MapObjectRender::getRank)).map(obj ->
                {
                    ObjectPreview prev = new ObjectPreview(game, obj.getObject(), () -> updateView(obj.getObject()));
                    if (obj instanceof UnitRender) {
                        prev.setSelected(((UnitRender) obj).isActionActive());
                    }
                    if (selectable) {
                        prev.selectable(this::updateSelection);
                    }
                    return prev;
                }
        ).collect(Collectors.toList());
        Collections.reverse(objects);


        if (objects.size() > 1) {
            Table previews = new Table(game.ui.skin);
            previews.defaults().pad(4).left();

            int columns = objects.size() > 21 ? 4 : 3;
            for (int i = 0; i < objects.size(); i++) {
                previews.add(objects.get(i));
                if (i % columns == columns - 1) {
                    previews.row();
                }
            }
            add(previews).colspan(2).row();
        }

        this.view = new Table(game.ui.skin);
        add(view).colspan(2);
        updateView(objects.get(0).obj);
        TableUtils.consumeClicks(this);
        updateSelection();
    }

    private void updateSelection() {
        game.ui.inGameUI.selectedUnits(objects.stream().filter(it -> it.selectionImage.selected &&
                it.obj instanceof NT_Unit).map(it -> (NT_Unit) it.obj).collect(Collectors.toList()));
    }

    private void updateView(NT_BoardObject object) {
        MenuUnit unit = new MenuUnit(game, getSkin(), object);
        view.clear();
        if (objects.size() > 1) {
            Table table = new Table(getSkin());
            List<NT_Unit> units = objects.stream().filter(it -> it.selectionImage.selected || !selectable).map(it -> it.obj)
                    .filter(it -> it instanceof NT_Unit).map(it -> (NT_Unit) it).collect(Collectors.toList());
            int power = units.stream().map(u -> (int) u.power).reduce(0, Integer::sum);
            int health = units.stream().map(u -> (int) u.health).reduce(0, Integer::sum);
            int maxHealth = units.stream().map(u -> (int) u.maxHealth).reduce(0, Integer::sum);
            table.defaults().left().pad(5).padLeft(10).padRight(10);
            table.add(new Label(units.size() + " Einheiten mit", getSkin()));
            table.add(IconLabel.attack(game, power));
            table.add(IconLabel.health(game, health, maxHealth));
            view.add(table).center().row();
        }
        view.add(unit).spaceTop(10);
    }

    private static class ObjectPreview extends Table {

        private SelectionImage selectionImage;
        private NT_BoardObject obj;

        private ObjectPreview(Gainea game, NT_BoardObject obj, ActionListener onHover) {
            super(game.ui.skin);
            this.obj = obj;
            TextureRegion icon = TextureUtils.unitIcon(game, obj.icon);
            selectionImage = new SelectionImage(game, icon);
            add(selectionImage).spaceRight(5);
            if (obj instanceof NT_Unit) {
                NT_Unit unit = (NT_Unit) obj;
                Table info = new Table();
                info.defaults().space(5).left();
                info.add(IconLabel.attack(game, unit.power)).row();
                info.add(IconLabel.health(game, unit.health, unit.maxHealth));
                add(info).center();
            }
            TableUtils.onHoverOrClick(this, onHover);
        }

        public void selectable(ActionListener onSelect) {
            TableUtils.onClick(this, () -> {
                        setSelected(!selectionImage.selected);
                        onSelect.action();
                    }
            );
        }

        public void setSelected(boolean selected) {
            selectionImage.selected = selected;
        }
    }

    private static class SelectionImage extends Image {

        private boolean selected = false;
        private Texture selectionRing;

        private SelectionImage(Gainea game, TextureRegion region) {
            super(region);
            setScaling(Scaling.fill);
            setOrigin(41, 41);
            layout();
            this.selectionRing = game.assets.get("textures/selection_ring.png", Texture.class);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            if (selected) {
                batch.draw(selectionRing, getX() - 5, getY() - 5,
                        selectionRing.getWidth() * getScaleX(), selectionRing.getHeight() * getScaleY());
            }
        }
    }


}
