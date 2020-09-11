package com.broll.gainea.client.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class TextureUtils {

    public static TextureRegion[] split(Texture texture, int width, int height) {
        TextureRegion[][] tmp = TextureRegion.split(texture, width, height);
        int cols = texture.getWidth() / width;
        int rows = texture.getHeight() / height;
        TextureRegion[] regions = new TextureRegion[cols * rows];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                regions[index++] = tmp[i][j];
            }
        }
        return regions;
    }
}
