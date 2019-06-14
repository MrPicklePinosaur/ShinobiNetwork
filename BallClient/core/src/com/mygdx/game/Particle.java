/* Shinobi Network
 ______  ______   ______  ______  __   ______   __       ______
/\  == \/\  __ \ /\  == \/\__  _\/\ \ /\  ___\ /\ \     /\  ___\
\ \  _-/\ \  __ \\ \  __<\/_/\ \/\ \ \\ \ \____\ \ \____\ \  __\
 \ \_\   \ \_\ \_\\ \_\ \_\ \ \_\ \ \_\\ \_____\\ \_____\\ \_____\
  \/_/    \/_/\/_/ \/_/ /_/  \/_/  \/_/ \/_____/ \/_____/ \/_____/

    Particle behavior and visuals
 */

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.FileHandler;

public class Particle {
    private static HashMap<String,FileHandle> behavior_lib = new HashMap<String, FileHandle>();
    private static TextureAtlas particle_atlas = new TextureAtlas();
    private static LinkedList<Particle> particle_list = new LinkedList<Particle>();

    private ParticleEffect particle;
    private Entity entity;
    private String name;

    public Particle(Entity entity,String name,int duration) {
        this.entity = entity;
        this.name = name;

        //create particle
        this.particle = new ParticleEffect();
        this.particle.load(Particle.behavior_lib.get(name),Particle.particle_atlas); //load behavior
        this.particle.setPosition(entity.getX(),entity.getY()); //bind to entity
        this.particle.setDuration(duration);
        Particle.particle_list.add(this);

        this.particle.start();
    }

    public static void load_particles(String lib_firepath) { //go through lib and load all partuckes
        try {
            Scanner fileReader = new Scanner(new BufferedReader(new FileReader(lib_firepath)));
            while (fileReader.hasNext()) {
                String particle_name = fileReader.nextLine();

                //load behavior
                FileHandle behavior = Gdx.files.internal("particles/"+particle_name+".p");
                Particle.behavior_lib.put(particle_name,behavior);

                //load images
                FileHandle image = Gdx.files.internal("particles/"+particle_name+".png");
                Particle.particle_atlas.addRegion(particle_name,new TextureRegion(new Texture(image)));

            }
        } catch(FileNotFoundException ex) { System.out.println(ex); }
    }

    public static void draw_all(SpriteBatch batch, float deltaTime) { //go through all active particles, update them, and draw them
        ArrayList<Particle> complete_list = new ArrayList<Particle>(); //list of particles to be removed

        for (Particle p : Particle.particle_list) {
            p.particle.setPosition(p.entity.getX(),p.entity.getY()); //first. update the particles

            p.particle.draw(batch,deltaTime);
            if (p.particle.isComplete()) { complete_list.add(p); } //if the particle effect has no more use
        }
        //remove all particles that arent doing anything
        for (Particle p : complete_list) { Particle.particle_list.remove(p); }
    }

}
