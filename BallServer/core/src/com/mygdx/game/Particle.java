package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Particle {

    private static ArrayList<Particle> particle_list = new ArrayList<Particle>();

    private String name;
    private float x;
    private float y;
    private int duration;

    public Particle(String name,float x,float y,int duration) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.duration = duration;

        Particle.particle_list.add(this);
    }


    public static void send_particles() {
        if (Particle.particle_list.size() == 0) { return; }
        String msg = "";
        for (Particle p : Particle.particle_list) {
            msg += (" "+p.name+","+p.x+","+p.y+","+p.duration);
        }
        Particle.particle_list.clear();

        if (!msg.equals("")) { msg = msg.substring(1); } //get rid of extra space
        BallClientHandler.broadcast(MT.UPDATEPARTICLE,msg);
    }

}
