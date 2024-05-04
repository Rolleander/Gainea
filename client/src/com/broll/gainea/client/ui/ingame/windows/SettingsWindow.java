package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Surrender;
import com.broll.gainea.net.NT_Vote_Base;
import com.broll.gainea.net.NT_Vote_KickPlayer;
import com.broll.gainea.net.NT_Vote_SkipTurn;

import java.util.function.DoubleConsumer;

public class SettingsWindow extends MenuWindow {
    public SettingsWindow(Gainea game) {
        super(game, "Optionen", game.ui.skin);
        defaults().pad(5);
        add(settingsPanel()).left().top().spaceRight(50);
        add(votingsPanel()).right().top();
        center(650, 100 + Math.max(2, game.state.getPlayers().size()) * 30);
    }

    private Table settingsPanel() {
        Table table = new Table();
        table.defaults().left().spaceBottom(5);
        table.add(LabelUtils.info(skin, "Musiklautstärke")).padRight(15);
        table.add(volumeSlider(AudioPlayer.getMusicVolume(), AudioPlayer::changeMusicVolume)).row();
        table.add(LabelUtils.info(skin, "Effektlautstärke")).padRight(15);
        table.add(volumeSlider(AudioPlayer.getSoundVolume(), AudioPlayer::changeSoundVolume)).row();
        table.add(TableUtils.textButton(skin, "Aufgeben", () -> {
            NT_Surrender nt = new NT_Surrender();
            game.client.getClient().sendTCP(nt);
        }));
        return table;
    }

    private Table votingsPanel() {
        Table table = new Table();
        table.defaults().left().spaceBottom(5);
        table.add(voteButton("Aktuellen Zug überspringen", new NT_Vote_SkipTurn())).row();
        for (NT_Player player : game.state.getPlayers()) {
            if (player.id != game.state.getPlayer().getId()) {
                NT_Vote_KickPlayer nt = new NT_Vote_KickPlayer();
                nt.playerId = player.id;
                table.add(voteButton(player.name + " entfernen", nt)).row();
            }
        }
        return table;
    }


    private Button voteButton(String text, NT_Vote_Base vote) {
        return TableUtils.textButton(skin, text, () -> {
            game.client.getClient().sendTCP(vote);
        });
    }
    
    private Slider volumeSlider(float initialValue, DoubleConsumer updateVolume) {
        Slider slider = new Slider(0, 1, 0.01f, false, skin);
        slider.setValue(initialValue);
        slider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                updateVolume.accept(slider.getValue());
            }
        });
        return slider;
    }

    @Override
    public void update() {

    }
}
