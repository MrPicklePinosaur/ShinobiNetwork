/*
 ______   __   __   ______  __   ______  __  __
/\  ___\ /\ "-.\ \ /\__  _\/\ \ /\__  _\/\ \_\ \
\ \  __\ \ \ \-.  \\/_/\ \/\ \ \\/_/\ \/\ \____ \
 \ \_____\\ \_\\"\_\  \ \_\ \ \_\  \ \_\ \/\_____\
  \/_____/ \/_/ \/_/   \/_/  \/_/   \/_/  \/_____/

 */

package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Entity {

    private CopyOnWriteArrayList<Projectile> projectile_list = new CopyOnWriteArrayList<Projectile>();

    //just a simple list of all the alive entities
    private static CopyOnWriteArrayList<Entity> entity_list = new CopyOnWriteArrayList<Entity>();

    protected ET entity_type;
    protected int id;
    protected String name;
    protected String texture_path;
    protected Body body;
    protected int spriteWidth;
    protected int spriteHeight;

    public Entity(String name) {
        this.id = Global.new_code();
        this.name = name;

        //Init sprite
        this.spriteWidth = Global.SPRITESIZE;
        this.spriteHeight = Global.SPRITESIZE;
        entity_list.add(this);
    }

    public static void removeEntity(Entity entity) {
        assert (Entity.entity_list.contains(entity)): "The entity that you are trying to remove isn't in the master list";
        Entity.entity_list.remove(entity);
        BallClientHandler.broadcast(MT.KILLENTITY,""+entity.getId()); //tell client to stop drawing it
    }

    public static void send_all() { //packages all entity positions into a string
        if (Entity.entity_list.size() == 0) { return; }
        String msg = "";
        for (Entity e : Entity.entity_list) { //for each entity
            try { //possibly a bad idea to slap a try-catch here
                msg += (" " + e.getET().toString() + "," + e.getId() + "," + e.getName() + "," + e.getX() + "," + e.getY() + "," + e.getRotation());
            } catch (NullPointerException ex) { System.out.println(ex); }
        }

        if (!msg.equals("")) { msg = msg.substring(1); } //get rid of extra space
        BallClientHandler.broadcast(MT.UPDATEENTITY,msg);
    }

    //Projecitle stuff
    public void shoot(String name,float angle,String fire_pattern,float dmg_mult,float speed_mult) {
        if (fire_pattern.equals("straight")) { this.newProjectile(name,angle, dmg_mult,speed_mult); }
        else if (fire_pattern.equals("double")) {
            this.newProjectile(name, angle - 7 * MathUtils.degreesToRadians, dmg_mult,speed_mult);
            this.newProjectile(name, angle + 7 * MathUtils.degreesToRadians, dmg_mult,speed_mult);
        } else if (fire_pattern.equals("triple")) {
            for (int i = -1; i < 2; i++) { this.newProjectile(name,angle+10*i*MathUtils.degreesToRadians, dmg_mult,speed_mult); }
        } else if (fire_pattern.equals("penta")) {
            for (int i = -2; i < 5; i++) { this.newProjectile(name,angle+8*i*MathUtils.degreesToRadians, dmg_mult,speed_mult); }
        } else if (fire_pattern.equals("ring")) {

            float radius = 0.5f*Global.PPM; //hard coded radius for now
            for (int i = 0; i < 12; i++) {
                float direct = angle-30*i*MathUtils.degreesToRadians;
                float x = this.getX()+radius*MathUtils.cos(direct);
                float y = this.getY()+radius*MathUtils.sin(direct);
                Vector2 spawn_pos = new Vector2(x,y);
                this.newProjectile(name,direct-90*MathUtils.degreesToRadians,dmg_mult,speed_mult,spawn_pos);
            }

        }
    }

    public void newProjectile(String name, float angle, float dmg_mult, float speed_mult, Vector2 spawn_pos) {
        Projectile p = new Projectile(name,this);
        p.init_pos(spawn_pos.x/Global.PPM,spawn_pos.y/Global.PPM,angle- MathUtils.degreesToRadians*45); //bullet sprites are at a 45 degree angle
        p.setDamage(p.getDamage()*dmg_mult);
        p.setSpeed(p.getSpeed()*speed_mult);

        p.setVelocity(angle);
        this.projectile_list.add(p);
    }
    private void newProjectile(String name,float angle,float dmg_mult,float speed_mult) {
        this.newProjectile(name,angle,dmg_mult,speed_mult,new Vector2(this.getX(),this.getY()));
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
    public String getName() { return this.name; }
    public int getId() { return this.id; }
    public CopyOnWriteArrayList<Projectile> getProjectileList() { return projectile_list; }

    //Setters
    public void init_pos(float x, float y, float rotation) { //DONT USE THIS TO MOVE THE ENTITY, INSTEAD USE PHYSICS
        this.body.setTransform(x,y,rotation);
    }

    public abstract void init_stats(String json_data);

}
