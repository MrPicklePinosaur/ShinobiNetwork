/* Shinobi Network
 ______  ______   ______  ______  __   ______   __       ______
/\  == \/\  __ \ /\  == \/\__  _\/\ \ /\  ___\ /\ \     /\  ___\
\ \  _-/\ \  __ \\ \  __<\/_/\ \/\ \ \\ \ \____\ \ \____\ \  __\
 \ \_\   \ \_\ \_\\ \_\ \_\ \ \_\ \ \_\\ \_____\\ \_____\\ \_____\
  \/_/    \/_/\/_/ \/_/ /_/  \/_/  \/_/ \/_____/ \/_____/ \/_____/

 Stores info about all the currently alive particle effects
 */

package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Particle {

    private static ArrayList<Particle> particle_list = new ArrayList<Particle>(); //master lib of particles

    private Entity entity;
    private String name;
    private int duration;

    public Particle(Entity entity,String name,int duration) {
        this.entity = entity;
        this.name = name;
        this.duration = duration;

        Particle.particle_list.add(this);
    }

    public static void send_particles() { //send updated positions of all the particles
        if (Particle.particle_list.size() == 0) { return; }
        String msg = "";
        for (Particle p : Particle.particle_list) {
            msg += (" "+p.entity.getId()+","+p.name+","+p.duration); //we have to bind an entity to the particle
        }
        Particle.particle_list.clear();

        if (!msg.equals("")) { msg = msg.substring(1); } //get rid of extra space
        BallClientHandler.broadcast(MT.UPDATEPARTICLE,msg);
    }

}
