package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class BallServerMain extends ApplicationAdapter {

	BallServer server;
	
	@Override
	public void create () {

		//Misc inits
		Entity.init_textures("texture_dimensions.txt");
		//Init server and such
		server = new BallServer(5000);
		server.start_server();

		//Thread that listens for connecting users
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					//keep waiting for new clients to show up
					while (true) {
						BallClientHandler client = new BallClientHandler(server.getServerSocket().accept());
						client.start_connection();
					}
				} catch(IOException ex) {
					System.out.println(ex);
				}
			}
		}).start(); //auto start thread
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//periodically send client position of all entities
		String entity_data = Entity.send_all();
		System.out.println(entity_data);
		if (entity_data != null) { BallClientHandler.broadcast(Global.MT_UPDATE,Entity.send_all()); } //broadcast only if there is something to broadcast

	}
	
	@Override
	public void dispose () {

	}
}
