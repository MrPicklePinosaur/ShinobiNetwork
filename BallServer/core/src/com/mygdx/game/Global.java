/*
 ______   __       ______   ______   ______   __
/\  ___\ /\ \     /\  __ \ /\  == \ /\  __ \ /\ \
\ \ \__ \\ \ \____\ \ \/\ \\ \  __< \ \  __ \\ \ \____
 \ \_____\\ \_____\\ \_____\\ \_____\\ \_\ \_\\ \_____\
  \/_____/ \/_____/ \/_____/ \/_____/ \/_/\/_/ \/_____/

 */

package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;

import java.util.HashSet;

import java.util.*;
import java.io.*;

class Global {

	public static Random rnd = new Random();
	public static Json json = new Json();

	public static World world;
	public static Game game;
	public static Map map;

	//Important vars
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 600;
	public static final float RESOLUTION = (float)SCREEN_HEIGHT/SCREEN_WIDTH;

	public static final int PPM = 100;
	public static final float deltatime = 1/60f;
	public static final float PLAYER_DAMPING = 50; //the amount of friction the player has with the floor

	//Collision filters
	public static final short BIT_STATIC = 2;
	public static final short BIT_PLAYER = 4;
	public static final short BIT_ENEMY = 8;
	public static final short BIT_PROJECTILE = 16;

	//Used for i/o
	private static int code = -1;

	public static int new_code() { //generates unique entity code
		Global.code+=1;
		return code;
	}

	public static void disposeGlobals() {
		Global.world.dispose();
	}
}

enum MT { //mt stands for messageTyoe
	//Message Types - output
	UPDATE, KILLENTITY, LOADMAP, SENDCHAT, BINDCAM,

	//Message Types (MT) - input
	USIN, CHATMSG, CMD
}

enum ET { //et sstands for entitiy type
	PLAYER, ENEMY, PROJECTILE, STATIC, WEAPON
}

enum COMMANDS {
	TELEPORT
}

enum TEAMTAG {
	RED, BLUE, SOLO
}

enum GAMETYPE {
	TDM, FFA
}

enum FIREPATTERN { //bullet pattern
	STRAIGHT, WAVE
}