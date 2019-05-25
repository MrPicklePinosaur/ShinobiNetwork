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
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BallServerMain extends ApplicationAdapter {

	//heavy lifters
	Game game;
	BallServer server;
	Box2DDebugRenderer debugRenderer;
	OrthographicCamera cam; //TODO: WE SHOULD NOT BE USING THIS
	OrthogonalTiledMapRenderer tiledMapRenderer;

	@Override
	public void create () {
		//init assets
		Entity.init_textures("texture_dimensions.txt");
		AssetManager.load_all_json();
		//Map.loadAll("map_library.txt");

		//Player p = new Player("ninja_run.png",TEAMTAG.BLUE);
		//System.out.println(test.get("ninja"));
		//p.stats_from_json(test.get("ninja"));

		//Connect to database
		//Database.connect("database.db");
		World world = new World(new Vector2(0,0),true);
		world.setContactListener(new CollisionListener());
		//choose a map
		//current_map = Map.getMap("Mountain Temple");
		Map map = new Map("maps/mountain_temple.tmx");

		game = new Game(world,map);

		//init heavy lifres
		debugRenderer = new Box2DDebugRenderer();

		cam = new OrthographicCamera((float) 1400/Global.PPM,(float) 1400/Global.PPM);
		cam.zoom = 1.2f;
		cam.position.x = (float)1500/Global.PPM;
		cam.position.y = (float)1500/Global.PPM;
		cam.update();

		tiledMapRenderer = new OrthogonalTiledMapRenderer(game.getMap().getTiledMap(),(float) 1/Global.PPM);
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
		String entity_data = game.send_all();
		if (!entity_data.equals("")) { BallClientHandler.broadcast(MT.UPDATE,game.send_all()); } //broadcast only if there is something to broadcast

		//draw stuff (TESTING ONLY)
		tiledMapRenderer.render();
		debugRenderer.render(game.getWorld(),cam.combined);

		//update
		game.getWorld().step(Global.deltatime,6,2); //step physics simulation
		AssetManager.sweepBodies();
		AssetManager.moveBodies();

	}
	
	@Override
	public void dispose () {
		server.close_server();
		tiledMapRenderer.dispose();
		debugRenderer.dispose();
		Gdx.app.exit();
	}
}
