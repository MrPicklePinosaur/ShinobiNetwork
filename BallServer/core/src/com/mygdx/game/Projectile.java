package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

class Projectile extends Entity {

    //Entity owner; //keeps track of who created the projectile

    public Projectile(String file_path) {
        super(file_path);

        //init body
        PolygonShape rect = new PolygonShape();
        rect.setAsBox(this.spriteWidth,this.spriteHeight);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = rect;
        this.body = Global.createBody(fdef, BodyDef.BodyType.KinematicBody);

        //REMOVE THIS LATER
        this.body.setLinearVelocity(2,2);
    }


}
