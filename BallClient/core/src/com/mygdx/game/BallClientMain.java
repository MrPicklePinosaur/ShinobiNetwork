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
	Camera camera;
	BallClient server_socket;

	//vars
	Sprite background;
	Entity client_entity;

	@Override
	public void create () {
		Entity.init_textures("texture_lib.txt");

		batch = new SpriteBatch();
		camera = new Camera();

		background = new Sprite(new Texture("background.png"));

		server_socket = new BallClient("127.0.0.1",5000);
		server_socket.start_connection();

		client_entity = server_socket.getClientEntity();
	}

	@Override
	public void render () {

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		background.draw(batch);
		Entity.draw_all(batch);
		batch.end();

		handleInput();

		camera.moveCam(client_entity);
		batch.setProjectionMatrix(camera.getCam().combined);
		camera.updateCam();
	}
	
	@Override
	public void dispose () {

	}

	public void handleInput() { //takes in user input and sends to server
		String msg = "";
		if (Gdx.input.isKeyPressed(Input.Keys.W)) { msg+=(",Key_W"); }
		if (Gdx.input.isKeyPressed(Input.Keys.S)) { msg+=(",Key_S"); }
		if (Gdx.input.isKeyPressed(Input.Keys.A)) { msg+=(",Key_A"); }
		if (Gdx.input.isKeyPressed(Input.Keys.D)) { msg+=(",Key_D"); }
		if (msg.equals("")) return; //if there isnt any input, don't send a message
		msg = msg.substring(1); //get rid of extra comma in front

		server_socket.send_msg(Global.MT_USIN,msg);
	}
}
