package com.mygdx.game;

import java.util.HashSet;

import java.util.*;
import java.io.*;

public class Global {

	//Message Types (MT) - input
	public static String MT_USIN = "MT_USIN";
	public static String MT_CHATMSG = "MT_CHATMSG"; 
	public static String MT_CMD = "MT_CMD";

	//Message Types - output
	public static int MT_UPDATE = 0;
	public static int MT_NEWENTITY = 1;
	public static int MT_KILLENTITY = 2;

	//Used for i/o
	private static int code = -1;

	public static int new_code() { //generates unique entity code
		Global.code+=1;
		return code;
	}

}