package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import javafx.scene.control.Tab;

import java.util.ArrayList;

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
        /*
        Table stats = new Table();
        TextField kills = new TextField();
        TextField deaths = new TextField();
        TextField damage = new TextField();
        */

        Table stats = new Table();
        float kdr = Global.user_data.getTotalKills();
        if (Global.user_data.getTotalKills() != 0) { //make sure theres mp divison by zero
            kdr = Global.user_data.getTotalKills() / Global.user_data.getTotalDeaths();
        }
        Label kd = new Label("KDR: "+kdr,skin);
        Label kills = new Label("Kills: "+Global.user_data.getTotalKills(),skin);
        Label deaths = new Label("Deaths: "+Global.user_data.getTotalDeaths(),skin);
        Label damage = new Label("Damage Dealt: "+Global.user_data.getTotalDamage(),skin);

        stats.add(kd);
        stats.row();
        stats.add(kills);
        stats.row();
        stats.add(deaths);
        stats.row();
        stats.add(damage);
        stats.setPosition(Global.SCREEN_WIDTH*3/4,Global.SCREEN_WIDTH/2);

        this.stage = new Stage();
        stage.addActor(play_button);
        stage.addActor(inventory_button);
        stage.addActor(quit_button);
        stage.addActor(stats);
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

    private boolean show_inventory = true;
    private Table loadout_overlay;
    private Table inventory_overlay;
    private ImageButton back_button;

    public GameScreen() {
        this.stage = new Stage();
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        //inventory stuff
        this.loadout_overlay = new Table();

        this.inventory_overlay = new Table();
        inventory_overlay.setBounds(0,0,Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);
        inventory_overlay.setDebug(true);
        inventory_overlay.setFillParent(true);
        inventory_overlay.pad(100);

        this.back_button = new ImageButton(new TextureRegionDrawable(AssetManager.getUIImage("back")));
        back_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                toggleInvVisible();
            }
        });

        toggleInvVisible(); //hide inventory screen

        this.inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        this.input_handler = new InputHandler();
        inputMultiplexer.addProcessor(this.input_handler);

        this.stage.addActor(loadout_overlay);
        this.stage.addActor(inventory_overlay);
        this.stage.addActor(back_button);
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
        if (this.show_inventory) { ScreenUtils.dimScreen(shapeRenderer,0.2f); } //dim screen if inv is open
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

    public void toggleInvVisible() {
        this.show_inventory = !this.show_inventory;
        if (this.show_inventory == true) {
            ScreenUtils.refreshInventory(this.inventory_overlay);
            this.inventory_overlay.setVisible(true);
            this.loadout_overlay.setVisible(true);
            this.back_button.setVisible(true);
        } else {
            this.inventory_overlay.setVisible(false);
            this.loadout_overlay.setVisible(false);
            this.back_button.setVisible(false);
        }
    }

    @Override public void show() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override public void hide() { Gdx.gl.glDisable(GL20.GL_BLEND); }

    @Override public void dispose() { }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }

}
class AwaitauthScreen implements Screen {

    private Stage stage;
    public AwaitauthScreen() {
        this.stage = new Stage();
    }

    @Override public void render(float delta) {

    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }

    @Override public void hide() { }

    @Override public void dispose() { }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }
}

class RetryconnectionScreen implements Screen {

    private Stage stage;
    private TextButton retry_button;
    private boolean connected = false;

