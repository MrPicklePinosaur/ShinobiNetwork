package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

class MainmenuScreen implements Screen {

    private Stage stage;
    private TextButton play_button;
    private TextButton inventory_button;
    private TextButton quit_button;

    public MainmenuScreen() {
        Skin skin = new Skin(Gdx.files.internal("gdx-skins/level-plane/skin/level-plane-ui.json"));

        this.play_button = new TextButton("Play",skin);
        play_button.setPosition(0,0);
        this.inventory_button = new TextButton("Inventory",skin);
        this.quit_button = new TextButton("Exit Game",skin);

        this.stage = new Stage();
        stage.addActor(play_button);
        stage.addActor(inventory_button);
        stage.addActor(quit_button);
    }

    @Override public void render(float delta) {
        this.stage.act(delta);
        this.stage.draw();
    }

    @Override public void resize(int width,int height) { }

    @Override public void pause() { }

    @Override public void resume() { }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }

    @Override public void hide() { }

    @Override public void dispose() { }

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
    private Camera camera;
    private ChatLog chatlog;

    public GameScreen() {
        this.stage = new Stage();
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        this.camera = new Camera();
        Gdx.gl.glEnable(GL20.GL_BLEND);

        this.inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        InputHandler input_handler = new InputHandler();
        inputMultiplexer.addProcessor(input_handler);
        Gdx.input.setInputProcessor(inputMultiplexer);
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
        chatlog.drawLog(shapeRenderer);
        shapeRenderer.end();

        //update stuff
        float deltaTime = Gdx.graphics.getDeltaTime();
        Global.updateInput();
        input_handler.sendMouse();
        input_handler.handleInput();
        Entity.stepFrameAll(deltaTime);

        camera.moveCam();
        batch.setProjectionMatrix(camera.getCam().combined);
        camera.updateCam();
    }

    @Override public void resize(int width,int height) { }

    @Override public void pause() { }

    @Override public void resume() { }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }

    @Override public void hide() { }

    @Override public void dispose() { }

    public Stage getStage() { return this.stage; }
}