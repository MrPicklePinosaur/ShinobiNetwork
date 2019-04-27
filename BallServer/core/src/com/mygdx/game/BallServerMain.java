package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class BallServerMain extends ApplicationAdapter {

	//heavy lifters
	BallServer server;
	Map current_map;
	Box2DDebugRenderer debugRenderer;
	OrthographicCamera cam; //TODO: WE SHOULD NOT BE USING THIS
	OrthogonalTiledMapRenderer tiledMapRenderer;

	@Override
	public void create () {
		//init assets
		Entity.init_textures("texture_dimensions.txt");
		Global.world = new World(new Vector2(0,0),true);

		//Load map
		current_map = new Map("maps/mountain_temple.tmx");

		//init heavy lifres
		debugRenderer = new Box2DDebugRenderer();

		cam = new OrthographicCamera((float) 400/Global.PPM,(float) 400/Global.PPM);
		cam.zoom = 3f;
		cam.position.x = (float)500/Global.PPM;
		cam.position.y = (float)500/Global.PPM;
		cam.update();

		tiledMapRenderer = new OrthogonalTiledMapRenderer(current_map.getMap(),(float) 1/Global.PPM);
		tiledMapRenderer.setView(cam);

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
					System.out.println(ex+" aka, something went wrong when connecting the player.");
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
		if (!entity_data.equals("")) { BallClientHandler.broadcast(Global.MT_UPDATE,Entity.send_all()); } //broadcast only if there is something to broadcast

		//draw stuff (TESTING ONLY)
		tiledMapRenderer.render();
		debugRenderer.render(Global.world,cam.combined);

		//update
		Global.world.step(Global.deltatime,6,2); //step physics simulation
	}
	
	@Override
	public void dispose () {
		server.close_server();
	}
}
