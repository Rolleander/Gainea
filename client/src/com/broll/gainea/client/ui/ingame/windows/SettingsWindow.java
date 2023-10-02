package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.net.NT_Surrender;

import java.util.function.DoubleConsumer;

public class SettingsWindow extends MenuWindow {
    public SettingsWindow(Gainea game) {
        super(game, "Optionen", game.ui.skin);
        defaults().pad(5);
        add(LabelUtils.info(skin, "Musiklautstärke")).padRight(15);
        add(volumeSlider(AudioPlayer.getMusicVolume(), AudioPlayer::changeMusicVolume)).row();
        add(LabelUtils.info(skin, "Effektlautstärke")).padRight(15);
        add(volumeSlider(AudioPlayer.getSoundVolume(), AudioPlayer::changeSoundVolume)).row();
        add(TableUtils.textButton(skin, "Aufgeben", () -> {
            NT_Surrender nt = new NT_Surrender();
            game.client.getClient().sendTCP(nt);
        }));
        center(320, 150);
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
