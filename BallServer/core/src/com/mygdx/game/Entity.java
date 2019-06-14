/* Shinboi Network
 ______   __   __   ______  __   ______  __  __
/\  ___\ /\ "-.\ \ /\__  _\/\ \ /\__  _\/\ \_\ \
\ \  __\ \ \ \-.  \\/_/\ \/\ \ \\/_/\ \/\ \____ \
 \ \_____\\ \_\\"\_\  \ \_\ \ \_\  \ \_\ \/\_____\
  \/_____/ \/_/ \/_/   \/_/  \/_/   \/_/  \/_____/

    Anything that moves and has a sprite is an entity
    Entity has 3 subclasses, player: projectile and weapon
 */

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Entity {

    private static CopyOnWriteArrayList<Entity> entity_list = new CopyOnWriteArrayList<Entity>(); //holds all alive entities
    private CopyOnWriteArrayList<Projectile> projectile_list = new CopyOnWriteArrayList<Projectile>();

    protected ET entity_type;
    protected int id;
    protected String name;
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

    public static void removeEntity(Entity entity) { //tell client to delete entity
        assert (Entity.entity_list.contains(entity)): "The entity that you are trying to remove isn't in the master list";
        Entity.entity_list.remove(entity); //delete entity from server side lib
        BallClientHandler.broadcast(MT.KILLENTITY,""+entity.getId()); //tell client to stop drawing it
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

        } else if (fire_pattern.equals("random")) {
            float offset = Global.rnd.nextInt(180)*MathUtils.degreesToRadians;
            this.newProjectile(name,angle-MathUtils.PI/2+offset,dmg_mult,speed_mult);
        }
    }

    public void newProjectile(String name, float angle, float dmg_mult, float speed_mult, Vector2 spawn_pos) {
        Projectile p = new Projectile(name,this);
        AssetManager.flagForMove(p,new Vector3(spawn_pos.x,spawn_pos.y,angle- MathUtils.degreesToRadians*45));
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
        if (!this.projectile_list.contains(projectile)) { return; } //if there is a probelm, ignore it
        this.projectile_list.remove(projectile);
        Entity.removeEntity(projectile);
        AssetManager.flagForPurge(projectile.getBody());
    }

    //statoc stuff
    public static CopyOnWriteArrayList<Entity> getEntityList() { return Entity.entity_list; }
    public static void clearEntityList() { Entity.entity_list.clear(); }

    //getters
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
