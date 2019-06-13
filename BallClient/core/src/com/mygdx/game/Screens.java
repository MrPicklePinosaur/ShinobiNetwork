package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import java.util.ArrayList;

class MainmenuScreen implements Screen {

    private Stage stage;
    private Table rootTable;
    private SpriteBatch batch = new SpriteBatch();
    private Label title;
    public MainmenuScreen() {

        TextButton play_button = new TextButton("Play",Global.skin);
        //play_button.setPosition(300,500);
        play_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                Global.game.setScreen(Global.game.game_screen);
                Global.server_socket.send_msg(MT.STARTGAME,"");
                Global.server_socket.enableGIP();
            }
        });

        TextButton inventory_button = new TextButton("Inventory",Global.skin);
        //inventory_button.setPosition(300,400);
        inventory_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                Global.game.setScreen(Global.game.inventory_screen);
            }
        });

        TextButton quit_button = new TextButton("Exit Game",Global.skin);
        //quit_button.setPosition(300,300);
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
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        BitmapFont pixelFont = Global.skin.getFont("PixelFont");
        labelStyle.font = pixelFont;
        labelStyle.fontColor = Color.valueOf("B5B5B5");
        Label kd = new Label("KDR: "+kdr,Global.skin);
        Label kills = new Label("Kills: "+Global.user_data.getTotalKills(),Global.skin);
        Label deaths = new Label("Deaths: "+Global.user_data.getTotalDeaths(),Global.skin);
        Label damage = new Label("Damage Dealt: "+Global.user_data.getTotalDamage(),Global.skin);
        kd.setStyle(labelStyle);
        kills.setStyle(labelStyle);
        deaths.setStyle(labelStyle);
        damage.setStyle(labelStyle);

        stats.add(kd).expandY().right().padRight(10f);
        stats.row();
        stats.add(kills).expandY().right().padRight(10f);
        stats.row();
        stats.add(deaths).expandY().right().padRight(10f);
        stats.row();
        stats.add(damage).expandY().right().padRight(10f);
        //stats.setPosition(Global.SCREEN_WIDTH*3/4,Global.SCREEN_WIDTH/2);

        rootTable = new Table();
        rootTable.setFillParent(true);
        Table buttonTable = new Table();

        this.stage = new Stage();
        stage.addActor(play_button);
        stage.addActor(inventory_button);
        stage.addActor(quit_button);
        stage.addActor(stats);
        stage.addActor(rootTable);
        stage.addActor(buttonTable);

        Pixmap menuPixmap = new Pixmap(1,1,Pixmap.Format.RGBA8888);
        menuPixmap.setColor(Color.valueOf("4c4c4c80"));
        menuPixmap.fill();
        rootTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(menuPixmap))));

        //rootTable.setSize(Global.SCREEN_WIDTH/2f,Global.SCREEN_HEIGHT);
        //rootTable.setPosition(0f,0f);
        buttonTable.add(play_button).pad(10f).width(play_button.getWidth()*2).height(play_button.getHeight()*2);
        buttonTable.row();
        buttonTable.add(inventory_button).center().pad(10f).width(inventory_button.getWidth()*2).height(inventory_button.getHeight()*2);
        buttonTable.row();
        buttonTable.add(quit_button).center().pad(10f).width(quit_button.getWidth()*2).height(quit_button.getHeight()*2);
        buttonTable.center();
        //rootTable.add(buttonTable).padRight(10f).right().padLeft(25f).padBottom(20f);
        rootTable.add(buttonTable);
        rootTable.add(stats).expandX().right().padRight(10f).fillY();
        rootTable.bottom();

        title = new Label("Ball Network!",Global.skin);
        title.setStyle(labelStyle);
        title.setPosition(Global.SCREEN_WIDTH/2f-title.getWidth()/2f,Global.SCREEN_HEIGHT/2f-title.getHeight()/2f);
        //rootTable.setDebug(true);
        //buttonTable.setDebug(true);
        //stats.setDebug(true);

    }

    @Override public void render(float delta) {
        this.stage.act(delta);
        this.stage.draw();
        batch.begin();
        title.draw(batch,1f);
        batch.end();

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
    //static Sprite background = new Sprite(new Texture("mountain_temple.png"));
    static Sprite background = new Sprite(new Texture("mt_ffa.png"));

    private Stage stage;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private InputMultiplexer inputMultiplexer;
    private InputHandler input_handler;

    private boolean show_menu = false;
    private Table pause_menu;
    private Table respawn_menu;
    private Inventory inv;

    public GameScreen() {
        this.stage = new Stage();
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        //menu
        TextButton resume_button = new TextButton("Resume",Global.skin);
        resume_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                Global.game.game_screen.hide_menu();
            }
        });
        TextButton inventory_button = new TextButton("Inventory",Global.skin);
        inventory_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                show_inv();
            }
        });
        TextButton exit_button = new TextButton("Leave Game",Global.skin);
        exit_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                Global.server_socket.send_msg(MT.LEAVEGAME,"");
                Global.server_socket.disableGIP();
                Global.game.setScreen(Global.game.mainmenu_screen);
            }
        });

        this.pause_menu = new Table();
        pause_menu.setBounds(0,0,Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);
        pause_menu.add(resume_button).pad(10);
        pause_menu.row();
        pause_menu.add(inventory_button).pad(10);
        pause_menu.row();
        pause_menu.add(exit_button).pad(10);
        this.pause_menu.setVisible(false);

        //Inventory
        this.inv = new Inventory(this.stage);
        this.inv.hide_inv();

        //Choose class menu
        this.respawn_menu = new Table();
        respawn_menu.setBounds(0,0,Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);
        final String[] class_list = new String[]{"ninja","archer","warrior","wizard"};
        for (int i = 0; i < 4; i++) {
            final int index = i;
            ImageButton choose = new ImageButton(new TextureRegionDrawable(new TextureRegion(AssetManager.getUIImage("choose_class_up"))),new TextureRegionDrawable(new TextureRegion(AssetManager.getUIImage("choose_class_down"))));
            Image class_image = new Image(AssetManager.getUIImage(class_list[index]));
            class_image.setFillParent(true);
            choose.addActor(class_image);
            choose.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event,float x,float y) {
                    String[] load_out = Global.user_data.getLoadout(class_list[index]); //TODO: DONT TRUST USER WITH THIS DATA
                    String msg = class_list[index]+","+load_out[0]+","+load_out[1];
                    Global.server_socket.send_msg(MT.RESPAWN,msg);
                    hide_death_screen();
                }
            });
            respawn_menu.add(choose).pad(10);
        }
        //this.hide_death_screen();

        this.stage.addActor(pause_menu);
        this.stage.addActor(respawn_menu);

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

        batch.begin();
        //HealthTracker.drawAll(batch);
        batch.end();

        //draw UI
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (this.show_menu) { ScreenUtils.dimScreen(shapeRenderer,0.3f); } //dim screen if menu is open
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

    public void show_menu() {
        this.show_menu = true;
        this.pause_menu.setVisible(true);
    }
    public void hide_menu() {
        this.show_menu = false;
        this.pause_menu.setVisible(false);
        this.inv.hide_inv();
    }
    public void show_inv() {
        this.pause_menu.setVisible(false);
        this.inv.show_inv();
    }
    public void show_death_screen() {
        this.hide_menu();
        this.respawn_menu.setVisible(true);
    }
    public void hide_death_screen() {
        this.respawn_menu.setVisible(false);
    }

    public boolean isMenuVisible() { return this.show_menu; }

    @Override public void show() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override public void hide() {
        Gdx.gl.glDisable(GL20.GL_BLEND);
        this.hide_menu();
        this.hide_death_screen();
    }

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
    private TextButton.TextButtonStyle tbs;

    public RetryconnectionScreen() {
        this.stage = new Stage();
        this.tbs = new TextButton.TextButtonStyle();
        Drawable db = Global.skin.newDrawable("buttonColor",Color.GRAY);
        db.setLeftWidth(Global.SCREEN_WIDTH/75f);
        db.setRightWidth(Global.SCREEN_WIDTH/100f);
        db.setTopHeight(Global.SCREEN_HEIGHT/55f);
        db.setBottomHeight(Global.SCREEN_HEIGHT/100f);
        this.tbs.up = db;
        db = Global.skin.newDrawable("buttonOverColor",Color.BLUE);
        this.tbs.over = db;
        db = Global.skin.newDrawable("buttonDownColor",Color.GREEN);
        this.tbs.down = db;
        this.tbs.font = Global.skin.getFont("PixelFont");
        this.tbs.overFontColor = Color.BLACK;
        this.tbs.fontColor = Color.WHITE;
        this.tbs.pressedOffsetX = 3;
        this.tbs.pressedOffsetY = -3;
        Global.skin.add("PixelFontStyle",tbs);
        this.retry_button = new TextButton("Retry connection",Global.skin,"PixelFontStyle");
        this.retry_button.setStyle(this.tbs);
        this.retry_button.setPosition(Global.SCREEN_WIDTH/2f-this.retry_button.getWidth()/2f,Global.SCREEN_HEIGHT/2f-this.retry_button.getHeight()/2f);
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
    private Inventory inv;

    public InventoryScreen() {

        this.stage = new Stage();
        this.inv = new Inventory(this.stage);
        inv.show_inv();

        ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(AssetManager.getUIImage("back"))));
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                Global.game.setScreen(Global.game.mainmenu_screen);
            }
        });

        this.stage.addActor(backButton.left().pad(20));
    }

    @Override public void render(float delta) {
        stage.act(delta);

        stage.draw();
    }

    @Override public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override public void hide() { }

    @Override public void dispose() { }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }
}

