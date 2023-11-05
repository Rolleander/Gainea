package com.broll.gainea.client;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public final class AudioPlayer {

    private static float soundVolume = 0.5f;
    private static float musicVolume = 0.5f;
    private static float musicLower = 0.7f;
    private static Assets assets;
    private static Music current;

    private AudioPlayer() {

    }

    public static void init(Assets assets) {
        AudioPlayer.assets = assets;
    }

    public static void playSong(String song) {
        if (current != null) {
            current.stop();
        }
        current = assets.get(song, Music.class);
        current.setLooping(true);
        current.setVolume(musicLower * musicVolume);
        current.play();
    }

    public static void stopMusic() {
        if (current != null) {
            current.stop();
        }
    }

    public static void playSound(String sound) {
        assets.get(sound, Sound.class).play(soundVolume);
    }

    public static void changeMusicVolume(double volume) {
        musicVolume = (float) volume;
        if (current != null) {
            current.setVolume(musicLower * musicVolume);
        }
    }

    public static void changeSoundVolume(double volume) {
        soundVolume = (float) volume;
        if (current != null) {
            current.setVolume(musicVolume);
        }
    }

    public static float getMusicVolume() {
        return musicVolume;
    }

    public static float getSoundVolume() {
        return soundVolume;
    }

}
