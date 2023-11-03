package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.game.GameStateListener;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.ui.ingame.unit.MenuUnit;
import com.broll.gainea.client.ui.utils.ActionListener;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_Action_BuyMerc;
import com.broll.gainea.net.NT_Unit;

import java.util.List;

public class ShopWindow extends MenuWindow {

    private final Table content;
    private boolean active = false;
    private List<NT_Action_BuyMerc> options;
    private PlayerPerformOptionalAction playerPerformAction;

    public ShopWindow(Gainea game) {
        super(game, "Shop", game.ui.skin);
        content = new Table(getSkin());
        add(content).expand().fill();
        content.defaults().pad(5);
        center(900, 410);
        game.state.addListener(new GameStateListener() {
            @Override
            public void gameBusy() {
                active = false;
                update();
            }

            @Override
            public void playerTurnIdle() {
                active = true;
                update();
            }
        });
    }

    @Override
    public void update() {
        content.clear();
        for (int i = 0; i < game.state.shop.units.length; i++) {
            ShopEntry entry = new ShopEntry(game.state.shop.units[i], game.state.shop.prices[i]);
            content.add(entry);
            if (i % 2 == 1) {
                content.row();
            }
        }
        updateActive();
    }

    public void updateState(List<NT_Action_BuyMerc> options, PlayerPerformOptionalAction playerPerformAction) {
        this.options = options;
        this.playerPerformAction = playerPerformAction;
        updateActive();
    }

    private void updateActive() {
        if (content.getChildren().isEmpty() || options == null || !active) return;
        for (NT_Action_BuyMerc action : options) {
            ShopEntry entry = (ShopEntry) content.getChildren().get(action.index);
            entry.activate(
                    () -> playerPerformAction.perform(action, 0, null)
            );
        }
    }

    private class ShopEntry extends Table {

        private NT_Unit unit;
        private Button button;

        ShopEntry(NT_Unit unit, short price) {
            super(skin);
            pad(5);
            this.unit = unit;
            MenuUnit entry = new MenuUnit(game, skin, unit, true);
            button = new Button(skin);
            button.add(LabelUtils.label(skin, "" + price));
            button.add(new Image(TextureUtils.icon(game, 2))).spaceLeft(5);
            button.setDisabled(true);
            add(button).center();
            add(entry).spaceLeft(5);
            addAction(Actions.alpha(0.6f));
        }

        public void activate(ActionListener listener) {
            addAction(Actions.alpha(1f));
            button.setDisabled(false);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    AudioPlayer.playSound("button.ogg");
                    event.stop();
                    ShopWindow.this.setVisible(false);
                    listener.action();
                }
            });
        }

    }
}
