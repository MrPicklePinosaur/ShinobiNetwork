package com.mygdx.game;

//Literally the sprite class in LibGDX but without the visual aspect
class AbstractSprite {

    private float x;
    private float y;
    private float rotation;
    private int width;
    private int height;

    public AbstractSprite(int width,int height) { //instead of taking in a texture, we just take in the DIMENSIONS
        this.x = 0;
        this.y = 0;
        this.rotation = 0;
        this.width = width;
        this.height = height;
    }

    //Getters
    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public float getRotation() { return this.rotation; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }

    //Setters
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    public void init_pos(float x, float y, float rotation) { //SHOULD ONLY EVERY BE CALLED ONCE, DO NOT USE THIS TO MOVE THE SPRITE< ONLY TO INIT IT
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

}
