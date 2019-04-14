package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

//very basic rn, add box2d integration later
public class Entity {
    //TODO: REMOVE ENTITY FROM LIST WHEN CLIENT DCs
    private static CopyOnWriteArrayList<Entity> entity_list = new CopyOnWriteArrayList<Entity>();

    private int id;
    private String texture_path;
    private float x;
    private float y;
    private float rotation;
    private int speed = 2;

    public Entity(String texture_path) {
        this.id = Global.new_code();
        this.texture_path = texture_path;
        this.x = 0;
        this.y = 0;
        this.rotation = 0;
        entity_list.add(this);
    }

    public void handleInput(String key) { //takes in user inputs from client and does physics simulations
        if (key.equals("Key_W")) { this.y += speed; }
        else if (key.equals("Key_S")) { this.y -= speed; }
        else if (key.equals("Key_A")) { this.x -= speed; }
        else if (key.equals("Key_D")) { this.x += speed; }

    }

    public static String send_pos() { //packages all entity positions into a string
        String msg = "";
        for (Entity e : Entity.entity_list) { //for each entity
            msg += (" "+e.getId()+","+e.getTexturePath()+","+e.getX()+","+e.getY());
        }

        if (msg.equals("")==false) { msg = msg.substring(1); } //get rid of extra space
        return msg;
    }

    //Getters
    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public float getRotation() { return this.rotation; }
    public String getTexturePath() { return this.texture_path; }
    public int getId() { return this.id; }

    //Setters
    public void init_pos(float x, float y, float rotation) { //DONT USE THIS TO MOVE THE ENTITY, INSTEAD USE PHYSICS
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

}
