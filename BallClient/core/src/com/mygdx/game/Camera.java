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
    private Boolean isLocked = false;

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
            float cam_shift_speed = 3;
            float max_cam_dist = 45;

            this.cam.translate(cam_shift_speed*MathUtils.cos(Global.m_angle),cam_shift_speed*MathUtils.sin(Global.m_angle));

            this.cam.position.x = MathUtils.clamp(this.cam.position.x,x-max_cam_dist*Math.abs(MathUtils.cos(Global.m_angle)),x+max_cam_dist*Math.abs(MathUtils.cos(Global.m_angle)));
            this.cam.position.y = MathUtils.clamp(this.cam.position.y,y-max_cam_dist*Math.abs(MathUtils.sin(Global.m_angle)),y+max_cam_dist*Math.abs(MathUtils.sin(Global.m_angle)));
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
