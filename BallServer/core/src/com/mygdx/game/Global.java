package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.World;

import java.util.HashSet;

import java.util.*;
import java.io.*;

public class Global {

	//Important vars
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 600;
	public static final float RESOLUTION = (float)SCREEN_HEIGHT/SCREEN_WIDTH;
	public static World world;
	public static final int PPM = 100;

	//Message Types (MT) - input
	public static final String MT_USIN = "MT_USIN";
	public static final String MT_CHATMSG = "MT_CHATMSG";
	public static final String MT_CMD = "MT_CMD";

	//Message Types - output
	public static final int MT_UPDATE = 0;
	public static final int MT_KILLENTITY = 1;
	public static final int MT_ASSIGNENTITY = 2;

	//Used for i/o
	private static int code = -1;

	public static int new_code() { //generates unique entity code
		Global.code+=1;
		return code;
	}

}