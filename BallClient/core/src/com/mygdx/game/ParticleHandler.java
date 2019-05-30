package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.FileHandler;

public class ParticleHandler {
    private static TextureAtlas particle_atlas = new TextureAtlas();
    private static LinkedList<ParticleEffect> particle_list = new LinkedList<ParticleEffect>();

    public static void load_particles() {
        FileHandle file_handle = Gdx.files.internal("particles/images");
        for (FileHandle file : file_handle.list()) {
            ParticleHandler.particle_atlas.addRegion("run_dust",new TextureRegion(new Texture(file)));
        }
    }

    public static ParticleEffect createParticle(String particle_name) {
        ParticleEffect new_particle = new ParticleEffect();
        new_particle.load(Gdx.files.internal("particles/behavior/"+particle_name+".p"),ParticleHandler.particle_atlas);
        new_particle.setPosition(1200,1200);
        new_particle.start();
        ParticleHandler.particle_list.add(new_particle);
        return new_particle;
    }

    public static void draw_all(SpriteBatch batch, float deltaTime) {
        for (ParticleEffect p : ParticleHandler.particle_list) {
            p.draw(batch,deltaTime);
        }
    }




}
