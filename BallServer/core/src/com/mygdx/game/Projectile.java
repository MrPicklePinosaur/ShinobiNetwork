/*
 ______  ______   ______      __   ______   ______   ______  __   __       ______
/\  == \/\  == \ /\  __ \    /\ \ /\  ___\ /\  ___\ /\__  _\/\ \ /\ \     /\  ___\
\ \  _-/\ \  __< \ \ \/\ \  _\_\ \\ \  __\ \ \ \____\/_/\ \/\ \ \\ \ \____\ \  __\
 \ \_\   \ \_\ \_\\ \_____\/\_____\\ \_____\\ \_____\  \ \_\ \ \_\\ \_____\\ \_____\
  \/_/    \/_/ /_/ \/_____/\/_____/ \/_____/ \/_____/   \/_/  \/_/ \/_____/ \/_____/

 */

package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Json;
import javafx.util.Pair;

class Projectile extends Entity {

    private Entity owner; //keeps track of who created the projectile
    private ProjectileStats stats;

    public Projectile(String file_path,String json_stat_data,Entity owner) {
        super(file_path);
        this.entity_type = ET.PROJECTILE;
        this.owner = owner;

        //init body
        /*
        PolygonShape rect = new PolygonShape();
        rect.setAsBox((float)this.spriteWidth/Global.PPM,(float)this.spriteHeight/Global.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = rect;
        */
        CircleShape circle = new CircleShape();
        circle.setRadius((this.spriteWidth/4f)/Global.PPM); //diameter of the circle is half of the width of the projecile
        FixtureDef fdef = new FixtureDef();
        fdef.shape = circle;

        fdef.filter.categoryBits = Global.BIT_PROJECTILE;
        fdef.filter.maskBits = Global.BIT_STATIC | Global.BIT_PLAYER;
        this.body = AssetManager.createBody(fdef, BodyDef.BodyType.DynamicBody);
        this.body.setUserData(new Pair<Class<?>,Projectile>(Projectile.class,this));
        //this.body.setUserData(this);

        circle.dispose();

        this.stats_from_json(json_stat_data);
    }

    public void setVelocity(float angle) {
        this.body.setLinearVelocity(this.getSpeed()* MathUtils.cos(angle),this.getSpeed()*MathUtils.sin(angle));
    }

    public void removeProjecitle() {
        this.owner.removeProjectile(this);
    }
    public Entity getOwner() { return this.owner; }

    public String getName() { return this.stats.getName(); }
    public int getDmg() { return this.stats.getDmg(); }
    public int getSpeed() { return this.stats.getSpeed(); }

    @Override
    public void stats_from_json(String json_data) {
        Json json = new Json();
        this.stats = json.fromJson(ProjectileStats.class,json_data);
    }

}

class ProjectileStats {
    private String name;
    private int dmg;
    private int speed;

    public ProjectileStats() { }
    public ProjectileStats(String name,int dmg, int speed) {
        this.name = name;
        this.dmg = dmg;
        this.speed = speed;
    }

    public String getName() { return this.name; }
    public int getDmg() { return this.dmg; }
    public int getSpeed() { return this.speed; }
}