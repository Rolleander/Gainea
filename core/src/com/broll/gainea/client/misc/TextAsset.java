package com.broll.gainea.client.misc;

import com.badlogic.gdx.files.FileHandle;

public class TextAsset {

    private String string;

    public TextAsset() {
        this.string = new String("".getBytes());
    }

    public TextAsset(byte[] data) {
        this.string = new String(data);
    }

    public TextAsset(String string) {
        this.string = new String(string.getBytes());
    }

    public TextAsset(FileHandle file) {
        this.string = new String(file.readBytes());
    }

    public TextAsset(TextAsset textAsset) {
        this.string = new String(textAsset.getContent().getBytes());
    }

    public void setContent(String string) {
        this.string = string;
    }

    public String getContent() {
        return this.string;
    }

    public void clear() {
        this.string = new String("".getBytes());
    }

}