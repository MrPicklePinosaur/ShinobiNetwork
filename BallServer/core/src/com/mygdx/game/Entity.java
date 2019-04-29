package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

//very basic rn, add box2d integration later
public abstract class Entity {
    //used so we know which piece of data belongs to which entity
    //private static ConcurrentHashMap<Integer,Entity> entity_library = new ConcurrentHashMap<Integer, Entity>();

    //just a simple list of all the alive entities
    private static HashMap<String,String> texture_dimensions = new HashMap<String, String>();

    protected int id;
    protected String texture_path;
    protected Body body;
    protected int speed = 2;
    protected int spriteWidth;
    protected int spriteHeight;

    public Entity(String texture_path) {
        this.id = Global.new_code();
        this.texture_path = texture_path;
        assert (Entity.texture_dimensions.containsKey(texture_path)): "Texture has not been initialized";

        //Init sprite
        String[] dim = Entity.texture_dimensions.get(texture_path).split("x"); //get the sprite dimensions
        this.spriteWidth = Integer.parseInt(dim[0]);
        this.spriteHeight = Integer.parseInt(dim[1]);
    }

    public static void init_textures(String texture_lib_path) { //load all tex
        try {
            Scanner fileReader = new Scanner(new BufferedReader(new FileReader(texture_lib_path)));
            while (fileReader.hasNext()) {
                //data comes in the form "texture_path","widthxheight"
                String[] data = fileReader.nextLine().split(",");
                Entity.texture_dimensions.put(data[0],data[1]);
            }
        } catch (IOException ex) { System.out.println(ex); }
    }


    //Getters
    public float getX() { return this.body.getPosition().x*Global.PPM; }
    public float getY() { return this.body.getPosition().y*Global.PPM; }
    public float getRotation() { return this.body.getTransform().getRotation(); }
    public Body getBody() { return this.body; }
    public String getTexturePath() { return this.texture_path; }
    public int getId() { return this.id; }

    //Setters
    public void init_pos(float x, float y, float rotation) { //DONT USE THIS TO MOVE THE ENTITY, INSTEAD USE PHYSICS
        this.body.setTransform(x,y,rotation);
    }

}
