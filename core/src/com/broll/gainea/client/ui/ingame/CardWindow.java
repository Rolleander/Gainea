package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.GameStateListener;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.ui.elements.ClosableWindow;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.client.ui.elements.TableUtils;
import com.broll.gainea.client.ui.elements.TextureUtils;
import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Card;
import com.broll.gainea.server.core.GameContainer;

import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Optional;

public class CardWindow extends ClosableWindow {
    private Table content;
    private List<NT_Action_Card> playableCards;
    private PlayerPerformOptionalAction playerPerformAction;
    private boolean cardsPlayable = false;

    public CardWindow(Gainea game, Skin skin) {
        super(game, "Karten", skin);
        content = new Table();
        ScrollPane scrollPane = new ScrollPane(content, skin);
        scrollPane.setScrollBarPositions(false, true);
        scrollPane.setOverscroll(false, false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        add(scrollPane).expand().fill();
        TableUtils.consumeClicks(this);
        center(1000, 500);
        update();
        game.state.addListener(new GameStateListener() {
            @Override
            public void gameBusy() {
                cardsPlayable = false;
                update();
            }

            @Override
            public void playerTurnIdle() {
                cardsPlayable = true;
                update();
            }
        });
    }

    public void updatePlayableCards(List<NT_Action_Card> cards, PlayerPerformOptionalAction playerPerformAction) {
        this.playableCards = cards;
        this.playerPerformAction = playerPerformAction;
    }

    public void update() {
        content.clear();
        content.top().left();
        content.pad(10);
        content.defaults().space(10);
        game.state.getCards().forEach(card -> {
            Table table = renderCard(game, card);
            Optional<NT_Action_Card> playableCard = playableCards.stream().filter(it -> it.cardId == card.id).findFirst();
            if (cardsPlayable && playableCard.isPresent()) {
                table.add(TableUtils.textButton(game.ui.skin, "Aktivieren!", () -> {
                    playerPerformAction.perform(playableCard.get(), 0, null);
                })).right().bottom();
            }
            content.add(table).expandX().fillX().row();
        });
    }

    public static Table renderCard(Gainea game, NT_Card card) {
        Skin skin = game.ui.skin;
        Table table = new Table(skin);
        table.defaults().space(15);
        table.left();
        table.setBackground("menu-bg");
        Table box = new Table(skin);
        box.top();
        box.add(LabelUtils.label(skin, card.title)).left().row();
        int w = 600;
        box.add(LabelUtils.autoWrap(LabelUtils.info(skin, card.text), w)).width(w).left().row();
        table.add(new Image(new TextureRegionDrawable(TextureUtils.cardPicture(game, card.picture)))).top();
        table.add(box).top().row();
        return table;
    }

}
