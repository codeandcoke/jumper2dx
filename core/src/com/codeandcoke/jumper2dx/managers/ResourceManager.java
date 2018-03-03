package com.codeandcoke.jumper2dx.managers;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Gestor de recursos de sonido e imagen del juego
 * @author Santiago Faci
 * @version Agosto 2014
 */
public class ResourceManager {

    public static AssetManager assets = new AssetManager();

    public static void loadAllResources() {

        // Sonidos y músicas
        assets.load("sounds/coin.wav", Sound.class);
        assets.load("sounds/jump.wav", Sound.class);
        assets.load("sounds/player_down.wav", Sound.class);
        assets.load("sounds/game_over.wav", Sound.class);
        assets.load("sounds/level_clear.wav", Sound.class);
        assets.load("sounds/kick.wav", Sound.class);
        assets.load("sounds/level1.mp3", Music.class);
        assets.load("sounds/level2.mp3", Music.class);
        assets.load("sounds/level3.mp3", Music.class);
        assets.load("sounds/item_appears.wav", Sound.class);
        assets.load("sounds/1up.wav", Sound.class);

        assets.load("items/items.pack", TextureAtlas.class);

        assets.load("characters/characters.pack", TextureAtlas.class);
    }

    public static void finishLoading() {
        assets.finishLoading();
    }

    public static boolean update() {
        return assets.update();
    }

    public static TextureAtlas getAtlas(String path) {
        return assets.get(path, TextureAtlas.class);
    }

    public static Sound getSound(String path) {
        return assets.get(path, Sound.class);
    }

    public static Music getMusic(String path) {
        return assets.get(path, Music.class);
    }

    public static void dispose() {
        assets.dispose();
    }
}
