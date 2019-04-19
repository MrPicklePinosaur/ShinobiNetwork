package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

//very basic rn, add box2d integration later
public class Entity {
    //TODO: REMOVE ENTITY FROM LIST WHEN CLIENT DCs
    //used so we know which piece of data belongs to which entity
    //private static ConcurrentHashMap<Integer,Entity> entity_library = new ConcurrentHashMap<Integer, Entity>();

    //just a simple list of all the alive entities
    private static CopyOnWriteArrayList<Entity> entity_list = new CopyOnWriteArrayList<Entity>();

    private static HashMap<String,String> texture_dimensions = new HashMap<String, String>();

    private int id;
    private String texture_path;
    private AbstractSprite sprite;
    private int speed = 2;

    public Entity(String texture_path) {
        this.id = Global.new_code();
        this.texture_path = texture_path;
        assert (Entity.texture_dimensions.containsKey(texture_path)): "Texture has not been initialized";
        this.sprite = this.init_entity(Entity.texture_dimensions.get(texture_path));
        entity_list.add(this);
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

    public AbstractSprite init_entity(String dimensions) {
        String[] dim = dimensions.split("x");
        return new AbstractSprite(Integer.parseInt(dim[0]),Integer.parseInt(dim[1]));
    }

    public void handleInput(String raw_inputs) { //takes in user inputs from client and does physics simulations
        String[] inputs = raw_inputs.split(",");
        for (String key : inputs) {
            if (key.equals("Key_W")) { this.sprite.init_pos(sprite.getX(),sprite.getY()+speed,0); }
            if (key.equals("Key_S")) { this.sprite.init_pos(sprite.getX(),sprite.getY()-speed,0); }
            if (key.equals("Key_A")) { this.sprite.init_pos(sprite.getX()-speed,sprite.getY(),0); }
            if (key.equals("Key_D")) { this.sprite.init_pos(sprite.getX()+speed,sprite.getY(),0); }
        }
    }

    /*
    public static Entity getEntity(int id) {
        if (Entity.entity_library.containsKey(id)) {
            return Entity.entity_library.get(id);
        }
        return null;
    }
    */

    public static void removeEntity(Entity entity) {
        assert (Entity.entity_list.contains(entity)): "The entity that you are trying to remove isn't in the master list";
        Entity.entity_list.remove(entity);
    }

    public static String send_all() { //packages all entity positions into a string
        String msg = "";
        for (Entity e : Entity.entity_list) { //for each entity
            msg += (" "+e.getId()+","+e.getX()+","+e.getY()+","+e.getRotation());
        }

        if (!msg.equals("")) { msg = msg.substring(1); } //get rid of extra space
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
        this.sprite.init_pos(x,y,rotation);
    }

}
