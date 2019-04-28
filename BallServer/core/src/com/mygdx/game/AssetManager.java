package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Body;

import java.util.LinkedList;

public class AssetManager {
    private static LinkedList<Body> kill_list = new LinkedList<Body>(); //list of bodies to be safely destroyed

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
