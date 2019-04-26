package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Player extends Entity {

    //TODO: LIST OF PROJECTILES THE PLAYER OWNS

    public Player(String texture_path) {
        super(texture_path);

        //init player body
        CircleShape circle = new CircleShape(); //Players have a circular fixture
        circle.setRadius((this.spriteWidth/4f)/Global.PPM); //diameter of the circle is half of the width of the entity
        FixtureDef fdef = new FixtureDef();
        fdef.shape = circle;
        this.body = Global.createBody(fdef,BodyDef.BodyType.DynamicBody);
        this.body.setLinearDamping(Global.PLAYER_DAMPING);
    }

    public void handleInput(String raw_inputs) { //takes in user inputs from client and does physics simulations
        String[] inputs = raw_inputs.split(",");
        this.body.setLinearVelocity(0,0); //reset velocity
        for (String key : inputs) {
            if (key.equals("Key_W")) { this.body.applyLinearImpulse(0,speed,0,0,true); }
            if (key.equals("Key_S")) { this.body.applyLinearImpulse(0,-speed,0,0,true); }
            if (key.equals("Key_A")) { this.body.applyLinearImpulse(-speed,0,0,0,true); }
            if (key.equals("Key_D")) { this.body.applyLinearImpulse(speed,0,0,0,true); }
        }
    }

}
