package com.tahirkaplan.mariobros.Scenes;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tahirkaplan.mariobros.MarioBros;
import com.tahirkaplan.mariobros.Sprites.Mario;

public class Hud {
    public Stage stage;
    private Viewport viewport;

    public Integer worldTimer;
    private float timeCount;
    private static Integer score;

    private Label countdownLabel;
    private static Label scoreLabel;
    private Label timeLabel;
    private Label levelLabel;
    private Label worldLabel;
    private Label marioLabel;

    private ImageButton left;
    private ImageButton right;
    private ImageButton jump;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpressed = false;

    private Vector2 velocity;
    private Mario mario;

    public Hud(SpriteBatch batch, final Mario mario){
        this.mario = mario;

        velocity = new Vector2();

        worldTimer = 300;
        timeCount = 0f;
        score = 0;

        viewport = new FillViewport(MarioBros.V_WIDTH,MarioBros.V_HEIGHT,new OrthographicCamera());
        stage = new Stage(viewport,batch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);



        countdownLabel = new Label(String.format("%03d",worldTimer),new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d",score),new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1-1",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("WORLD",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        marioLabel = new Label("MARIO",new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(marioLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);

        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        if ( Gdx.app.getType() == Application.ApplicationType.Android){
            left = new ImageButton(new TextureRegionDrawable(new Texture("left.png")));
            right = new ImageButton(new TextureRegionDrawable(new Texture("right.png")));
            jump = new ImageButton(new TextureRegionDrawable(new Texture("jump.png")));

            left.setPosition(MarioBros.V_WIDTH/16,MarioBros.V_HEIGHT/16);
            left.setSize(MarioBros.V_HEIGHT/6,MarioBros.V_HEIGHT/6);
            left.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    leftPressed = true;
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    leftPressed = false;
                }


            });
            stage.addActor(left);

            right.setPosition(MarioBros.V_WIDTH/16 + left.getWidth() + MarioBros.V_WIDTH/16,MarioBros.V_HEIGHT/16);
            right.setSize(MarioBros.V_HEIGHT/6,MarioBros.V_HEIGHT/6);
            right.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    rightPressed = true;
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    rightPressed = false;
                }
            });

            jump.setPosition(MarioBros.V_WIDTH - left.getWidth() - MarioBros.V_HEIGHT/6 ,MarioBros.V_HEIGHT/16);
            jump.setSize(MarioBros.V_HEIGHT/6,MarioBros.V_HEIGHT/6);
            jump.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    jumpressed = true;
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    jumpressed = false;
                }
            });


            stage.addActor(left);
            stage.addActor(right);
            stage.addActor(jump);

        }



        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);

    }

    public void update(float delta){
        timeCount += delta;
        if (timeCount >=1){
            worldTimer--;
            countdownLabel.setText(String.format("%03d",worldTimer));
            timeCount = 0;
        }

        if (leftPressed && mario.currentState != Mario.State.DEAD){
            velocity = mario.b2body.getLinearVelocity();
            if (velocity.x > -2) velocity.x -= 0.1f;
            mario.b2body.setLinearVelocity(velocity);
        }

        if (rightPressed && mario.currentState != Mario.State.DEAD){
            velocity = mario.b2body.getLinearVelocity();
            if (velocity.x < 2) velocity.x += 0.1f;
            mario.b2body.setLinearVelocity(velocity);
        }

        if (jumpressed){
            if (mario.currentState != Mario.State.JUMPING && mario.currentState != Mario.State.DEAD){
                velocity = mario.b2body.getLinearVelocity();
                velocity.y = 4f;
                mario.b2body.setLinearVelocity(velocity);
            }
        }

    }

    public static void addScore(int value){
        score += value;
        scoreLabel.setText(String.format("%06d",score));
    }

    public void dispose(){
        stage.dispose();
    }
}






