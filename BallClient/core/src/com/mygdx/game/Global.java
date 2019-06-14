/*
 ______   __       ______   ______   ______   __
/\  ___\ /\ \     /\  __ \ /\  == \ /\  __ \ /\ \
\ \ \__ \\ \ \____\ \ \/\ \\ \  __< \ \  __ \\ \ \____
 \ \_____\\ \_____\\ \_____\\ \_____\\ \_\ \_\\ \_____\
  \/_____/ \/_____/ \/_____/ \/_____/ \/_/\/_/ \/_____/

 */

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;

import java.util.Random;

class Global {

	public static Skin skin = new Skin(Gdx.files.internal("gdx-skins/level-plane/skin/level-plane-ui.json"));
	public static TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("gdx-skins/level-plane/skin/level-plane-ui.atlas"));

	public static Label.LabelStyle labelStyle;

	static{
		skin.addRegions(atlas);
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.GRAY);
		pixmap.fill();
		skin.add("buttonColor",new Texture(pixmap));
		pixmap.setColor(Color.BLUE);
		pixmap.fill();
		skin.add("buttonOverColor",new Texture(pixmap));
		pixmap.setColor(Color.GREEN);
		pixmap.fill();
		skin.add("buttonDownColor",new Texture(pixmap));
		skin.add("defaultFont",new BitmapFont());
		skin.add("PixelFont",new BitmapFont(Gdx.files.internal("fonts/PixelFont.fnt")));
		skin.add("PixelFont_Small",new BitmapFont(Gdx.files.internal("fonts/PixelFont_Small.fnt")));

		Global.labelStyle = new Label.LabelStyle();
		BitmapFont pixelFont = Global.skin.getFont("PixelFont");
		labelStyle.font = pixelFont;
		labelStyle.fontColor = Color.valueOf("B5B5B5");
	}

	//helpers
	public static Random rnd = new Random();
	public static Json json = new Json();

	//important
	public static BallClientMain game;
	public static UserData user_data;
	public static ChatLog chatlog;
	public static Camera camera;

	//Important vars
	public static BallClient server_socket;
	public static String server_ip = "127.0.0.1";
	public static int server_port = 5000;

	//Graphic dimensions
	public static final int SCREEN_WIDTH = 1200;
	public static final int SCREEN_HEIGHT = 800;
	public static final int SPRITESIZE = 40;
	public static final float WEAPONSCALE = 0.8f;

	//input
	public static float m_x = 0;
	public static float m_y = 0;
	public static float m_angle = 0;

	public static void updateInput() {
		Global.m_x = Gdx.input.getX();
		Global.m_y = Gdx.input.getY();
		Global.m_angle = -1*MathUtils.atan2(m_y-SCREEN_HEIGHT/2f,m_x-SCREEN_WIDTH/2f);
	}

}

enum MT { //Message types for communication with server
	//Message types - output
	USIN, CHATMSG, CMD, RESPAWN,

	CHECKCREDS, REGISTER, STARTGAME, LEAVEGAME,

	//Message types (MT) - input
	UPDATEENTITY, KILLENTITY, LOADMAP, SENDCHAT, BINDCAM, UPDATEPARTICLE, CHOOSECLASS, UPDATEHP, GAMEOVER, UPDATELEADERBOARD, PLAYSOUND,

	CREDSACCEPTED, CREDSDENIED, REGISTERSUCCESS, REGISTERFAILED,
}

enum ET { //used to identify entity
	PLAYER, ENEMY, PROJECTILE, STATIC, WEAPON
}
