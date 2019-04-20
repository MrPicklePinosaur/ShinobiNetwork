package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Entity {
    //TODO: find a way to remove any entities that are no longer needed
    private static ConcurrentHashMap<Integer,Entity> entity_library = new ConcurrentHashMap<Integer,Entity>(); //used so we know which piece of data belongs to which entity
    private static HashMap<String,Texture> texture_lib = new HashMap<String,Texture>(); //holds file_path and texture object

    private Sprite sprite;

    private Entity(String texture_path) { //THE ONLY TIME CLIENT IS ALLOWED TO CREATE ENTITIES IS IF THE SERVER SAYS SO
        assert (Entity.texture_lib.containsKey(texture_path)): "Texture hasn't been loaded yet.";
        this.sprite = new Sprite(Entity.texture_lib.get(texture_path));
    }

    public static void init_textures(String texture_lib_path) { //TODO: possibly use async loading later, if there are too many assets
        try {
            Scanner fileReader = new Scanner(new BufferedReader(new FileReader(texture_lib_path)));
            while (fileReader.hasNext()) {
                //data is in the form "texture_path"
                String texture_path = fileReader.nextLine();
                Texture texture = new Texture(texture_path);
                Entity.texture_lib.put(texture_path,texture);
            }
        } catch(IOException ex) { System.out.println(ex+", something went wrong when loading textures."); }
    }

    public void set_pos(float x, float y, float rotation) {
        this.sprite.setX(x);
        this.sprite.setY(y);
        this.sprite.setRotation(rotation);
    }

    public static void update_entity(String data) { //if entity doesnt exist, we create a new one
        //format: ID,texture_path,x,y
        String[] parsed = data.split(",");
        int id = Integer.parseInt(parsed[0]);
        String texture_path = parsed[1];
        float x = Float.parseFloat(parsed[2]);
        float y = Float.parseFloat(parsed[3]);
        float rot = Float.parseFloat(parsed[4]);

        Entity entity;
        if (!Entity.entity_library.containsKey(id)) { //if entity doesnt exist yet, create it
            Entity newEntity = new Entity(texture_path);
            entity_library.put(id,newEntity);
            entity = newEntity;
        } else {
            entity = Entity.entity_library.get(id);
        }

        //apply all the updates
        entity.set_pos(x,y,rot);
    }
    public static void kill_entity(int id) {
        assert (Entity.entity_library.containsKey(id)): "The entity you are trying to remove doesn't exist in master list";
        Entity.entity_library.remove(id);
    }

    public static void draw_all(SpriteBatch batch) { //iterate through entity hashmap and draws everything
        for (Entity e : Entity.entity_library.values()) {
            e.getSprite().draw(batch);
        }
    }

    public float getX() { return this.sprite.getX(); }
    public float getY() { return this.sprite.getY(); }
    public Sprite getSprite() { return this.sprite; }
}
