package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class Camera {
    OrthographicCamera cam;

    public Camera() {
        this.cam = new OrthographicCamera(400,400);
    }

    //TODO: problem: we dont know the entity that the client owns, so the cam might have some trouble following
    public void moveCam(Entity target) { //make camera follow a target entity
        float x = target.getX();
        float y = target.getY();

        this.cam.position.x = MathUtils.clamp(this.cam.position.x,x,x);
        this.cam.position.y = MathUtils.clamp(this.cam.position.y,y,y);
    }
}
