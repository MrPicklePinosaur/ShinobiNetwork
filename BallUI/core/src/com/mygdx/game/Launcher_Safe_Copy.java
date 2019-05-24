package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;

import java.util.LinkedList;

public class Launcher_Safe_Copy extends ApplicationAdapter {
    ShapeRenderer sr;
    Texture minimap;
    private Stage stage;
    private Table table;
    Skin skin;
    TextureAtlas atlas;
    Label chatLabel;
    Label chatLogLabel;
    TextField chatText;
    static LinkedList<String> playerMSGs = new LinkedList<String>();
    public void chatLog(){
        atlas = new TextureAtlas("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.atlas");
        skin = new Skin(Gdx.files.internal("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.json"));
        skin.addRegions(atlas);
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        //table.setDebug(true); // This is optional, but enables debug lines for tables.

        // Add widgets to the table here.
        chatLabel = new Label("Chat: ", skin);
        chatLabel.setAlignment(Align.right);
        //chatLogLabel = new Label("Line 1\nLine 2",skin);
        chatLogLabel = new Label("",skin);
        chatText = new TextField("", skin);
        chatText.setWidth(150);
        chatText.setMaxLength(120); //if 120 characters was good enough for twitter, it's good enough for us
        //table.add(new Label("",skin)).width(100f);
        //Cell chatlog = table.add(chatLogLabel).width(100f).maxHeight(100f);//.colspan(2);
        //table.row();
        table.add(chatLabel).width(100f);
        Cell textfield = table.add(chatText).width(150f);
        //chatlog.clearActor();
        //chatLogLabel = new Label("Linejiojii9okiooikokpkokokokoiooooooooooooooooooooooookd 3\nLinikkkkkkkkkkkkkkkkkkkkkkklllllllllllllllllllllllllouikmoimoke 4",skin);
        chatLogLabel.setWrap(true); //you still need to setWrap to true each time the label is changed
        //chatLogLabel.setWidth(100);
        table.bottom().right().padBottom(10f).padRight(10f);

        /*if(input!="\n" && input=="\r"){
            updateChatLog(chatMsgs,);
        }*/

        final String name = "User";   //placeholder for player username

        chatText.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if(textField.getText().trim().length()>0 && c=='\r'){   //if enter key is pressed and the msgbox isnt just whitespace
                    updateChatLog(name+": "+textField.getText());
                    textField.setText("");
                }
            }
        } );
        /*if(textlistener.keyTyped(InputEvent e,char key)){

        }*/

        //chatlog.setActor(chatLogLabel);
        textfield.setActor(chatText);
    }
    public void updateChatLog(String newChatMsg){
        //LinkedList<Label> chatMsgs = server.getAllMsgs();
        LinkedList<String> chatMsgs = playerMSGs;
        //Dealing with new chat message sent
        if(chatMsgs.size()>14){
            chatMsgs.removeFirst();
            chatMsgs.add(newChatMsg);
        }else{
            chatMsgs.add(newChatMsg);
        }
        //Updating table
        table.clearChildren();
        for(String msg : chatMsgs){
            Label userName = new Label(msg.substring(0,msg.indexOf(":")+2),skin);
            userName.setWrap(true);
            userName.setWidth(10f);
            userName.setAlignment(Align.right);
            Label content = new Label(msg.substring(msg.indexOf(":")+2),skin);
            content.setWrap(true);
            content.setWidth(150);

            table.add(userName).width(100f).top();
            table.add(content).width(150f).padBottom(10f);
            table.row();
        }
        table.add(new Label("Chat: ",skin)).right();
        table.add(chatText);
        table.bottom().right().padBottom(10f).padRight(10f);
    }
    public class MyTextInputListener implements Input.TextInputListener {
        @Override
        public void input (String text) {
        }

        @Override
        public void canceled () {
        }
    }
    @Override
    public void create () {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        minimap = new Texture("MiniMap.png");
        sr = new ShapeRenderer();
        chatLog();
    }

    @Override
    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render () {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.rect(1340,150,250,400,new Color(0,0,0,0.25f),new Color(0,0,0,0.25f),Color.BLACK,Color.BLACK);
        sr.rect(1340,550,250,350,Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK);  //temporary space cover, will be replaced by minimap later
        sr.end();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }}
