package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

class Projectile extends Entity {

    //Entity owner; //keeps track of who created the projectile

    public Projectile(String file_path) {
        super(file_path);
        this.entity_type = ET.PROJECTILE;
        this.speed = 3;
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
        fdef.filter.maskBits = Global.BIT_STATIC;
        this.body = Global.createBody(fdef, BodyDef.BodyType.DynamicBody);
    }

    public void setVelocity(float angle) {
        this.body.setLinearVelocity(speed* MathUtils.cos(angle),speed*MathUtils.sin(angle));

    }


}
