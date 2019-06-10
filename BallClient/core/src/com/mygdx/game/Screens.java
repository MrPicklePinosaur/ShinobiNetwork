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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
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
        quit_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                Gdx.app.exit();
            }
        });

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
    private Table inventory_grid;
    public InventoryScreen() {
        Skin skin = new Skin(Gdx.files.internal("gdx-skins/level-plane/skin/level-plane-ui.json"));

        this.stage = new Stage();

        //INVENTORY
        //some basic settings for the table
        this.inventory_grid = new Table();
        inventory_grid.setBounds(0,0,Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);
        inventory_grid.setDebug(true);
        inventory_grid.setFillParent(true);
        inventory_grid.pad(100);
        refreshInventory();

        //BUTTONS
        ImageButton backButton = new ImageButton(new TextureRegionDrawable(AssetManager.getUIImage("back")));
        backButton.setPosition(20,200);
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                Global.game.setScreen(Global.game.mainmenu_screen);
            }
        });

        Table tabs = new Table();
        TextButton allItems_button = new TextButton("All items",skin);
        allItems_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {

            }
        });
        TextButton ninjaItems_button = new TextButton("Ninja",skin);
        ninjaItems_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {

            }
        });
        TextButton archerItems_button = new TextButton("Archer",skin);
        archerItems_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {

            }
        });
        TextButton warriorItems_button = new TextButton("Warrior",skin);
        warriorItems_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {

            }
        });
        TextButton wizardItems_button = new TextButton("Wizard",skin);
        wizardItems_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {

            }
        });

        tabs.add(wizardItems_button);
        tabs.add(warriorItems_button);
        tabs.add(archerItems_button);
        tabs.add(ninjaItems_button);
        tabs.add(allItems_button);
        tabs.setPosition(150,300);
        tabs.setTransform(true);
        tabs.rotateBy(90);

        stage.addActor(backButton);
        stage.addActor(inventory_grid);
        stage.addActor(tabs);
    }

    @Override public void render(float delta) {
        stage.act(delta);

        stage.draw();
    }

    public void refreshInventory() {
        //TODO: when refreshing inventory, actually ask the server for a refresh
        Texture empty_slot = AssetManager.getUIImage("empty_slot");
        this.inventory_grid.clearChildren();
        //populate the table with the contents of the user's inventory
        ArrayList<String> inv = new ArrayList<String>();
        for (String i : Global.user_data.getInventory()) { inv.add(i); }

        for (int j = 0; j < 6; j++) { //6 rows
            for (int i = 0; i < 4; i++) { //4 columns
                Stack stack = new Stack(); //used to overlay images
                Image empty_slot_img = new Image(empty_slot);
                stack.add(empty_slot_img);
                if (inv.size() > 0) { //go through client's inv list and draw them
                    String item_path = inv.get(0);
                    Image item = new Image(AssetManager.getSpritesheet(item_path));
                    inv.remove(0);
                    stack.add(item);
                }
                empty_slot_img.setScaling(Scaling.fit);
                this.inventory_grid.add(stack).pad(10);
            }
            this.inventory_grid.row(); //move down a row
        }
    }

    @Override public void show() {
        refreshInventory();
        Gdx.input.setInputProcessor(stage);
    }

    @Override public void hide() { }

    @Override public void dispose() { }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }
}

class LoginScreen implements Screen {

    private Stage stage;
    private TextField username_field;
    private TextField password_field;

    public LoginScreen() {
        Skin skin = new Skin(Gdx.files.internal("gdx-skins/level-plane/skin/level-plane-ui.json"));
        stage = new Stage();

        this.username_field = new TextField("",skin);
        username_field.setMessageText("Username"); //displays when box is empty
        username_field.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                //if enter is pressed, submit form
                if (c == '\r') { submit_creds(username_field.getText(),password_field.getText()); }
            }
        });

        this.password_field = new TextField("",skin);
        password_field.setMessageText("Password");
        password_field.setPasswordMode(true);
        password_field.setPasswordCharacter('*');
        password_field.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                //if enter is pressed, submit form
                if (c == '\r') { submit_creds(username_field.getText(),password_field.getText()); }
            }
        });

        TextButton login_button = new TextButton("Login!",skin);
        login_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                submit_creds(username_field.getText(),password_field.getText()); //we also submit the form if the button is pressed
            }
        });

        Table table = new Table();
        table.add(username_field);
        table.row();
        table.add(password_field);
        table.row();
        table.add(login_button);
        table.setPosition(200,200);
        stage.addActor(table);

        //AUTO LOGIN FOR NOW
        submit_creds("daniel","password");
    }

    @Override public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void submit_creds(String username,String password) {
        username = username.replaceAll("[,$\\s]",""); //get rid of dangerous characters
        password = password.replaceAll("[,$\\s]","");
        if (username.equals("") || password.equals("")) { creds_declined(); return; } //dont send if field(s) are empty
        Global.server_socket.send_msg(MT.CHECKCREDS,username+","+password);
    }
    public void creds_accepted() {
        Global.game.setScreen(Global.game.connecting_screen);
        Global.game.loadScreens();
    }
    public void creds_declined() {
        this.password_field.setText(""); //if the creds are wrong, clear the password field
        //TODO: display a message saying creds are invalid
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }

    @Override public void hide() { }

    @Override public void dispose() { }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }
}

class LobbyScreen implements Screen {

    private Stage stage;
    public LobbyScreen() {

    }

    @Override public void render(float delta) {

    }

    @Override public void show() { }

    @Override public void hide() { }

    @Override public void dispose() { }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }
}