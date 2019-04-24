package com.mygdx.game;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Map {
    TiledMap map;
    MapLayer collision_layer;
    MapObjects collisions;
    String file_path;

    public Map(String file_path) {
        this.file_path = file_path;
        this.map = new TmxMapLoader().load(file_path);
        this.collision_layer = this.map.getLayers().get("COLLISION");
        this.collisions = this.collision_layer.getObjects();
        //this.createBody();
    }

    /*
    public void createBody() { //takes in all of the map's collision data and creates static bodies out of them
        for (MapObject obj : this.collisions.getByType(RectangleMapObject)) { //get each of the objects on collision layer

        }
    }
    */


    public TiledMap getMap() { return this.map; }
}
