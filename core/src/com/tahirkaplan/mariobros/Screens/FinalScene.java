package com.tahirkaplan.mariobros.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tahirkaplan.mariobros.MarioBros;

public class FinalScene extends ScreenAdapter {

    private MarioBros game;

    private Viewport viewport;
    private Stage stage;

    public FinalScene(MarioBros game){
        this.game = game;

        viewport = new FitViewport(MarioBros.V_WIDTH,MarioBros.V_HEIGHT,new OrthographicCamera());

        stage = new Stage(viewport,game.batch);

        Table table = new Table();

        Label label = new Label("Congratulations",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Label label1 = new Label("Tap to Play Again",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        table.center();
        table.setFillParent(true);


        table.add(label).expandX().padBottom(12);
        table.row();

        table.add(label1).expandX().padTop(8);
        stage.addActor(table);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,1,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();


        if (Gdx.input.justTouched()){
            game.setScreen(new PlayScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {

    }



    @Override
    public void dispose() {

    }
}
