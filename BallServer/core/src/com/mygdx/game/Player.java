package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.ArrayList;
import java.util.HashMap;

public class Player extends Entity {

    private ArrayList<Projectile> projectile_list = new ArrayList<Projectile>();

    private float mx;
    private float my;

    //TODO: LIST OF PROJECTILES THE PLAYER OWNS

    public Player(String texture_path) {
        super(texture_path);
        this.mx = 0;
        this.my = 0;

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
            /*
            if (key.contains("MOUSE_POS")) {
                String[] data = key.split("-");
                this.mx = Float.parseFloat(data[1]);
                this.my = Float.parseFloat(data[2]);
                System.out.println(this.mx+" "+this.my);
            }
            */
            if (key.equals("Key_Q")) { this.newProjectile("katanaSlash.png"); }
            //if (key.equals("MOUSE_LEFT")) { this.newProjectile("katanaSlash.png"); } //shoot bullet
            if (key.equals("Key_W")) { this.body.setLinearVelocity(this.body.getLinearVelocity().x,speed); }
            if (key.equals("Key_S")) { this.body.setLinearVelocity(this.body.getLinearVelocity().x,-speed); }
            if (key.equals("Key_A")) { this.body.setLinearVelocity(-speed,this.body.getLinearVelocity().y); }
            if (key.equals("Key_D")) { this.body.setLinearVelocity(speed,this.body.getLinearVelocity().y); }
        }
    }

    public void newProjectile(String file_path) {
        Projectile p = new Projectile(file_path);
        p.init_pos(this.getX()/Global.PPM,this.getY()/Global.PPM,0);
        this.projectile_list.add(p);
    }
    public void removeProjectile(Projectile projectile) {
        //safe removal of projectile
        this.projectile_list.remove(projectile);
        AssetManager.flagForPurge(projectile.getBody());
    }

}
