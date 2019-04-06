package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.*;
import java.io.*;
import java.net.*;

public class BallClientMain extends ApplicationAdapter {

	//heavy lifters
	SpriteBatch batch;
	Texture t = new Texture("cube.png");

	BallClient client;

	@Override
	public void create () {
		batch = new SpriteBatch();

		client = new BallClient("127.0.0.1",5000);
		client.start_connection();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
	

		batch.end();

		handleInput();
	}
	
	@Override
	public void dispose () {

	}

	public void handleInput() { //takes in user input and sends to server
		if (Gdx.input.isKeyPressed(Input.Keys.W)) { client.out_packer(Global.MT_USIN,"Key_W"); }
		else if (Gdx.input.isKeyPressed(Input.Keys.S)) { client.out_packer(Global.MT_USIN,"Key_S"); }
		else if (Gdx.input.isKeyPressed(Input.Keys.A)) { client.out_packer(Global.MT_USIN,"Key_A"); }
		else if (Gdx.input.isKeyPressed(Input.Keys.D)) { client.out_packer(Global.MT_USIN,"Key_D"); }
	}
}
