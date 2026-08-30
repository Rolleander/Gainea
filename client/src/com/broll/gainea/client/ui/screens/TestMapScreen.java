package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.game.ClientMapContainer;
import com.broll.gainea.client.game.GoalState;
import com.broll.gainea.client.ui.Screen;
import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.net.proto.*;
import java.util.concurrent.atomic.AtomicInteger;

/** Offline protobuf-state map preview used during UI development. */
public class TestMapScreen extends Screen {
    private final AtomicInteger ids = new AtomicInteger();

    @Override public Actor build() {
        AudioPlayer.playSong("celtic.mp3");
        ClientMapContainer.RENDER_DEBUG = true;
        game.state.initOffline(TestMapTopology.create(), 0, 30, 0);

        NT_Shop.Builder shop = NT_Shop.newBuilder();
        for (int i = 0; i < 8; i++) {
            NT_ShopItem.Builder item = NT_ShopItem.newBuilder().setPrice(5 + i);
            if (i <= 5) item.setUnit(NT_ShopUnit.newBuilder().setPrice(5 + i)
                .setUnit(unit(-1, getAreaNum("Weidesteppe")).toBuilder().setDescription("langer kauftext ok passt")));
            else item.setOther(NT_ShopOther.newBuilder().setPrice(5 + i)
                .setDescription("langer text für spell zum kaufen ok passt bestimmt"));
            shop.addItems(item);
        }
        game.state.shop = shop.build();

        for (int i = 0; i < 3; i++) {
            NT_Goal.Builder goal = NT_Goal.newBuilder().setId(ids.incrementAndGet()).setPoints(1 + i)
                .setRestriction("Gainea").setDescription("Erobere zweitausend wasauchimmer Sachen")
                .setProgression(i).setProgressionGoal(5 * i);
            for (int h = 0; h < 10; h++) goal.addLocations(RandomUtils.random(0, 30));
            game.state.getGoals().add(new GoalState(goal.build()));
        }

        NT_BoardUpdate.Builder board = NT_BoardUpdate.newBuilder().setRound(0);
        for (int playerId = 0; playerId < 2; playerId++) {
            NT_Player.Builder player = NT_Player.newBuilder().setName("tester " + playerId).setCards(playerId)
                .setColor(playerId).setPoints(playerId * 10).setFraction(playerId).setId(playerId).setStars(playerId * 5);
            int count = playerId == 0 ? 10 : 3;
            int location = getAreaNum(playerId == 0 ? "Grünland" : "Große Wüste");
            for (int j = 0; j < count; j++) player.addUnits(unit(playerId, location));
            board.addPlayers(player);
        }
        board.addObjects(NT_BoardObject.newBuilder().setUnit(unit(-1, getAreaNum("Weidesteppe"))));
        game.state.update(board.build());
        game.ui.initInGameUi();
        game.state.getMap().displayRenders();
        game.ui.inGameUI.show();
        game.ui.inGameUI.updateWindows();
        addCard();
        return new Table();
    }

    private int getAreaNum(String area) { return game.state.getMap().getArea(area).getNumber(); }

    private NT_Unit unit(int owner, int location) {
        int health = MathUtils.random(1, 5);
        NT_Monster monster = NT_Monster.newBuilder().setStars(4).setId(ids.incrementAndGet())
            .setIcon(MathUtils.random(0, 100)).setName("Testfighter").setPower(MathUtils.random(1, 3))
            .setHealth(health).setMaxHealth(health).setOwner(owner).setLocation(location).build();
        return NT_Unit.newBuilder().setMonster(monster).setId(monster.getId()).setIcon(monster.getIcon())
            .setName(monster.getName()).setPower(monster.getPower()).setHealth(health).setMaxHealth(health)
            .setOwner(owner).setLocation(location).build();
    }

    private void addCard() {
        NT_Card card = NT_Card.newBuilder().setPicture(MathUtils.random(0, 80)).setTitle("coole karte")
            .setText("langer Text für eine Karte").setId(ids.incrementAndGet()).build();
        game.state.getCards().add(card);
        game.ui.inGameUI.updateWindows();
    }
}
