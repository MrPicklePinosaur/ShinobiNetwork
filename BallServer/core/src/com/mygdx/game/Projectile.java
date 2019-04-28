package com.mygdx.game;

class Projectile extends Entity {

    Entity owner; //keeps track of who created the projectile

    public Projectile(String file_path) {
        super(file_path);
    }


}
