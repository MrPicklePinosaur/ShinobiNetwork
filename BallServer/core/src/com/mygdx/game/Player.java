package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.*;

public class Player extends Entity {

    private float m_angle;

    //TODO: LIST OF PROJECTILES THE PLAYER OWNS

    public Player(String texture_path) {
        super(texture_path);
        this.entity_type = ET.PLAYER;
        this.m_angle = 0;

        //init player body
        CircleShape circle = new CircleShape(); //Players have a circular fixture
        circle.setRadius((this.spriteWidth/4f)/Global.PPM); //diameter of the circle is half of the width of the entity
        FixtureDef fdef = new FixtureDef();
        fdef.shape = circle;
        fdef.filter.categoryBits = Global.BIT_PLAYER;
        fdef.filter.maskBits = Global.BIT_STATIC | Global.BIT_PLAYER;
        this.body = Global.createBody(fdef,BodyDef.BodyType.DynamicBody);
        this.body.setUserData(new Pair<Class<?>,Player>(Player.class,this));
        //this.body.setUserData(this);
        this.body.setLinearDamping(Global.PLAYER_DAMPING);

        circle.dispose();
    }

    public void handleInput(String raw_inputs) { //takes in user inputs from client and does physics simulations
        String[] inputs = raw_inputs.split(",");
        this.body.setLinearVelocity(0,0); //reset velocity
        for (String key : inputs) {
            if (key.contains("MOUSE_ANGLE:")) {
                String[] data = key.split(":");
                this.m_angle = Float.parseFloat(data[1]);
            }
            if (key.equals("Key_Q")) { this.newProjectile("katanaSlash.png",this.m_angle); }
            //if (key.equals("MOUSE_LEFT")) { this.newProjectile("katanaSlash.png"); } //shoot bullet
            if (key.equals("Key_W")) { this.body.setLinearVelocity(this.body.getLinearVelocity().x,speed); }
            if (key.equals("Key_S")) { this.body.setLinearVelocity(this.body.getLinearVelocity().x,-speed); }
            if (key.equals("Key_A")) { this.body.setLinearVelocity(-speed,this.body.getLinearVelocity().y); }
            if (key.equals("Key_D")) { this.body.setLinearVelocity(speed,this.body.getLinearVelocity().y); }
        }
    }


}
