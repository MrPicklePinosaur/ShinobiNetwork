/*
 ______   __       ______   ______   ______   __
/\  ___\ /\ \     /\  __ \ /\  == \ /\  __ \ /\ \
\ \ \__ \\ \ \____\ \ \/\ \\ \  __< \ \  __ \\ \ \____
 \ \_____\\ \_____\\ \_____\\ \_____\\ \_\ \_\\ \_____\
  \/_____/ \/_____/ \/_____/ \/_____/ \/_/\/_/ \/_____/

 */

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Random;

class Global {

	public static Camera camera;

	//Scene 2d
	public static Stage stage;
	public static ChatLog chatlog;

	//Important vars
	public static BallClient server_socket;
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 600;
	public static final float RESOLUTION = (float)SCREEN_HEIGHT/SCREEN_WIDTH;
	public static int PPM = 100;

	public static final int SPRITESIZE = 40;
	public static final float WEAPONSCALE = 0.8f;

	public static float m_x = 0;
	public static float m_y = 0;
	public static float m_angle = 0;

	public static void updateInput() {
		Global.m_x = Gdx.input.getX();
		Global.m_y = Gdx.input.getY();
		Global.m_angle = -1*MathUtils.atan2(m_y-SCREEN_HEIGHT/2f,m_x-SCREEN_WIDTH/2f);
	}

}

enum MT {
	//Message types - output
	USIN, CHATMSG, CMD,

	//Message types (MT) - input
	UPDATEENTITY, KILLENTITY, LOADMAP, SENDCHAT, BINDCAM, UPDATEPARTICLE,
}

enum ET {
	PLAYER, ENEMY, PROJECTILE, STATIC, WEAPON
}
