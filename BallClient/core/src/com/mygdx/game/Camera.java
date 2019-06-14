/* Shinobi Network
 ______   ______   __    __   ______   ______   ______
/\  ___\ /\  __ \ /\ "-./  \ /\  ___\ /\  == \ /\  __ \
\ \ \____\ \  __ \\ \ \-./\ \\ \  __\ \ \  __< \ \  __ \
 \ \_____\\ \_\ \_\\ \_\ \ \_\\ \_____\\ \_\ \_\\ \_\ \_\
  \/_____/ \/_/\/_/ \/_/  \/_/ \/_____/ \/_/ /_/ \/_/\/_/

    Smooth tracking of player and cam offset based on mouse postions
 */

package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;

public class Camera {

    private OrthographicCamera cam;
    private Boolean isLocked = false;

    private float cam_bind_x;
    private float cam_bind_y;

    public Camera() {
        this.cam = new OrthographicCamera(600,400);
        cam.zoom = 0.9f;
        cam_bind_x = 0; this.cam_bind_y = 0;
        cam.update();
    }

    public void moveCam() { //make camera follow a target entity
        float x = this.cam_bind_x; //current pos the camera is binded to
        float y = this.cam_bind_y;

        //this amazing camera code is from: https://stackoverflow.com/questions/24047172/libgdx-camera-smooth-translation
        Vector3 target = new Vector3(x+MathUtils.cos(Global.m_angle)*20,y+MathUtils.sin(Global.m_angle)*20,0);
        final float speed = 0.1f, ispeed=1.0f-speed;

        Vector3 cameraPosition = cam.position;
        cameraPosition.scl(ispeed);
        target.scl(speed);
        cameraPosition.add(target);
        cam.position.set(cameraPosition);

        this.updateCam();

    }

    //setters
    public void updateCam() { this.cam.update(); }
    public void bindPos (Vector2 pos) {
        this.cam_bind_x = pos.x;
        this.cam_bind_y = pos.y;
    }

    //getters
    public OrthographicCamera getCam() { return this.cam; }

}
