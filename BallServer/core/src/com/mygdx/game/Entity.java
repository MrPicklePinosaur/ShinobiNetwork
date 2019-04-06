package com.mygdx.game;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

//very basic rn, add box2d integration later
public class Entity {
    //TODO: REMOVE ENTITY FROM LIST WHEN CLIENT DCs
    private static CopyOnWriteArrayList<Entity> entity_list = new CopyOnWriteArrayList<Entity>();

    private int x;
    private int y;
    private int speed = 2;

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
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
            msg += (" "+e.getX()+","+e.getY());
        }

        if (msg.equals("")==false) { msg = msg.substring(1); } //get rid of extra space
        return msg;
    }

    public int getX() { return this.x; }
    public int getY() { return this.y; }

}
