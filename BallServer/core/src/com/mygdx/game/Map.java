package com.mygdx.game;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Map {
    private static HashMap<String,Map> map_list = new HashMap<String, Map>();

    private TiledMap map;
    private MapLayer collision_layer;
    private MapObjects collisions;
    private String file_path;

    public Map(String file_path) {
        this.file_path = file_path;
        this.map = new TmxMapLoader().load(file_path);
        this.collision_layer = this.map.getLayers().get("COLLISION");
        this.collisions = this.collision_layer.getObjects();
        this.loadCollisions();
    }

    public void loadCollisions() { //takes in all of the map's collision data and creates static bodies out of them
        for (MapObject obj : this.collisions) { //get each of the objects on collision layer
            //for now, assume that all of the objects are rectagles TODO: ADD SUPPORT FOR CIRCULAR COLLISIONS
            Rectangle rect = ((RectangleMapObject) obj).getRectangle();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(rect.getWidth()/Global.PPM/2,rect.getHeight()/Global.PPM/2); //the parameters are half the width and half the height (for some reason)
            FixtureDef fdef = new FixtureDef();
            fdef.shape = shape;
            Body new_body = Global.createBody(fdef, BodyDef.BodyType.StaticBody);
            new_body.setTransform((rect.getX()+rect.getWidth()/2)/Global.PPM,(rect.getY()+rect.getHeight()/2)/Global.PPM,0);
        }
    }

    public static void loadAll(String map_lib_path) { //loads all maps
        try {
            Scanner fileReader = new Scanner(new BufferedReader(new FileReader(map_lib_path)));
            while (fileReader.hasNext()) {
                //data comes in the form: "map_name","file_path"
                String[] data = fileReader.nextLine().split(",");
                Map newMap = new Map(data[1]);
                Map.map_list.put(data[0],newMap);
            }
            fileReader.close();
        } catch(IOException ex) { System.out.println(ex); }
    }

    public TiledMap getTiledMap() { return this.map; }
    public static Map getMap(String map_name) {
        assert (Map.map_list.containsKey(map_name)): "Map is not found or has not been loaded";
        return Map.map_list.get(map_name);
    }
}
