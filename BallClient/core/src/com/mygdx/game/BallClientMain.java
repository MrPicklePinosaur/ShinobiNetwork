package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.*;
import java.io.*;
import java.net.*;

public class BallClientMain extends ApplicationAdapter {
	BallClient client;

	@Override
	public void create () {
		client = new BallClient("127.0.0.1",5000);
		client.start_connection();
		client.send_msg("HELLO SERVER");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		handleInput();
	}
	
	@Override
	public void dispose () {

	}

	public void handleInput() { //takes in user input and sends to server
		if (Gdx.input.isKeyPressed(Input.Keys.W)) { client.send_msg(Global.MT_USIN,"Key_W"); }
		else if (Gdx.input.isKeyPressed(Input.Keys.S)) { client.send_msg(Global.MT_USIN,"Key_S"); }
		else if (Gdx.input.isKeyPressed(Input.Keys.A)) { client.send_msg(Global.MT_USIN,"Key_A"); }
		else if (Gdx.input.isKeyPressed(Input.Keys.D)) { client.send_msg(Global.MT_USIN,"Key_D"); }
	}
}
