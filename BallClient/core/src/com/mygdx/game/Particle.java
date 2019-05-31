package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.FileHandler;

public class Particle {
    private static TextureAtlas particle_atlas = new TextureAtlas();
    private static LinkedList<ParticleEffect> particle_list = new LinkedList<ParticleEffect>();

    private String filepath;
    public Particle(String filepath) {
        this.filepath = filepath;
    }

    public static void load_particles(String lib_filepath) {
        try {
            Scanner fileReader = new Scanner(new BufferedReader(new FileReader(lib_filepath)));
            while(fileReader.hasNext()) {

            }
        } catch(FileNotFoundException ex) { System.out.println(ex); }
    }

    public static ParticleEffect createParticle(String particle_name) {
        ParticleEffect new_particle = new ParticleEffect();
        new_particle.load(Gdx.files.internal("particles/behavior/"+particle_name+".p"),Particle.particle_atlas);
        new_particle.setPosition(1200,1200);
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