class Inventory {

    private Stage stage;
    private Table inventory_grid;
    private Table loadout_inv;
    private Table tabs;

    public Inventory(Stage screen_stage) {
        this.stage = screen_stage;

        //INVENTORY
        //some basic settings for the table
        this.inventory_grid = new Table();
        inventory_grid.setBounds(Global.SCREEN_WIDTH/8,0,Global.SCREEN_WIDTH*3/8,Global.SCREEN_HEIGHT);
        //inventory_grid.setDebug(true);
        inventory_grid.setFillParent(true);
        inventory_grid.left();

        this.loadout_inv = new Table();


        //BUTTONS
        TextButton allItems_button = new TextButton("All items",Global.skin);
        allItems_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                refreshInventory("");
                hide_loadout();
            }
        });
        TextButton ninjaItems_button = new TextButton("Ninja",Global.skin);
        ninjaItems_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                refreshInventory("katana,waki");
                show_loadout("ninja");
            }
        });
        TextButton archerItems_button = new TextButton("Archer",Global.skin);
        archerItems_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                refreshInventory("bow,quiver");
                show_loadout("archer");
            }
        });
        TextButton warriorItems_button = new TextButton("Warrior",Global.skin);
        warriorItems_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                refreshInventory("sword,helm");
                show_loadout("warrior");
            }
        });
        TextButton wizardItems_button = new TextButton("Wizard",Global.skin);
        wizardItems_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                refreshInventory("staff,spell");
                show_loadout("wizard");
            }
        });

        this.tabs = new Table();
        tabs.setBounds(Global.SCREEN_WIDTH/8,0,Global.SCREEN_HEIGHT,Global.SCREEN_WIDTH/8);
        tabs.add(wizardItems_button);
        tabs.add(warriorItems_button);
        tabs.add(archerItems_button);
        tabs.add(ninjaItems_button);
        tabs.add(allItems_button);
        tabs.setTransform(true);
        tabs.rotateBy(90);
        tabs.bottom();
        tabs.setDebug(true);

        stage.addActor(inventory_grid);
        stage.addActor(loadout_inv);
        stage.addActor(tabs);
    }

    public void refreshInventory(String filter) { //filter inv by item type, if an empty string is provided, it means no filtere
        //TODO: when refreshing inventory, actually ask the server for a refresh

        this.inventory_grid.clearChildren();
        //populate the table with the contents of the user's inventory
        ArrayList<String> inv = new ArrayList<String>();
        for (String name : Global.user_data.getInventory()) {
            if (filter.contains(AssetManager.getItemDescrip(name).getItemType()) || filter.length() == 0) {
                inv.add(name);
            }
        }

        Texture empty_slot_up = AssetManager.getUIImage("empty_slot_up");
        //Texture empty_slot_hover = AssetManager.getUIImage("empty_slot_hover");
        Texture empty_slot_down = AssetManager.getUIImage("empty_slot_down");

        for (int j = 0; j < 6; j++) { //6 rows
            for (int i = 0; i < 4; i++) { //4 columns
                final ImageButton slot = new ImageButton(new TextureRegionDrawable(new TextureRegion(empty_slot_up)),new TextureRegionDrawable(new TextureRegion(empty_slot_down)));
                slot.addListener(new ClickListener() {
                    @Override public void clicked(InputEvent event,float x,float y) {
                        if (slot.getChildren().size > 1) { //size gratehr than 1 means that there is an item on this slot

                            //find out which slot the item belongs in and equip it
                            String item_name = slot.getName();
                            assert(item_name != null && !item_name.equals("")): "Slot name is empty";

                            String item_type = AssetManager.getItemDescrip(item_name).getItemType();
                            String filter = Global.user_data.setLoadout(item_name,item_type);
                            refresh_loadout(filter);
                        }
                    }
                });
                if (inv.size() > 0) { //go through client's inv list and draw them
                    String item_path = inv.get(0);
                    Image item = new Image(AssetManager.getSpritesheet(item_path));
                    item.setFillParent(true);
                    slot.addActor(item);
                    slot.setName(item_path);
                    inv.remove(0);
                }

                this.inventory_grid.add(slot).pad(10);
            }
            this.inventory_grid.row(); //move down a row
        }

    }

    public void show_inv() {
        refreshInventory("");
        this.inventory_grid.setVisible(true);
        this.tabs.setVisible(true);
        this.loadout_inv.setVisible(false);
    }

    public void hide_inv() {
        this.inventory_grid.setVisible(false);
        this.tabs.setVisible(false);
        this.loadout_inv.setVisible(false);
    }

    public void refresh_loadout(String filter) {
        assert (filter.equals("ninja") || filter.equals("archer") || filter.equals("warrior") || filter.equals("wizard")): "Invalid filter";
        String[] loadout = Global.user_data.getLoadout(filter); //the first item is the weapon, second is the ability

        this.loadout_inv.clear();

        Texture empty_slot = AssetManager.getUIImage("empty_slot_up");

        Label class_name = new Label(filter,Global.skin);

        //weapon slot
        Stack weapon_stack = new Stack();
        weapon_stack.add(new Image(empty_slot));
        if (loadout.length == 2) { weapon_stack.add(new Image(AssetManager.getSpritesheet(loadout[0]))); } //if the user actually has a weapon equipped

        //class preview
        Stack class_preview = new Stack();
        class_preview.add(new Image(AssetManager.getUIImage("selected_class")));
        class_preview.add(new Image(AssetManager.getUIImage(filter)));

        //ability slot
        Stack ability_stack = new Stack();
        ability_stack.add(new Image(empty_slot));

        if (loadout.length == 2) { ability_stack.add(new Image(AssetManager.getSpritesheet(loadout[1]))); } //if the user actually has an ability equipped

        loadout_inv.add(class_name);
        loadout_inv.row();
        loadout_inv.add(weapon_stack);
        loadout_inv.add(class_preview).pad(10);
        loadout_inv.add(ability_stack);
        loadout_inv.setBounds(Global.SCREEN_WIDTH*4/8,0,Global.SCREEN_WIDTH*4/8,Global.SCREEN_HEIGHT);
        loadout_inv.pad(30);
    }

    public void show_loadout(String filter) { refresh_loadout(filter); this.loadout_inv.setVisible(true); }
    public void hide_loadout() { this.loadout_inv.setVisible(false); }
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
        stage = new Stage();
        this.username_field = new TextField("",Global.skin);
        username_field.setMessageText("Username"); //displays when box is empty
        username_field.setMaxLength(16);
        username_field.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                //if enter is pressed, submit form
                if (c == '\r') { submit_creds(username_field.getText(),password_field.getText()); }
            }
        });

        this.password_field = new TextField("",Global.skin);
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

        TextButton login_button = new TextButton("Login!",Global.skin);
        login_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                submit_creds(username_field.getText(),password_field.getText()); //we also submit the form if the button is pressed
            }
        });

        this.invalid_creds = new Label("",Global.skin);

        CheckBox remember_me = new CheckBox("Remember me",Global.skin);

        Table login_table = new Table(Global.skin);
        Pixmap loginPixmap = new Pixmap(1,1,Pixmap.Format.RGBA8888);
        loginPixmap.setColor(Color.valueOf("db0a3180"));//hexcode with A - 80: 50% transparency
        loginPixmap.fill();
        login_table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(loginPixmap))));

        Label logInLabel = new Label("Log in!",Global.skin);
        Label.LabelStyle logInLabelStyle = new Label.LabelStyle();
        logInLabelStyle.font = Global.skin.getFont("PixelFont");
        logInLabel.setStyle(logInLabelStyle);

        login_table.setBounds(0,0,Global.SCREEN_WIDTH/2,Global.SCREEN_HEIGHT);
        login_table.add(logInLabel).padBottom(10);
        login_table.row();
        login_table.add(invalid_creds).padBottom(10);
        login_table.row();
        login_table.add(username_field).padBottom(5);
        login_table.row();
        login_table.add(password_field).padTop(5).padBottom(5);
        login_table.row();
        login_table.add(remember_me).padTop(5);
        login_table.row();
        login_table.add(login_button).padTop(10);


        Table register_table = new Table(Global.skin);
        Pixmap registerPixmap = new Pixmap(1,1,Pixmap.Format.RGBA8888);
        registerPixmap.setColor(Color.valueOf("084ca180"));//hexcode with A - 80: 50% transparency
        registerPixmap.fill();
        register_table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(registerPixmap))));

        Label registerLabel = new Label("Register!",Global.skin);
        Label.LabelStyle registerLabelStyle = new Label.LabelStyle();
        registerLabelStyle.font = Global.skin.getFont("PixelFont");
        registerLabel.setStyle(registerLabelStyle);


        register_table.setBounds(Global.SCREEN_WIDTH/2,0,Global.SCREEN_WIDTH/2,Global.SCREEN_HEIGHT);
        this.warning_text = new Label("",Global.skin);

        this.reg_username_field = new TextField("",Global.skin);
        reg_username_field.setMessageText("Username");
        reg_username_field.setMaxLength(16);

        this.reg_email_field = new TextField("",Global.skin);
        reg_email_field.setMessageText("Email");
        reg_email_field.setMaxLength(30);

        this.reg_password_field = new TextField("",Global.skin);
        reg_password_field.setMessageText("Password");
        reg_password_field.setPasswordMode(true);
        reg_password_field.setPasswordCharacter('*');
        reg_password_field.setMaxLength(30);

        this.confirm_password_field = new TextField("",Global.skin);
        confirm_password_field.setMessageText("Confirm password");
        confirm_password_field.setPasswordMode(true);
        confirm_password_field.setPasswordCharacter('*');
        confirm_password_field.setMaxLength(30);

        TextButton register_button = new TextButton("Register!",Global.skin);
        register_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                String warning = register(reg_username_field.getText(),reg_email_field.getText(),reg_password_field.getText(),confirm_password_field.getText());
                warning_text.setText(warning);
            }
        });

        register_table.add(registerLabel).padBottom(10);
        register_table.row();
        register_table.add(warning_text).padBottom(10);
        register_table.row();
        register_table.add(reg_username_field).padBottom(5);
        register_table.row();
        register_table.add(reg_email_field).padTop(5).padBottom(5);
        register_table.row();
        register_table.add(reg_password_field).padTop(5).padBottom(5);
        register_table.row();
        register_table.add(confirm_password_field).padTop(5);
        register_table.row();
        register_table.add(register_button).padTop(10);

        stage.addActor(login_table);
        stage.addActor(register_table);
    }

    @Override public void render(float delta) {
        //AUTO LOGIN FOR NOW
        submit_creds("daniel","password");
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

class ScreenUtils {

    public static void dimScreen(ShapeRenderer sr,float dimness) {
        sr.setColor(0,0,0,dimness);
        sr.rect(0,0,Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);
    }
}