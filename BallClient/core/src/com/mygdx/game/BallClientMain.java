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

	//screems
	GameScreen game_screen;
	MainmenuScreen mainmenu_screen;
	AwaitauthScreen awaitauth_screen;
	RetryconnectionScreen retryconnection_screen;
	InventoryScreen inventory_screen;
	OptionsScreen options_screen;
	LoginScreen login_screen;

	@Override
	public void create () {
		Thread.currentThread().setName("Main");
		Global.game = this;

		//Init calls
		Gdx.graphics.setWindowedMode(Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);
		AssetManager.load_all();
		Particle.load_particles("particle_lib.txt");
		AudioPlayer.load_sounds("sounds/");
		AudioPlayer.load_music("music/");

		this.retryconnection_screen = new RetryconnectionScreen();
		this.login_screen = new LoginScreen();
		this.awaitauth_screen = new AwaitauthScreen();

		if (!Global.game.attempt_connection(Global.server_ip, Global.server_port)) { //attempt to connect to server
			setScreen(retryconnection_screen); //if login fails, ask client if they want to try again
		} else { //otherwise go straight to login screen
			AudioPlayer.play_music();
			setScreen(login_screen);
		}
	}

	public void loadScreens() { //load the rest of the screens after creds has been authed
		//init screens
		this.mainmenu_screen = new MainmenuScreen();
		this.game_screen = new GameScreen();
		this.inventory_screen = new InventoryScreen();
		this.options_screen = new OptionsScreen();

		//init ui
		Global.chatlog = new ChatLog(game_screen.getStage());
		Global.camera = new Camera();
		setScreen(mainmenu_screen);
	}

	public boolean attempt_connection(String ip,int port) { //try to connect to server
		Global.server_socket = new BallClient(ip,port); //Init server
		return Global.server_socket.start_connection(); //return if connection was sucessful or not
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render(); //draw whatever the current screen is
	}
	
	@Override
	public void dispose () {
		//server_socket.close_connection(); //this line causes nullPointer on serverside for some reason
        Gdx.app.exit();
	}

}
