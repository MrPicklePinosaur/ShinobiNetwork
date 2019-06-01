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
    private FIREPATTERN fire_pattern;
    private int speed;

    public Projectile(String name,Entity owner,String fire_pattern,int speed) {
        super(name);
        this.entity_type = ET.PROJECTILE;
        this.owner = owner;
        this.fire_pattern = FIREPATTERN.valueOf(fire_pattern.toUpperCase());
        this.speed = speed;

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
        fdef.isSensor = true;
        this.body = AssetManager.createBody(fdef, BodyDef.BodyType.DynamicBody);
        this.body.setUserData(new Pair<Class<?>,Projectile>(Projectile.class,this));
        //this.body.setUserData(this);

        circle.dispose();

    }

    public void setVelocity(float angle) {
        this.body.setLinearVelocity(this.speed* MathUtils.cos(angle),this.speed*MathUtils.sin(angle));
    }

    @Override public void init_stats(String json_data) { }

    public void removeProjecitle() {
        this.owner.removeProjectile(this);
    }
    public Entity getOwner() { return this.owner; }


}
