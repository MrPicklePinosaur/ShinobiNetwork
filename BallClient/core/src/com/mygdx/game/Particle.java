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
    private static LinkedList<ParticleEffect> particle_list = new LinkedList<ParticleEffect>();

    private String filepath;
    public Particle(String filepath) {
        this.filepath = filepath;
    }

    public static void load_particles(String lib_firepath) {
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

    public static ParticleEffect createParticle(String particle_name, Vector2 pos) {
        ParticleEffect new_particle = new ParticleEffect();
        new_particle.load(Particle.behavior_lib.get(particle_name),Particle.particle_atlas);
        new_particle.setPosition(pos.x,pos.y);
        new_particle.start();
        Particle.particle_list.add(new_particle);
        return new_particle;
    }

    public static void draw_all(SpriteBatch batch, float deltaTime) {
        for (ParticleEffect p : Particle.particle_list) {
            p.draw(batch,deltaTime);
        }
    }
}
