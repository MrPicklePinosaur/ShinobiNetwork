/*
 ______   __       ______   ______   ______   __
/\  ___\ /\ \     /\  __ \ /\  == \ /\  __ \ /\ \
\ \ \__ \\ \ \____\ \ \/\ \\ \  __< \ \  __ \\ \ \____
 \ \_____\\ \_____\\ \_____\\ \_____\\ \_\ \_\\ \_____\
  \/_____/ \/_____/ \/_____/ \/_____/ \/_/\/_/ \/_____/

 */

package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;

import java.util.*;

class Global {

	public static Random rnd = new Random();
	public static Json json = new Json();

	public static World world;
	public static Game game;
	public static GameMap map;
	public static Database db;

	//Important vars
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 600;
	public static final float RESOLUTION = (float)SCREEN_HEIGHT/SCREEN_WIDTH;

	public static final int PPM = 100;
	public static final float deltatime = 1/60f;
	public static final float PLAYER_DAMPING = 50; //the amount of friction the player has with the floor
	public static final int SPRITESIZE = 40;

	//Collision filters
	public static final short BIT_STATIC = 2;
	public static final short BIT_PLAYER = 4;
	public static final short BIT_ENEMY = 8;
	public static final short BIT_PROJECTILE = 16;
	public static final short BIT_REDSTATIC = 32;
	public static final short BIT_BLUESTATIC = 64;

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
	UPDATEENTITY, KILLENTITY, LOADMAP, SENDCHAT, BINDCAM, UPDATEPARTICLE, CHOOSECLASS, UPDATEHP, GAMEOVER, UPDATELEADERBOARD,

	CREDSACCEPTED, CREDSDENIED, REGISTERSUCCESS, REGISTERFAILED,

	//Message Types (MT) - input
	USIN, CHATMSG, CMD, RESPAWN,

	CHECKCREDS, REGISTER, STARTGAME, LEAVEGAME
}

enum ET { //et sstands for entitiy type
	PLAYER, ENEMY, PROJECTILE, STATIC, WEAPON
}

enum COMMANDS {
	TELEPORT, HELP, SPEEDY
}

enum TEAMTAG {
	RED, BLUE, SOLO
}

enum FIREPATTERN { //bullet pattern
	STRAIGHT, WAVE
}
