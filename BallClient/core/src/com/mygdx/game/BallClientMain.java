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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class BallClientMain extends ApplicationAdapter {

	//heavy lifters
	SpriteBatch batch;
	Camera camera;
	InputHandler input_handler;
	//TiledMapRenderer tiledMapRenderer; //EXTREMELY USEFUL LATER

	Sprite background;

	@Override
	public void create () {
		//Init calls
		AssetManager.loadAnimations("spritesheet_lib.txt");
		Gdx.graphics.setWindowedMode(Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);

		//init variables
		batch = new SpriteBatch();
		camera = new Camera();
		input_handler = new InputHandler();
		Gdx.input.setInputProcessor(input_handler);

		//init sprites (REMOVE LATER)
		background = new Sprite(new Texture("mountain_temple.png"));

		//Init server

		Global.server_socket = new BallClient("127.0.0.1",5000);
		Global.server_socket.start_connection();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		background.draw(batch);
		Entity.drawAll(batch);
		batch.end();

		//update stuff
		float deltaTime = Gdx.graphics.getDeltaTime();
		Global.updateInput();
		input_handler.sendMouse();
		input_handler.handleInput();
		Entity.stepFrameAll(deltaTime);

		if (Entity.getClientEntity() != null) { camera.moveCam(Entity.getClientEntity()); }
		batch.setProjectionMatrix(camera.getCam().combined);
		camera.updateCam();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		//server_socket.close_connection(); //this line causes nullPointer on serverside for some reason
        Gdx.app.exit();
	}


}
