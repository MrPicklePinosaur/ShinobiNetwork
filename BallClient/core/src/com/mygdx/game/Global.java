package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class Global {

	public static int PPM = 100;

	//Message types (MT) - input
	public static final String MT_UPDATE = "MT_UPDATE";
	public static final String MT_KILLENTITY = "MT_KILLENTITY";
	public static final String MT_ASSIGNENTITY = "MT_ASSIGNENTITY";

	//Message types - output
	public static final int MT_USIN = 0;
	public static final int MT_CHATMSG = 1;
	public static final int MT_CMD = 2;


}