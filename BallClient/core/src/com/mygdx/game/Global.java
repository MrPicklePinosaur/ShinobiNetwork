package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

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

}

enum MT {
	//Message types - output
	USIN, CHATMSG, CMD,

	//Message types (MT) - input
	UPDATE, KILLENTITY, ASSIGNENTITY, LOADMAP
}