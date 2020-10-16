package com.broll.gainea.client.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.google.common.util.concurrent.AsyncCallable;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class MessageUtils {

    private final static int MESSAGE_DURATION = 2000;

    private MessageUtils() {
    }

    public static void showCenterMessage(Gainea game, String message) {
        game.ui.inGameUI.showCenterOverlay(TableUtils.removeAfter(LabelUtils.title(game.ui.skin, message), MESSAGE_DURATION)).center();
    }

}
