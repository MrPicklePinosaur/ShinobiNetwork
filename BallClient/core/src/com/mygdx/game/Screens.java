package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

class MainmenuScreen implements Screen {

    private Stage stage;

    public MainmenuScreen() {
        Skin skin = new Skin(Gdx.files.internal("gdx-skins/level-plane/skin/level-plane-ui.json"));

        TextButton play_button = new TextButton("Play",skin);
        play_button.setPosition(300,500);
        play_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                Global.game.setScreen(Global.game.game_screen);
                Global.server_socket.send_msg(MT.STARTGAME,"");
            }
        });

        TextButton inventory_button = new TextButton("Inventory",skin);
        inventory_button.setPosition(300,400);
        inventory_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                Global.game.setScreen(Global.game.inventory_screen);
            }
        });

        TextButton quit_button = new TextButton("Exit Game",skin);
        quit_button.setPosition(300,300);

        this.stage = new Stage();
        stage.addActor(play_button);
        stage.addActor(inventory_button);
        stage.addActor(quit_button);
    }

    @Override public void render(float delta) {
        this.stage.act(delta);
        this.stage.draw();
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }

    @Override public void hide() { }

    @Override public void dispose() { }

    @Override public void resize(int width,int height) { }

    @Override public void pause() { }

    @Override public void resume() { }

    public Stage getStage() { return this.stage; }
}

class GameScreen implements Screen {
    //init sprites (REMOVE LATER)
    static Sprite background = new Sprite(new Texture("mountain_temple.png"));

    private Stage stage;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private InputMultiplexer inputMultiplexer;
    private InputHandler input_handler;

    public GameScreen() {
        this.stage = new Stage();
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        //Gdx.gl.glEnable(GL20.GL_BLEND);

        this.inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        this.input_handler = new InputHandler();
        inputMultiplexer.addProcessor(this.input_handler);
    }

    @Override public void render(float delta) {
        batch.begin();
        background.draw(batch);
        Entity.drawAll(batch);
        Particle.draw_all(batch, Gdx.graphics.getDeltaTime());
        batch.end();

        //draw UI
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Global.chatlog.drawLog(shapeRenderer);
        shapeRenderer.end();

        //update stuff
        float deltaTime = Gdx.graphics.getDeltaTime();
        Global.updateInput();
        this.input_handler.sendMouse();
        this.input_handler.handleInput();
        Entity.stepFrameAll(deltaTime);

        Global.camera.moveCam();
        batch.setProjectionMatrix(Global.camera.getCam().combined);
        Global.camera.updateCam();
    }

    @Override public void show() { Gdx.input.setInputProcessor(inputMultiplexer); }

    @Override public void hide() { }

    @Override public void dispose() { }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }
}

class ConnectingScreen implements Screen {

    public ConnectingScreen() {

    }

    @Override public void render(float delta) {

    }

    @Override public void show() { }

    @Override public void hide() { }

    @Override public void dispose() { }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }

}

class InventoryScreen implements Screen {

    private Stage stage;

    public InventoryScreen() {
        Skin skin = new Skin(Gdx.files.internal("gdx-skins/level-plane/skin/level-plane-ui.json"));
        Texture empty_slot = new Texture(Gdx.files.internal("empty_slot.png"));

        this.stage = new Stage();

        //INVENTORY
        //some basic settings for the table
        Table inventory_grid = new Table();
        inventory_grid.setBounds(0,0,Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);
        inventory_grid.setDebug(true);
        inventory_grid.setFillParent(true);
        inventory_grid.pad(100);

        //populate the table with the contents of the user's inventory
        ArrayList<String> inv = new ArrayList<String>();
        for (String i : Global.user_data.getInventory()) { inv.add(i); }

        for (int j = 0; j < 6; j++) { //6 rows
            for (int i = 0; i < 4; i++) { //4 columns
                Stack stack = new Stack(); //used to overlay images
                Image empty_slot_img = new Image(empty_slot);
                stack.add(empty_slot_img);
                if (inv.size() > 0) { //go through client's inv list and draw them
                    String item_path = inv.get(0)+".png";
                    Image item = new Image(AssetManager.getSpritesheet(item_path));
                    inv.remove(0);
                    stack.add(item);
                }
                empty_slot_img.setScaling(Scaling.fit);
                inventory_grid.add(stack).pad(10);
            }
            inventory_grid.row(); //move down a row
        }


        //BUTTONS
        //ImageButton backButton = new ImageButton();

        //stage.addActor(backButton);
        stage.addActor(inventory_grid);
    }

    @Override public void render(float delta) {
        stage.act(delta);

        stage.draw();
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }

    @Override public void hide() { }

    @Override public void dispose() { }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }
}