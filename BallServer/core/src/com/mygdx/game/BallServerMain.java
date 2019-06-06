/*
 __    __   ______   __   __   __
/\ "-./  \ /\  __ \ /\ \ /\ "-.\ \
\ \ \-./\ \\ \  __ \\ \ \\ \ \-.  \
 \ \_\ \ \_\\ \_\ \_\\ \_\\ \_\\"\_\
  \/_/  \/_/ \/_/\/_/ \/_/ \/_/ \/_/

 */

package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.io.IOException;

public class BallServerMain extends ApplicationAdapter {

	//heavy lifters
	BallServer server;
	Box2DDebugRenderer debugRenderer;
	OrthographicCamera cam; //TODO: WE SHOULD NOT BE USING THIS
	OrthogonalTiledMapRenderer tiledMapRenderer;

	@Override
	public void create () {
		//init assets
		AssetManager.load_all_json();
		//Map.loadAll("map_library.txt");

		//Connect to database
		//Database.connect("database.db");

		Global.game = new KOTHGame();
		Global.world = new World(new Vector2(0,0),true);
		Global.world.setContactListener(new CollisionListener());

		//choose a map
		//current_map = Map.getMap("Mountain Temple");
		Global.map = new Map("maps/mountain_temple.tmx");


		//init heavy lifres
		debugRenderer = new Box2DDebugRenderer();

		cam = new OrthographicCamera((float) 1400/Global.PPM,(float) 1400/Global.PPM);
		cam.zoom = 1.2f;
		cam.position.x = (float)1500/Global.PPM;
		cam.position.y = (float)1500/Global.PPM;
		cam.update();

		tiledMapRenderer = new OrthogonalTiledMapRenderer(Global.map.getTiledMap(),(float) 1/Global.PPM);
		tiledMapRenderer.setView(cam);

		//Init server and such
		server = new BallServer(5000);
		server.start_server();

		Thread.currentThread().setName("Main");

		//Thread that listens for connecting users
		new Thread(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setName("AwaitClient");
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
		Entity.send_all();
		Particle.send_particles();

		//draw stuff (TESTING ONLY)
		tiledMapRenderer.render();
		debugRenderer.render(Global.world,cam.combined);

		//update
		Ability.updateAll(Global.deltatime);
		ActiveEffect.updateAll(Global.deltatime);
		Player.updateAll(Global.deltatime);
		Projectile.updateAll();
		Global.game.checkObjective();
		Global.world.step(Global.deltatime,6,2); //step physics simulation
		AssetManager.sweepBodies();
		AssetManager.moveBodies();

	}
	
	@Override
	public void dispose () {
		server.close_server();
		tiledMapRenderer.dispose();
		debugRenderer.dispose();
		Global.disposeGlobals();
		Gdx.app.exit();
	}
}
