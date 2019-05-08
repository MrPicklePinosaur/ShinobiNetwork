/*
 ______   ______   ______   ______  ______     __    __   ______   __   __   ______   ______   ______   ______
/\  __ \ /\  ___\ /\  ___\ /\  ___\/\__  _\   /\ "-./  \ /\  __ \ /\ "-.\ \ /\  __ \ /\  ___\ /\  ___\ /\  == \
\ \  __ \\ \___  \\ \___  \\ \  __\\/_/\ \/   \ \ \-./\ \\ \  __ \\ \ \-.  \\ \  __ \\ \ \__ \\ \  __\ \ \  __<
 \ \_\ \_\\/\_____\\/\_____\\ \_____\ \ \_\    \ \_\ \ \_\\ \_\ \_\\ \_\\"\_\\ \_\ \_\\ \_____\\ \_____\\ \_\ \_\
  \/_/\/_/ \/_____/ \/_____/ \/_____/  \/_/     \/_/  \/_/ \/_/\/_/ \/_/ \/_/ \/_/\/_/ \/_____/ \/_____/ \/_/ /_/

 */

package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.LinkedList;

public class AssetManager { //mainly just a bunch of helper methods
    private static LinkedList<Body> kill_list = new LinkedList<Body>(); //list of bodies to be safely destroyed


    //helper methods for bodies
    public static Body createBody(FixtureDef fdef, BodyDef.BodyType bodyType) { //takes in a fixture and creates a body
        BodyDef bdef = new BodyDef();
        bdef.type = bodyType;
        assert (Global.world != null): "world has not been initialized";
        Body new_body = Global.world.createBody(bdef);
        new_body.createFixture(fdef);
        return new_body;
    }

    public static void flagForPurge(Body body) { AssetManager.kill_list.add(body); }
    public static void sweepBodies() { //removes all bodies safely
        for (Body b : AssetManager.kill_list) {
            assert(b != null): "Body you are trying to purge is null";
            Global.world.destroyBody(b);
            b.setUserData(null);
            b = null;
        }
        AssetManager.kill_list.clear();
    }

}
