package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.*;

//this was helpful https://github.com/libgdx/libgdx/wiki/2D-Animation

public class SpriteSheet {

    private Texture spritesheet;
    private float frameTime = 0.025f;
    private float stateTime; //tracks which frame of animation we are on
    private Animation<TextureRegion> animation;


    public SpriteSheet(Texture spritesheet) {
        this.spritesheet = spritesheet;
        this.stateTime = 0;

        int NUM_COLS = this.spritesheet.getWidth()/Global.SPRITESIZE;
        int NUM_ROWS = this.spritesheet.getHeight()/Global.SPRITESIZE;

        TextureRegion[][] raw = TextureRegion.split(this.spritesheet,Global.SPRITESIZE,Global.SPRITESIZE);
        TextureRegion[] frames = new TextureRegion[NUM_COLS*NUM_ROWS];
        //push all raw data into a 1D array
        int index = 0;
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                frames[index++] = raw[i][j];
            }
        }
        this.animation = new Animation<TextureRegion>(this.frameTime,frames);
    }
}
