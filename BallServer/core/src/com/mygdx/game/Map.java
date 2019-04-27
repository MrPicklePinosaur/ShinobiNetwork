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
        this.createBody();
    }

    public void createBody() { //takes in all of the map's collision data and creates static bodies out of them
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

    public TiledMap getMap() { return this.map; }
}
