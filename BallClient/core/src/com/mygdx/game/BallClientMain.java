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
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class BallClientMain extends ApplicationAdapter {

	//heavy lifters
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	InputMultiplexer inputMultiplexer;
	InputHandler input_handler;
	//TiledMapRenderer tiledMapRenderer; //EXTREMELY USEFUL LATER

	Sprite background;

	//UI stuff

	@Override
	public void create () {
		//Init calls
		AssetManager.loadAnimations("spritesheet_lib.txt");
		Gdx.graphics.setWindowedMode(Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);

		Particle.load_particles("particle_lib.txt");

		//init ui stuff
		Global.stage = new Stage();
		Global.chatlog = new ChatLog();

		//init variables
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		//shapeRenderer.setProjectionMatrix(Global.camera.getCam().combined);
		Global.camera = new Camera();

		//input
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(Global.stage);
		input_handler = new InputHandler();
		inputMultiplexer.addProcessor(input_handler);
		Gdx.input.setInputProcessor(inputMultiplexer);

		//init sprites (REMOVE LATER)
		background = new Sprite(new Texture("mountain_temple.png"));

		//Init server
		Global.server_socket = new BallClient("127.0.0.1",5000);
		Thread.currentThread().setName("Main");
		if (Global.server_socket.start_connection() == false) {
			 //client goes back to main screen
			Gdx.app.exit(); //for now the game just closes
		}

		Global.server_socket.send_msg(MT.CHECKCREDS,"daniel,password");

		Gdx.gl.glEnable(GL20.GL_BLEND);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (Global.server_socket.isGameInProgress()) {
			batch.begin();
			background.draw(batch);
			Entity.drawAll(batch);
			Particle.draw_all(batch, Gdx.graphics.getDeltaTime());
			batch.end();

			//draw UI
			Global.stage.act(Gdx.graphics.getDeltaTime());
			Global.stage.draw();

			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			Global.chatlog.drawLog(shapeRenderer);
			shapeRenderer.end();

			//update stuff
			float deltaTime = Gdx.graphics.getDeltaTime();
			Global.updateInput();
			input_handler.sendMouse();
			input_handler.handleInput();
			Entity.stepFrameAll(deltaTime);

			Global.camera.moveCam();
			batch.setProjectionMatrix(Global.camera.getCam().combined);
			Global.camera.updateCam();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		//server_socket.close_connection(); //this line causes nullPointer on serverside for some reason
        Gdx.app.exit();
	}


}
