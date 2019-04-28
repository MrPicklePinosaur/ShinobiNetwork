package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Entity {
    private static Entity client_entity; //the entity that this specific client owns
    private static int client_entity_id = -1;

    private static ConcurrentHashMap<Integer,Entity> entity_library = new ConcurrentHashMap<Integer,Entity>(); //used so we know which piece of data belongs to which entity

    private float x;
    private float y;
    private float rotation;

    private Animation<TextureRegion> animation;
    private float frameTime = 0.25f; //used for animation

    private Entity(String texture_path) { //THE ONLY TIME CLIENT IS ALLOWED TO CREATE ENTITIES IS IF THE SERVER SAYS SO

        assert (AssetLoader.animation_lib.containsKey(texture_path)): "Texture hasn't been loaded yet.";
        TextureRegion[] frames = AssetLoader.getAnimation(texture_path);
        this.animation = new Animation<TextureRegion>(frameTime,frames);

        this.x = 0;
        this.rotation = 0;
        this.frameTime = 1f;
    }

    public void set_pos(float x, float y, float rotation) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
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
            //This block creates and integrates the entity
            Entity newEntity = new Entity(texture_path);
            entity_library.put(id,newEntity);

            if (id == Entity.client_entity_id) {
                Entity.client_entity = newEntity;
            }

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

    public void stepFrame(float deltaTime) { this.frameTime += deltaTime; } //possibly combine with getFrame
    public static void stepFrameAll(float deltaTime) {
        for (Entity e : Entity.entity_library.values()) {
            e.stepFrame(deltaTime);
        }
    }

    public TextureRegion getFrame() { return this.animation.getKeyFrame(this.frameTime,true); }
    public static void drawAll(SpriteBatch batch) {
        for (Entity e : Entity.entity_library.values()) {
            TextureRegion tex = e.getFrame();
            batch.draw(tex,e.getX()-Global.SPRITESIZE/2,e.getY()-Global.SPRITESIZE/2); //TODO, add scaling and rotation ALSO, DONT ASSUME SPRITESIZE!!!!!
        }
    }

    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public static Entity getClientEntity() {
        //assert (Entity.client_entity != null): "Client_entity has not been initalized";
        return Entity.client_entity;
    }

    public static void assignClientId(int id) { Entity.client_entity_id = id; }

}
