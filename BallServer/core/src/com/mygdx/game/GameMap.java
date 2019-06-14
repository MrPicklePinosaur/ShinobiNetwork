/*
 __    __   ______   ______
/\ "-./  \ /\  __ \ /\  == \
\ \ \-./\ \\ \  __ \\ \  _-/
 \ \_\ \ \_\\ \_\ \_\\ \_\
  \/_/  \/_/ \/_/\/_/ \/_/

 */

package com.mygdx.game;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GameMap {
    private static HashMap<String, GameMap> map_list = new HashMap<String, GameMap>();

    private TiledMap map;

    //GameMap landmarks
    private ArrayList<Vector2> red_spawn = new ArrayList<Vector2>();
    private ArrayList<Vector2> blue_spawn = new ArrayList<Vector2>();
    private ArrayList<Vector2> solo_spawn = new ArrayList<Vector2>();
    private Vector2 default_camera;
    private HashMap<String,Rectangle> map_zones = new HashMap<String, Rectangle>();

    public GameMap(String file_path) {
        this.map = new TmxMapLoader().load(file_path);

        //load map objects
        this.loadCollisions();
        this.load_spawn_points(this.map.getLayers().get("spawn_points").getObjects());
        this.load_zones(this.map_zones);

    }

    public void loadCollisions() { //takes in all of the map's collision data and creates static bodies out of them
        MapObjects collisions = this.map.getLayers().get("COLLISION").getObjects();
        for (MapObject obj : collisions) { //get each of the objects on collision layer
            //for now, assume that all of the objects are rectagles TODO: ADD SUPPORT FOR CIRCULAR COLLISIONS
            Rectangle rect = ((RectangleMapObject) obj).getRectangle();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(rect.getWidth()/Global.PPM/2,rect.getHeight()/Global.PPM/2); //the parameters are half the width and half the height (for some reason)
            FixtureDef fdef = new FixtureDef();
            fdef.shape = shape;

            if (obj.getName() == null) { //default cp;;osopm
                fdef.filter.categoryBits = Global.BIT_STATIC;
                fdef.filter.maskBits = Global.BIT_PLAYER | Global.BIT_PROJECTILE;
            } else if (obj.getName().equals("walk_boundry")) { //walk boundties are collisions that bullets may pass thorugh
                fdef.filter.categoryBits = Global.BIT_STATIC;
                fdef.filter.maskBits = Global.BIT_PLAYER;
            } else if (obj.getName().equals("red_spawn_door")) { //collisions red players can go through but blue cant
                fdef.filter.categoryBits = Global.BIT_REDSTATIC;
            } else if (obj.getName().equals("blue_spawn_door")) { //vice versa
                fdef.filter.categoryBits = Global.BIT_BLUESTATIC;
            }

            //now create the actual body for the collision
            Body new_body = AssetManager.createBody(fdef, BodyDef.BodyType.StaticBody);
            new_body.setUserData(new Pair<Class<?>, GameMap>(GameMap.class,this));
            new_body.setTransform((rect.getX()+rect.getWidth()/2)/Global.PPM,(rect.getY()+rect.getHeight()/2)/Global.PPM,0);
            shape.dispose();
        }
    }

    public void load_spawn_points(MapObjects layer) {
        for (MapObject obj : layer) { //spawn points are saved as point objects on the TiledMap
            Rectangle rect = ((RectangleMapObject) obj).getRectangle(); //points are saved as rectangle with h = 0 and w =0
            Vector2 point = new Vector2(rect.x,rect.y);

            if (obj.getName().equals("red")) {
                this.red_spawn.add(point);
            } else if (obj.getName().equals("blue")) {
                this.blue_spawn.add(point);
            } else if (obj.getName().equals("solo")) {
                this.solo_spawn.add(point);
            } else if (obj.getName().equals("default_camera")) { //the point that the camera is binded to if the player is dead
                this.default_camera = point;
            }
        }
    }

    public void load_zones(HashMap<String,Rectangle> target) { //grab all the objects from the 'zones' layer on the tilemap
        MapObjects objectives = this.map.getLayers().get("zones").getObjects();
        for (MapObject obj : objectives) {
            Rectangle rect = ((RectangleMapObject) obj).getRectangle();
            target.put(obj.getName(),rect);
        }
    }

    public static void loadAll(String map_lib_path) { //loads all maps
        try {
            Scanner fileReader = new Scanner(new BufferedReader(new FileReader(map_lib_path)));
            while (fileReader.hasNext()) {
                //data comes in the form: "map_name","file_path"
                String[] data = fileReader.nextLine().split(",");
                GameMap newMap = new GameMap(data[1]);
                GameMap.map_list.put(data[0],newMap);
            }
            fileReader.close();
        } catch(IOException ex) { System.out.println(ex); }
    }

    public Vector2 get_spawn_point(TEAMTAG teamtag) { //find a suitable spawn point
        ArrayList<Vector2> spawn_list = new ArrayList<Vector2>();

        //Decide which spawn list to choose from
        if (teamtag == TEAMTAG.RED) { spawn_list = this.red_spawn; }
        else if (teamtag == TEAMTAG.BLUE) { spawn_list = this.blue_spawn; }
        else if (teamtag == TEAMTAG.SOLO) { spawn_list = this.solo_spawn; }

        assert (spawn_list.size() > 0): "No spawn points to choose from";

        Collections.shuffle(spawn_list); //randomize spawns

        Vector2 best_spawn = new Vector2();
        float best_dist = Float.NEGATIVE_INFINITY;
        for (Vector2 v : spawn_list) { //prioritize spawn points tht are the furthest away from all players
            float dist = 0;
            for (Player p : Global.game.getPlayerList()) { dist += Math.hypot(v.x-p.getX(),v.y-p.getY()); }

            if (dist > best_dist) {
                best_dist = dist;
                best_spawn = v;
            }
        }

        assert (best_spawn != null): "No spawn point found";
        return best_spawn;
    }

    //getters
    public HashMap<String, Rectangle> getObjectives() { return this.map_zones; }
    public TiledMap getTiledMap() { return this.map; }
    public Vector2 getDefaultCameraPoint() { return this.default_camera; }
}
