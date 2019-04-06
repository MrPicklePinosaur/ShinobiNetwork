package com.mygdx.game;

import java.util.*;
import java.io.*;
import java.net.*;

//very basic rn, add box2d integration later
public class Entity {

    private static CopyOnWriteArrayList<Entity> entity_list = new CopyOnWriteArrayList<Entity>();

    private int x;
    private int y;
    private int speed = 2;

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
        entity_list.append(this);
    }

    public void handleInput(String key) { //takes in user inputs from client and does physics simulations
        if (usin.equals("Key_W")) { this.x += speed; }
        else if (usin.equals("Key_S")) { this.x -= speed; }
        else if (usin.equals("Key_A")) { this.y -= speed; }
        else if (usin.equals("Key_D")) { this.y += speed; }
    }

}
