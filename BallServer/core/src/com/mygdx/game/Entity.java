/*
 ______   __   __   ______  __   ______  __  __
/\  ___\ /\ "-.\ \ /\__  _\/\ \ /\__  _\/\ \_\ \
\ \  __\ \ \ \-.  \\/_/\ \/\ \ \\/_/\ \/\ \____ \
 \ \_____\\ \_\\"\_\  \ \_\ \ \_\  \ \_\ \/\_____\
  \/_____/ \/_/ \/_/   \/_/  \/_/   \/_/  \/_____/

 */

package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.JsonValue;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Entity {

    private ArrayList<Projectile> projectile_list = new ArrayList<Projectile>();

    //just a simple list of all the alive entities
    private static CopyOnWriteArrayList<Entity> entity_list = new CopyOnWriteArrayList<Entity>();
    private static HashMap<String,String> texture_dimensions = new HashMap<String, String>();

    protected ET entity_type;
    protected int id;
    protected String texture_path;
    protected Body body;
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
            fileReader.close();
        } catch (IOException ex) { System.out.println(ex); }
    }

    public static void removeEntity(Entity entity) {
        assert (Entity.entity_list.contains(entity)): "The entity that you are trying to remove isn't in the master list";
        Entity.entity_list.remove(entity);
        BallClientHandler.broadcast(MT.KILLENTITY,""+entity.getId()); //tell client to stop drawing it
    }

    public static String send_all() { //packages all entity positions into a string
        String msg = "";
        for (Entity e : Entity.entity_list) { //for each entity
            ET et = e.getET();
            float rot = e.getRotation();

            msg += (" "+e.getET().toString()+","+e.getId()+","+e.getTexturePath()+","+e.getX()+","+e.getY()+","+rot);
        }

        if (!msg.equals("")) { msg = msg.substring(1); } //get rid of extra space
        return msg;
    }

    //Projecitle stuff
    public void newProjectile(String file_path,float angle,String firepattern) {
        Projectile p = new Projectile(file_path,AssetManager.getProjectileJsonData("slash"),this,firepattern);
        p.init_pos(this.getX()/Global.PPM,this.getY()/Global.PPM,angle- MathUtils.degreesToRadians*45); //bullet sprites are at a 45 degree angle
        p.setVelocity(angle);
        this.projectile_list.add(p);
    }
    public void removeProjectile(Projectile projectile) {
        //safe removal of projectile
        //assert(this.projectile_list.contains(projectile)): "projecitle you are trying to remove does not exist";
        if (!this.projectile_list.contains(projectile)) { return; } //if there is a probelm, ignore it
        this.projectile_list.remove(projectile);
        Entity.removeEntity(projectile);
        AssetManager.flagForPurge(projectile.getBody());
    }


    //Getters
    public ET getET() { return this.entity_type; }
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

    public abstract void init_stats(String json_data);

}
