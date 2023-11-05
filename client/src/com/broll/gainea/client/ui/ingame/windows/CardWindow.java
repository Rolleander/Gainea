package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.game.GameStateListener;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.ui.utils.ActionListener;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Card;

import java.util.List;
import java.util.Optional;

public class CardWindow extends MenuWindow {

    private final Table cardList;

    private final Table cardView;

    private NT_Card selectedCard;
    private List<NT_Action_Card> playableCards;
    private PlayerPerformOptionalAction playerPerformAction;
    private boolean cardsPlayable = false;

    public CardWindow(Gainea game) {
        super(game, "Karten", game.ui.skin);
        this.cardList = new Table(game.ui.skin);
        this.cardView = new Table(game.ui.skin);
        cardList.top().left();
        cardList.defaults().space(10);
        add(cardList).padTop(10).left().padLeft(15).row();
        add(cardView).padTop(10).left().padBottom(10).expand().fill();
        TableUtils.consumeClicks(this);
        center(920, 410);
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

    public static Table previewCard(Gainea game, NT_Card card) {
        return new PreviewCard(game, card, null, false);
    }

    public static Table renderCard(Gainea game, NT_Card card) {
        return new CardView(game, card, null);
    }

    public void updatePlayableCards(List<NT_Action_Card> cards, PlayerPerformOptionalAction playerPerformAction) {
        this.playableCards = cards;
        this.playerPerformAction = playerPerformAction;
    }

    public void update() {
        cardList.clear();
        cardView.clear();
        int cardCount = game.state.getCards().size();
        for (int i = 0; i < cardCount; i++) {
            NT_Card card = game.state.getCards().get(i);
            PreviewCard preview = new PreviewCard(game, card, () -> selectCard(card), getPlayAction(card) != null);
            cardList.add(preview);
            if (i % 9 == 8) {
                cardList.row();
            }
        }
        setHeight(TextureUtils.CARD_HEIGHT + 20 + ((TextureUtils.CARD_HEIGHT / 2) + 20) * (float) Math.ceil(cardCount / 9f));
        if (selectedCard == null) {
            if (cardCount > 0) {
                selectCard(game.state.getCards().get(0));
            }
        } else if (game.state.getCards().stream().noneMatch(it -> it.id == selectedCard.id)) {
            if (cardCount > 0) {
                selectCard(game.state.getCards().get(0));
            } else {
                this.selectedCard = null;
            }
        } else {
            selectCard(selectedCard);
        }
    }

    private void selectCard(NT_Card card) {
        this.selectedCard = card;
        this.cardList.getChildren().forEach(it -> {
            ((PreviewCard) it).setSelected(((PreviewCard) it).card.id == selectedCard.id);
        });
        cardView.clear();
        ActionListener playListener = null;
        NT_Action_Card action = getPlayAction(card);
        if (action != null) {
            playListener = () -> {
                CardWindow.this.setVisible(false);
                playerPerformAction.perform(action, 0, null);
            };
        }
        cardView.add(new CardView(game, selectedCard, playListener));
    }

    private NT_Action_Card getPlayAction(NT_Card card) {
        if (playableCards != null) {
            Optional<NT_Action_Card> playableCard = playableCards.stream().filter(it -> it.cardId == card.id).findFirst();
            if (cardsPlayable) {
                return playableCard.orElse(null);
            }
        }
        return null;
    }


    private static class CardView extends Table {

        private CardView(Gainea game, NT_Card card, ActionListener playListener) {
            super(game.ui.skin);
            defaults().space(15);
            left();
            setBackground("card-bg");
            Table box = new Table(game.ui.skin);
            box.top();
            box.add(LabelUtils.label(game.ui.skin, card.title)).left();
            if (playListener != null) {
                Button activate = TableUtils.textButton(game.ui.skin, "Aktivieren!", playListener);
                box.add(activate).right().pad(3);
            }
            box.row();
            int w = 540;
            box.add(LabelUtils.autoWrap(LabelUtils.info(game.ui.skin, card.text), w)).colspan(2).width(w).left().row();
            add(new Image(new TextureRegionDrawable(TextureUtils.cardPicture(game, card.picture)))).top();
            add(box).top().row();
        }
    }

    private static class PreviewCard extends Table {

        public NT_Card card;

        private PreviewCard(Gainea game, NT_Card card, ActionListener listener, boolean playable) {
            super(game.ui.skin);
            this.card = card;
            pad(3);
            TextureRegion region = TextureUtils.cardPicture(game, card.picture);
            region.setRegion((int) (region.getRegionX() + TextureUtils.CARD_HEIGHT * 0.5), region.getRegionY(), TextureUtils.CARD_HEIGHT, TextureUtils.CARD_HEIGHT);
            Image img = new Image(new TextureRegionDrawable(region));
            img.setScaling(Scaling.fit);
            img.setColor(Color.WHITE);
            Table imgBox = new Table(game.ui.skin);
            imgBox.setBackground("white");
            imgBox.add(img);
            add(imgBox).top().width(TextureUtils.CARD_HEIGHT / 2f).height(TextureUtils.CARD_HEIGHT / 2f);
            if (playable) {
                img.addAction(Actions.forever(Actions.sequence(
                        Actions.alpha(0.7f, 1f, Interpolation.smooth2),
                        Actions.alpha(1, 0.4f, Interpolation.circleIn)
                )));
            }
            if (listener != null) {
                TableUtils.onClick(this, () -> {
                    AudioPlayer.playSound("button.ogg");
                    listener.action();
                });
            }
            setSelected(false);
        }

        public void setSelected(boolean selected) {
            if (selected) {
                setBackground("selected");
            } else {
                setBackground("dark");
            }
        }

    }
}
