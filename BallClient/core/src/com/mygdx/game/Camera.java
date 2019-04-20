package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class Camera {
    private OrthographicCamera cam;

    public Camera() {
        this.cam = new OrthographicCamera(400,400);
    }

    public void moveCam(Entity target) { //make camera follow a target entity
        float x = target.getX();
        float y = target.getY();

        this.cam.position.x = MathUtils.clamp(this.cam.position.x,x,x);
        this.cam.position.y = MathUtils.clamp(this.cam.position.y,y,y);

        this.updateCam();
    }

    public void updateCam() { this.cam.update(); }
    public OrthographicCamera getCam() { return this.cam; }

}
