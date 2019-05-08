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
	//TiledMapRenderer tiledMapRenderer; //EXTREMELY USEFUL LATER

	BallClient server_socket;

	Sprite background;

	@Override
	public void create () {
		//Init calls
		AssetManager.loadAnimations("spritesheet_lib.txt");
		Gdx.graphics.setWindowedMode(Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);

		//init variables
		batch = new SpriteBatch();
		camera = new Camera();

		//init sprites (REMOVE LATER)
		background = new Sprite(new Texture("mountain_temple.png"));

		//Init server
		server_socket = new BallClient("127.0.0.1",5000);
		server_socket.start_connection();
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
		sendKeyboard();
		Entity.stepFrameAll(deltaTime);

		if (Entity.getClientEntity() != null) { camera.moveCam(Entity.getClientEntity()); }
		batch.setProjectionMatrix(camera.getCam().combined);
		camera.updateCam();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		//server_socket.close_connection(); //this line causes nullPointer on serverside for some reason

	}

	public void sendKeyboard() { //takes in user input and sends to server
		String msg = "";
		//TODO: send mouse position as well
		//msg+=(",MOUSE_POS-"+Global.m_x+"-"+Global.m_y);
		msg+=(",MOUSE_ANGLE:"+Global.m_angle);
		if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) { msg+=(",Key_Q"); }
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) { msg+=(",MOUSE_LEFT"); } //TODO: possibly do one for key click
		if (Gdx.input.isKeyPressed(Input.Keys.W)) { msg+=(",Key_W"); }
		if (Gdx.input.isKeyPressed(Input.Keys.S)) { msg+=(",Key_S"); }
		if (Gdx.input.isKeyPressed(Input.Keys.A)) { msg+=(",Key_A"); }
		if (Gdx.input.isKeyPressed(Input.Keys.D)) { msg+=(",Key_D"); }
		if (msg.equals("")) return; //if there isnt any input, don't send a message
		msg = msg.substring(1); //get rid of extra comma in front

		server_socket.send_msg(MT.USIN,msg);
	}
}
