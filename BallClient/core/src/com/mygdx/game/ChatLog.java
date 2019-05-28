//Shrey mahey
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

public class ChatLog extends ApplicationAdapter {
    private Table table;
    private Skin skin;
    private TextureAtlas atlas;
    private Label chatLabel;
    private Label chatLogLabel;
    private TextField chatText;
    
    private LinkedList<String> playerMSGs = new LinkedList<String>();

    public ChatLog() {
        atlas = new TextureAtlas("gdx-skins/clean-crispy/skin/clean-crispy-ui.atlas");
        skin = new Skin(Gdx.files.internal("gdx-skins/clean-crispy/skin/clean-crispy-ui.json"));
        skin.addRegions(atlas);

        Table newTable = new Table();
        newTable.setFillParent(true);
        newTable.setDebug(false);
        Global.stage.addActor(newTable);
        this.table = newTable; //this table is the UI table, so be careful when clearing children
        table.setDebug(true); // This is optional, but enables debug lines for tables.
        // Add widgets to the table here.
        
        chatLabel = new Label("Chat: ", skin);
        chatLabel.setAlignment(Align.right);
        chatLogLabel = new Label("", skin);
        chatText = new TextField("", skin);
        chatText.setWidth(150);
        chatText.setMaxLength(120); //if 120 characters was good enough for twitter, it's good enough for us
        table.add(chatLabel).width(100f);
        
        Cell textfield = table.add(chatText).width(150f);
        chatLogLabel.setWrap(true); //you still need to setWrap to true each time the label is changed
        table.bottom().right().padBottom(10f).padRight(10f);

        final String name = "User";   //placeholder for player username
        chatText.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (textField.getText().trim().length() > 0 && c == '\r') {   //if enter key is pressed and the msgbox isnt just whitespace
                    updateChatLog(name + ": " + textField.getText());
                    textField.setText("");
                }
            }
        });
        textfield.setActor(chatText);
    }

    public void updateChatLog(String newChatMsg) {
        //Dealing with new chat message sent
        if (this.playerMSGs.size() > 14) {
            this.playerMSGs.removeFirst();
            this.playerMSGs.add(newChatMsg);
        } else {
            this.playerMSGs.add(newChatMsg);
        }

        //Updating table
        table.clearChildren();
        for (String msg : this.playerMSGs) {
            Label userName = new Label(msg.substring(0, msg.indexOf(":") + 2), skin);
            userName.setWrap(true);
            userName.setWidth(10f);
            userName.setAlignment(Align.right);
            Label content = new Label(msg.substring(msg.indexOf(":") + 2), skin);
            content.setWrap(true);
            content.setWidth(150);

            table.add(userName).width(100f).top();
            table.add(content).width(150f).padBottom(10f);
            table.row();
        }
        table.add(new Label("Chat: ", skin)).right();
        table.add(chatText);
        table.bottom().right().padBottom(10f).padRight(10f);
    }

    public void drawLog(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(1340,150,250,400,new Color(0,0,0,0.25f),new Color(0,0,0,0.25f),Color.BLACK,Color.BLACK);
    }
}