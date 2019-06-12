/*
 ______   ______   __    __   ______   ______   ______
/\  ___\ /\  __ \ /\ "-./  \ /\  ___\ /\  == \ /\  __ \
\ \ \____\ \  __ \\ \ \-./\ \\ \  __\ \ \  __< \ \  __ \
 \ \_____\\ \_\ \_\\ \_\ \ \_\\ \_____\\ \_\ \_\\ \_\ \_\
  \/_____/ \/_/\/_/ \/_/  \/_/ \/_____/ \/_/ /_/ \/_/\/_/

 */

package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

public class Camera {
    private static final float max_cam_dist = 40;
    private static final float cam_shift_speed = 1.5f;

    private OrthographicCamera cam;
    private Boolean isLocked = true;

    private float cam_bind_x;
    private float cam_bind_y;

    public Camera() {
        this.cam = new OrthographicCamera(600,400);
        this.cam_bind_x = 0; this.cam_bind_y = 0;
    }

    public void moveCam() { //make camera follow a target entity
        float x = this.cam_bind_x;
        float y = this.cam_bind_y;

        if (this.isLocked == false) {
            //shift camera in direction of mouse
            this.cam.position.x += Camera.cam_shift_speed * MathUtils.cos(Global.m_angle);
            this.cam.position.y += Camera.cam_shift_speed * MathUtils.sin(Global.m_angle);

            float bound_x = Math.abs(Camera.max_cam_dist * MathUtils.cos(Global.m_angle));
            float bound_y = Math.abs(Camera.max_cam_dist * MathUtils.sin(Global.m_angle));

            this.cam.position.x = MathUtils.clamp(this.cam.position.x, x - bound_x, x + bound_x);
            this.cam.position.y = MathUtils.clamp(this.cam.position.y, y - bound_y, y + bound_y);
        } else {
            this.cam.position.x = x;
            this.cam.position.y = y;
        }

        this.updateCam();

    }

    public void toggleCameraLock() { this.isLocked = !this.isLocked; }
    public void updateCam() { this.cam.update(); }
    public void bindPos (Vector2 pos) {
        this.cam_bind_x = pos.x;
        this.cam_bind_y = pos.y;
    }
    public OrthographicCamera getCam() { return this.cam; }

}
