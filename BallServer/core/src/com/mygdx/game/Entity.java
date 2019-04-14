package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

//very basic rn, add box2d integration later
public class Entity {
    //TODO: REMOVE ENTITY FROM LIST WHEN CLIENT DCs
    //used so we know which piece of data belongs to which entity
    private static ConcurrentHashMap<Integer,Entity> entity_library = new ConcurrentHashMap<Integer, Entity>();

    private int id;
    private String texture_path;
    private Sprite sprite;
    private int speed = 2;

    public Entity(String texture_path) {
        this.id = Global.new_code();
        this.texture_path = texture_path;
        this.sprite = new Sprite(new Texture(this.texture_path));
        entity_library.put(this.id,this);
    }

    public void handleInput(String key) { //takes in user inputs from client and does physics simulations
        /*
        if (key.equals("Key_W")) { this.y += speed; }
        else if (key.equals("Key_S")) { this.y -= speed; }
        else if (key.equals("Key_A")) { this.x -= speed; }
        else if (key.equals("Key_D")) { this.x += speed; }
        */
    }

    public static Entity getEntity(int id) {
        if (Entity.entity_library.containsKey(id)) {
            return Entity.entity_library.get(id);
        }
        return null;
    }

    public static String send_all() { //packages all entity positions into a string
        String msg = "";
        for (Entity e : Entity.entity_library.values()) { //for each entity
            msg += (" "+e.getId()+","+e.getTexturePath()+","+e.getX()+","+e.getY());
        }

        if (msg.equals("")==false) { msg = msg.substring(1); } //get rid of extra space
        return msg;
    }

    //Getters
    public float getX() { return this.sprite.getX(); }
    public float getY() { return this.sprite.getY(); }
    public float getRotation() { return this.sprite.getRotation(); }
    public String getTexturePath() { return this.texture_path; }
    public int getId() { return this.id; }

    //Setters
    public void init_pos(float x, float y, float rotation) { //DONT USE THIS TO MOVE THE ENTITY, INSTEAD USE PHYSICS
        this.sprite.setX(x);
        this.sprite.setY(y);
        this.sprite.setRotation(rotation);
    }

}
