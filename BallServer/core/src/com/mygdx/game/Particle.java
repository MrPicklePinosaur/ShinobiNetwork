package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Particle {

    private static HashMap<String, LinkedList<Vector2>> particle_list = new HashMap<String, LinkedList<Vector2>>();

    public static void newParticle(String name, Vector2 pos) {
        if (Particle.particle_list.containsKey(name)) {
            Particle.particle_list.get(name).add(pos);
        } else {
            LinkedList<Vector2> pos_list = new LinkedList<Vector2>();
            pos_list.add(pos);
            Particle.particle_list.put(name,pos_list);
        }
    }

    public static String send_particles() {
        String msg = "";
        for (String name : Particle.particle_list.keySet()) {
            LinkedList<Vector2> pos_list = Particle.particle_list.get(name);
            for (Vector2 pos : pos_list) {
                msg += (" "+name+","+pos.x+","+pos.y);
            }
        }

        if (!msg.equals("")) { msg = msg.substring(1); } //get rid of extra space
        return msg;
    }

}
