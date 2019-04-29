package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Player extends Entity {

    private static CopyOnWriteArrayList<Player> player_list = new CopyOnWriteArrayList<Player>();

    float mx;
    float my;

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

        Player.player_list.add(this);
    }

    public void handleInput(String raw_inputs) { //takes in user inputs from client and does physics simulations
        String[] inputs = raw_inputs.split(",");
        this.body.setLinearVelocity(0,0); //reset velocity
        for (String key : inputs) {
            if (key.contains("MOUSE_POS")) {
                String[] data = key.split("-");
                this.mx = Float.parseFloat(data[1]);
                this.my = Float.parseFloat(data[2]);
                System.out.println(this.mx+" "+this.my);
            }
            if (key.equals("MOUSE_LEFT")) {  } //shoot bullet
            if (key.equals("Key_W")) { this.body.setLinearVelocity(this.body.getLinearVelocity().x,speed); }
            if (key.equals("Key_S")) { this.body.setLinearVelocity(this.body.getLinearVelocity().x,-speed); }
            if (key.equals("Key_A")) { this.body.setLinearVelocity(-speed,this.body.getLinearVelocity().y); }
            if (key.equals("Key_D")) { this.body.setLinearVelocity(speed,this.body.getLinearVelocity().y); }
        }
    }

    public static String send_all() { //packages all entity positions into a string
        String msg = "";
        for (Player p : Player.player_list) { //for each entity
            msg += (" "+p.getId()+","+p.getTexturePath()+","+p.getX()+","+p.getY()+","+p.getMX()+","+p.getMY());
        }

        if (!msg.equals("")) { msg = msg.substring(1); } //get rid of extra space
        return msg;
    }

    public static void removeEntity(Player player) {
        assert (Player.player_list.contains(player)): "The entity that you are trying to remove isn't in the master list";
        Player.player_list.remove(player);
    }

    public float getMX() { return this.mx; }
    public float getMY() { return this.my; }

}
