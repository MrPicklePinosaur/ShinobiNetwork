package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Random;

class Global {

	//Important vars
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 600;
	public static final float RESOLUTION = (float)SCREEN_HEIGHT/SCREEN_WIDTH;
	public static int PPM = 100;

	public static final int SPRITESIZE = 32;

	public static float m_x;
	public static float m_y;
	public static float m_angle;

	public static void updateInput() {
		Global.m_x = Gdx.input.getX();
		Global.m_y = Gdx.input.getY();
		Global.m_angle = MathUtils.atan2(m_y,m_x);
	}

}

enum MT {
	//Message types - output
	USIN, CHATMSG, CMD,

	//Message types (MT) - input
	UPDATEPLAYER, KILLENTITY, ASSIGNENTITY, LOADMAP
}