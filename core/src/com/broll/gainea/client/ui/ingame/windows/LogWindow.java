package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.components.IconLabel;
import com.broll.gainea.client.ui.utils.LabelUtils;

public class LogWindow extends MenuWindow {

    private Table logTable;
    private ScrollPane logScrollPane;

    public LogWindow(Gainea game) {
        super(game, "Log", game.ui.skin);
        logTable = new Table(skin);
        logTable.top();
        logTable.setBackground("menu-bg");
        logTable.pad(10);
        logScrollPane = new ScrollPane(logTable, skin);
        logScrollPane.setScrollBarPositions(false, true);
        logScrollPane.setOverscroll(false, false);
        logScrollPane.setScrollingDisabled(true, false);
        logScrollPane.setFadeScrollBars(false);
        add(logScrollPane).fillX().expandX().fillY().expandY();
        center(800, 500);
        log("hallo [RED]player[] wie gehts dir denn?");
    }

    public void log(String message) {
        addLog(basicLog(message));
    }

    public void logCardEvent(String message) {
        addLog(iconLog(7, message));
    }

    private Table basicLog(String message) {
        Table log = new Table(skin);
        log.add(LabelUtils.markup(skin, message));
        return log;
    }

    private Table iconLog(int icon, String message) {
        Table log = new Table(skin);
        log.add(new IconLabel(game, icon, message));
        return log;
    }

    private void addLog(Table log) {
        log.left();
        log.top();
        logTable.add(log).expandX().fillX().row();
        logScrollPane.invalidate();
        logScrollPane.layout();
        logScrollPane.setScrollY(logScrollPane.getMaxY());
    }

    @Override
    public void update() {

    }


}
