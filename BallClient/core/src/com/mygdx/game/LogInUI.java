//shrey mahey

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;

import java.util.LinkedList;

//This class will handle the visual component to the log in system
//it will have sign up and log in options, and will use Database
//to give the user feedback on if they registered/signed in properly
public class LogInUI {
    private Table table;
    private Skin skin;
    private TextureAtlas atlas;
    private Label logInTitle;
    private Label registerTitle;
    private Label logInU;
    private Label logInP;
    private TextField chatText;

    public LogInUI(){
        atlas = new TextureAtlas("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.atlas");
        skin = new Skin(Gdx.files.internal("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.json"));
        skin.addRegions(atlas);
        this.table = table; //this table is the UI table, so be careful when clearing children
        //table.setDebug(true); // This is optional, but enables debug lines for tables.
        // Add widgets to the table here.
        table.add(logInTitle).width(100f);
        Cell textfield = table.add(chatText).width(150f);

        //chatLogLabel.setWrap(true); //you still need to setWrap to true each time the label is changed
        //table.bottom().right().padBottom(10f).padRight(10f);

        final String name = "User";   //placeholder for player username

        chatText.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (textField.getText().trim().length() > 0 && c == '\r') {   //if enter key is pressed and the msgbox isnt just whitespace
                    //deal with entered username
                    textField.setText("");
                }
            }
        });
        textfield.setActor(chatText);

    }
    
    public static void userNameTaken(){
        System.out.println("Username taken!");
    }
}
