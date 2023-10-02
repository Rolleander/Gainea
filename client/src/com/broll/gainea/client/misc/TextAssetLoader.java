package com.broll.gainea.client.misc;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class TextAssetLoader extends AsynchronousAssetLoader<TextAsset, TextAssetLoader.TextParameter> {

    public TextAssetLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    TextAsset TextAsset;

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, TextParameter parameter) {
        this.TextAsset = null;
        this.TextAsset = new TextAsset(file);
    }

    @Override
    public TextAsset loadSync(AssetManager manager, String fileName, FileHandle file, TextParameter parameter) {
        TextAsset TextAsset = this.TextAsset;
        this.TextAsset = null;
        return TextAsset;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TextParameter parameter) {
        return null;
    }

    public static class TextParameter extends AssetLoaderParameters<TextAsset> {

    }

}