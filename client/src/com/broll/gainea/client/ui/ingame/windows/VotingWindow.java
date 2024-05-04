package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.net.NT_Vote;

public class VotingWindow extends Window {

    private Gainea game;
    private Table content = new Table();

    public VotingWindow(Gainea game, String text, boolean requestedByMe) {
        super("Abstimmung", game.ui.skin);
        this.game = game;
        setWidth(300);
        setHeight(120);
        defaults().pad(5).spaceBottom(10);
        add(LabelUtils.info(getSkin(), text)).row();
        if (requestedByMe) {
            waitForOthers();
        } else {
            content.add(TableUtils.textButton(getSkin(), "Ja!", () -> {
                answer(true);
            })).spaceRight(50);
            content.add(TableUtils.textButton(getSkin(), "Nein!", () -> {
                answer(false);
            })).spaceRight(50);
        }
        add(content);
        toFront();
    }

    public void result(boolean success) {
        content.clear();
        if (success) {
            content.add(LabelUtils.markup(getSkin(), "[GREEN] Abstimmung erfolgreich!"));
        } else {
            content.add(LabelUtils.markup(getSkin(), "[RED] Anfrage abgelehnt!"));
        }
        addAction(Actions.sequence(Actions.delay(3), Actions.fadeOut(0.3f), Actions.removeActor()));
    }

    private void answer(boolean yes) {
        waitForOthers();
        NT_Vote nt = new NT_Vote();
        nt.yes = yes;
        game.client.getClient().sendTCP(nt);
    }

    private void waitForOthers() {
        content.clear();
        content.add(LabelUtils.markup(getSkin(), "[BLUE] Abstimmung läuft..."));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getStage() != null) {
            setX(getStage().getWidth() - getWidth() - 10);
            setY(getStage().getHeight() - getHeight() - 10);
        }
    }
}