    public RetryconnectionScreen() {
        Skin skin = new Skin(Gdx.files.internal("gdx-skins/level-plane/skin/level-plane-ui.json"));
        this.stage = new Stage();

        this.retry_button = new TextButton("Retry connection",skin);
        retry_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                connected = Global.game.attempt_connection(Global.server_ip, Global.server_port);
            }
        });
        stage.addActor(retry_button);
    }

    @Override public void render(float delta) {
        stage.act(delta);
        stage.draw();

        if (this.connected) { Global.game.setScreen(Global.game.login_screen); } //if user connects, take them to login screen
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }

    @Override public void hide() { }

    @Override public void dispose() { }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }
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

    @Override public void show() {
        ScreenUtils.refreshInventory(this.inventory_grid);
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
    private Label invalid_creds;

    private Label warning_text;
    private TextField reg_username_field;
    private TextField reg_email_field;
    private TextField reg_password_field;
    private TextField confirm_password_field;

    public LoginScreen() {
        Skin skin = new Skin(Gdx.files.internal("gdx-skins/level-plane/skin/level-plane-ui.json"));
        stage = new Stage();

        this.username_field = new TextField("",skin);
        username_field.setMessageText("Username"); //displays when box is empty
        username_field.setMaxLength(16);
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
        username_field.setMaxLength(30);
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

        this.invalid_creds = new Label("",skin);

        CheckBox remember_me = new CheckBox("Remember me",skin);

        Table login_table = new Table();
        login_table.setBounds(0,0,Global.SCREEN_WIDTH/2,Global.SCREEN_HEIGHT);
        login_table.add(invalid_creds);
        login_table.row();
        login_table.add(username_field);
        login_table.row();
        login_table.add(password_field);
        login_table.row();
        login_table.add(remember_me.pad(10));
        login_table.row();
        login_table.add(login_button);


        Table register_table = new Table();
        register_table.setBounds(Global.SCREEN_WIDTH/2,0,Global.SCREEN_WIDTH/2,Global.SCREEN_HEIGHT);
        register_table.setDebug(true);

        this.warning_text = new Label("",skin);

        this.reg_username_field = new TextField("",skin);
        reg_username_field.setMessageText("Username");
        reg_username_field.setMaxLength(16);

        this.reg_email_field = new TextField("",skin);
        reg_email_field.setMessageText("Email");
        reg_email_field.setMaxLength(30);

        this.reg_password_field = new TextField("",skin);
        reg_password_field.setMessageText("Password");
        reg_password_field.setPasswordMode(true);
        reg_password_field.setPasswordCharacter('*');
        reg_password_field.setMaxLength(30);

        this.confirm_password_field = new TextField("",skin);
        confirm_password_field.setMessageText("Confirm password");
        confirm_password_field.setPasswordMode(true);
        confirm_password_field.setPasswordCharacter('*');
        confirm_password_field.setMaxLength(30);

        TextButton register_button = new TextButton("Register!",skin);
        register_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                String warning = register(reg_username_field.getText(),reg_email_field.getText(),reg_password_field.getText(),confirm_password_field.getText());
                warning_text.setText(warning);
            }
        });

        register_table.add(warning_text).pad(10);
        register_table.row();
        register_table.add(reg_username_field);
        register_table.row();
        register_table.add(reg_email_field);
        register_table.row();
        register_table.add(reg_password_field);
        register_table.row();
        register_table.add(confirm_password_field);
        register_table.row();
        register_table.add(register_button);

        stage.addActor(login_table);
        stage.addActor(register_table);
    }

    @Override public void render(float delta) {
        //AUTO LOGIN FOR NOW
        //submit_creds("daniel","password");
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
        Global.game.setScreen(Global.game.awaitauth_screen);
        Global.game.loadScreens();
    }
    public void creds_declined() {
        this.password_field.setText(""); //if the creds are wrong, clear the password field
        //TODO: display a message saying creds are invalid
        this.invalid_creds.setText("Invalid username or password");
    }
    public void register_success() {
        this.warning_text.setText("Registration Successful!");
        this.reg_username_field.setText("");
        this.reg_email_field.setText("");
        this.reg_password_field.setText("");
        this.confirm_password_field.setText("");
    }
    public void register_failed() { this.warning_text.setText("Username taken"); }

    public String register(String username,String email,String password,String confirmpass) {
        if (username.length()==0 || email.length()==0 || password.length()==0 || confirmpass.length()==0) { return "Empty field"; }
        if (!password.equals(confirmpass)) { return "Passwords don't match"; }
        if (!(3 <= username.length() && username.length() <= 16)) { return "Username must be between 3 and 16 characters"; }
        if (username.contains("![a-zA-Z0-9]")) { return "Username can only contain letters and numbers"; }
        if (!(8 <= password.length() && password.length() <= 30)) { return "Password must be between 8 and 30 characters"; }
        if (password.contains("![a-zA-Z0-9]")) { return "Password can only contain letters and numbers"; }

        //if all else is good, send message to server to check to see if username is taken
        Global.server_socket.send_msg(MT.REGISTER,username+","+password);
        return ""; //no warning :)
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

class ScreenUtils {

    public static void refreshInventory(Table grid) {
        //TODO: when refreshing inventory, actually ask the server for a refresh
        Texture empty_slot = AssetManager.getUIImage("empty_slot");
        grid.clearChildren();
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
                grid.add(stack).pad(10);
            }
            grid.row(); //move down a row
        }
    }

    public static void dimScreen(ShapeRenderer sr,float dimness) {
        sr.setColor(0,0,0,dimness);
        sr.rect(0,0,Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);
    }
}