/*
 __    __   ______   __   __   __
/\ "-./  \ /\  __ \ /\ \ /\ "-.\ \
\ \ \-./\ \\ \  __ \\ \ \\ \ \-.  \
 \ \_\ \ \_\\ \_\ \_\\ \_\\ \_\\"\_\
  \/_/  \/_/ \/_/\/_/ \/_/ \/_/ \/_/

 */

package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;

public class BallClientMain extends Game {

	GameScreen game_screen;
	MainmenuScreen mainmenu_screen;
	AwaitauthScreen awaitauth_screen;
	RetryconnectionScreen retryconnection_screen;
	InventoryScreen inventory_screen;
	LoginScreen login_screen;

	@Override
	public void create () {
		Thread.currentThread().setName("Main");
		Global.game = this;

		//Init calls
		Gdx.graphics.setWindowedMode(Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);
		AssetManager.load_all();
		Particle.load_particles("particle_lib.txt");

		this.retryconnection_screen = new RetryconnectionScreen();
		this.login_screen = new LoginScreen();
		this.awaitauth_screen = new AwaitauthScreen();

		if (!Global.game.attempt_connection(Global.server_ip, Global.server_port)) { //attempt to connect to server
			setScreen(retryconnection_screen); //if login fails, ask client if they want to try again
		} else { //otherwise go straight to login screen
			setScreen(login_screen);
		}
	}

	public void loadScreens() {
		this.mainmenu_screen = new MainmenuScreen();
		this.game_screen = new GameScreen();
		this.inventory_screen = new InventoryScreen();
		Global.chatlog = new ChatLog(game_screen.getStage());
		Global.camera = new Camera();
		setScreen(mainmenu_screen);
	}

	public boolean attempt_connection(String ip,int port) {
		Global.server_socket = new BallClient(ip,port); //Init server
		return Global.server_socket.start_connection();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render();
	}
	
	@Override
	public void dispose () {
		//server_socket.close_connection(); //this line causes nullPointer on serverside for some reason
        Gdx.app.exit();
	}


}
