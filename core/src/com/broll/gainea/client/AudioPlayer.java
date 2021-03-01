package com.broll.gainea.client;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public final class AudioPlayer {

    private AudioPlayer() {

    }

    private static Assets assets;
    private static Music current;

    public static void init(Assets assets) {
        AudioPlayer.assets = assets;
    }

    public static void playSong(String song) {
        if (current != null) {
            current.stop();
            current.dispose();
        }
        current = assets.get(song, Music.class);
        current.setLooping(true);
        current.play();
    }

    public static void playSound(String sound) {
        assets.get(sound, Sound.class).play();
    }

}
