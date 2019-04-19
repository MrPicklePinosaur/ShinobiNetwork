package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class Global {

	//Message types (MT) - input
	public static String MT_UPDATE = "MT_UPDATE";
	public static String MT_NEWENTITY = "MT_NEWENTITY";
	public static String MT_KILLENTITY = "MT_KILLENTITY";

	//Message types - output
	public static int MT_USIN = 0;
	public static int MT_CHATMSG = 1;
	public static int MT_CMD = 2;


}