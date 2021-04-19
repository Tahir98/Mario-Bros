package com.tahirkaplan.mariobros.Sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.tahirkaplan.mariobros.MarioBros;
import com.tahirkaplan.mariobros.Screens.PlayScreen;
import com.tahirkaplan.mariobros.Sprites.Enemies.Enemy;
import com.tahirkaplan.mariobros.Sprites.Enemies.Turtle;

public class Mario extends Sprite {

    public enum State{
        FALLING,JUMPING,STANDING,RUNNING,GROWING,DEAD
    }


    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;

    private TextureRegion marioStand;

    private Animation marioRun;
    private TextureRegion marioJump;

    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation bigMarioRun;
    private Animation growMario;

    private TextureRegion marioDead;

    private float stateTimer;
    private boolean runningRight;

    private boolean marioIsBig = false;
    private boolean runGrowAnimation;

    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;

    public boolean marioIsdead;

    public Mario(PlayScreen screen){
        //super(screen.getAtlas().findRegion("little_mario"));
        this.world = screen.getWorld();

        currentState = State.STANDING;
        previousState = currentState;

        stateTimer = 0f;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i=1;i<4;i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"),i*16,0,16,16));
        }
        marioRun = new Animation(0.1f,frames);
        frames.clear();


        for (int i=1;i<4;i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),i*16,0,16,32));
        }

        bigMarioRun = new Animation(0.1f,frames);
        frames.clear();

        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"),80,0,16,16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"),80,0,16,32);

        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"),0,0,16,16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32);


        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32));

        growMario = new Animation(0.2f,frames);

        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"),96,0,16,16);

        defineMario();

        setBounds(0,0,16/MarioBros.PPM,16/MarioBros.PPM);
        setRegion(marioStand);
        timeToDefineBigMario = false;
    }

    public void update(float delta) {
        if (marioIsBig){
            setPosition(b2body.getPosition().x - getWidth()/2,b2body.getPosition().y - getHeight()/2 - 6/MarioBros.PPM);
        }else
            setPosition(b2body.getPosition().x - getWidth()/2,b2body.getPosition().y - getHeight()/2);
        setRegion(getFrame(delta));

        if (timeToDefineBigMario)
            defineBigMario();

        if (timeToRedefineMario)
            reDefineMario();

    }

    public TextureRegion getFrame(float delta){
        currentState = getState();

        TextureRegion region;
        switch (currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region =  (TextureRegion) growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer))
                    runGrowAnimation = false;
                break;

            case JUMPING:
                region = marioIsBig ? bigMarioJump :marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? (TextureRegion) bigMarioRun.getKeyFrame(stateTimer,true):
                        (TextureRegion) marioRun.getKeyFrame(stateTimer,true);
                break;
            case FALLING:
            case STANDING:
                default:
                    region = marioIsBig ? bigMarioStand : marioStand;
                    break;
        }
        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true,false);
            runningRight = false;
        }else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true,false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;

        return region;
    }

    public State getState(){
        if (marioIsdead)
            return State.DEAD;
        else if(runGrowAnimation)
            return State.GROWING;
        else if (b2body.getLinearVelocity().y>0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if (b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public void grow(){
        if (!marioIsBig){
            runGrowAnimation = true;
            setBounds(getX(),getY(),getWidth(),getHeight()*2);
            MarioBros.manager.get("audio/sounds/powerup.wav", Sound.class).play();
        }

        //marioIsBig = true;

        if (!marioIsBig){
            timeToDefineBigMario = true;
        }


    }

    public boolean isDead(){
        return marioIsdead;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    public boolean isBig(){
        return marioIsBig;
    }

    public void hit(Enemy enemy){
        if (enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL){
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        }else {
            if (marioIsBig){
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(),getY(),getWidth(),getHeight()/2);
                MarioBros.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            }else{
                MarioBros.manager.get("audio/music/mario_music.ogg", Music.class).stop();
                MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
                marioIsdead = true;
                Filter filter = new Filter();
                filter.maskBits = MarioBros.NOTHING_BIT;
                for (Fixture fixture : b2body.getFixtureList()){
                    fixture.setFilterData(filter);
                }

                b2body.applyLinearImpulse(new Vector2(0,4f),b2body.getWorldCenter(),true);
            }
        }


    }

    public void marioFalled(){
        if (b2body.getPosition().y < -1/MarioBros.PPM && !marioIsdead){
            MarioBros.manager.get("audio/music/mario_music.ogg", Music.class).stop();
            MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
            marioIsdead = true;
            Filter filter = new Filter();
            filter.maskBits = MarioBros.NOTHING_BIT;
            for (Fixture fixture : b2body.getFixtureList()){
                fixture.setFilterData(filter);
            }

            b2body.applyLinearImpulse(new Vector2(0,4f),b2body.getWorldCenter(),true);
        }
    }

    public void reDefineMario(){
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);


        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6/MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT | MarioBros.OBJECT_BIT | MarioBros.ENEMY_BIT |
                MarioBros.ENEMY_HEAD_BIT | MarioBros.ITEM_BIT;


        fdef.shape = shape;

        b2body.createFixture(fdef).setUserData(this);


        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/MarioBros.PPM,6/MarioBros.PPM),new Vector2(2/MarioBros.PPM,6/MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToRedefineMario = false;
    }

    public void defineBigMario(){
        System.out.println("big mario");
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0,10/MarioBros.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6/MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT | MarioBros.OBJECT_BIT | MarioBros.ENEMY_BIT |
                MarioBros.ENEMY_HEAD_BIT | MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        shape.setPosition(new Vector2(0,-14/MarioBros.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/MarioBros.PPM,6/MarioBros.PPM),new Vector2(2/MarioBros.PPM,6/MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
        marioIsBig = true;
    }


    public void defineMario(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(128/MarioBros.PPM,32/MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6/MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT | MarioBros.OBJECT_BIT | MarioBros.ENEMY_BIT |
                MarioBros.ENEMY_HEAD_BIT | MarioBros.ITEM_BIT;

        fdef.shape = shape;


        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/MarioBros.PPM,6/MarioBros.PPM),new Vector2(2/MarioBros.PPM,6/MarioBros.PPM));
        fdef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

    }


}
