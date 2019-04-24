package com.mygdx.game;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Map {
    TiledMap map;
    String file_path;

    public Map(String file_path) {
        this.file_path = file_path;
        this.map = new TmxMapLoader().load(file_path);
    }

    public TiledMap getMap() { return this.map; }
}
