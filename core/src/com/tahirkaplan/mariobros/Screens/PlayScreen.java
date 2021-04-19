package com.tahirkaplan.mariobros.Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tahirkaplan.mariobros.MarioBros;
import com.tahirkaplan.mariobros.Scenes.Hud;
import com.tahirkaplan.mariobros.Sprites.Enemies.Enemy;
import com.tahirkaplan.mariobros.Sprites.Items.Item;
import com.tahirkaplan.mariobros.Sprites.Items.ItemDef;
import com.tahirkaplan.mariobros.Sprites.Items.Mushroom;
import com.tahirkaplan.mariobros.Sprites.Mario;
import com.tahirkaplan.mariobros.Tools.B2WorldCreator;
import com.tahirkaplan.mariobros.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingDeque;

public class PlayScreen implements Screen {

    private MarioBros game;

    private TextureAtlas atlas;

    private OrthographicCamera gameCam;
    private Viewport viewport;

    private Hud hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    private Mario player;

    private Music music;

    private Array<Item> items;
    private LinkedBlockingDeque<ItemDef> itemToSpawn;


    public PlayScreen(MarioBros game) {

        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        this.game = game;

        gameCam = new OrthographicCamera();
        viewport = new FitViewport(MarioBros.V_WIDTH/MarioBros.PPM,MarioBros.V_HEIGHT/MarioBros.PPM,gameCam);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");

        renderer = new OrthogonalTiledMapRenderer(map,1/MarioBros.PPM);

        gameCam.position.set(viewport.getWorldWidth()/2,viewport.getWorldHeight()/2,0);

        world = new World(new Vector2(0,-10),true);

        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        player = new Mario(this);
        hud = new Hud(game.batch,this.player);

        world.setContactListener(new WorldContactListener());

        music = MarioBros.manager.get("audio/music/mario_music.ogg",Music.class);
        music.setLooping(true);
        music.play();

        items = new Array<Item>();
        itemToSpawn = new LinkedBlockingDeque<ItemDef>();


    }

    public void spawnItem(ItemDef idef){
        itemToSpawn.add(idef);
    }

    public void handleSpawningItems(){
        if (!itemToSpawn.isEmpty()){
            ItemDef idef = itemToSpawn.poll();
            if (idef.type == Mushroom.class){
                items.add(new Mushroom(this,idef.position.x,idef.position.y));
            }
        }
    }

    @Override
    public void show() {

    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    public void handleInput(float delta){
        if (player.currentState == Mario.State.DEAD)
            return;

       if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
           if (Gdx.input.isKeyPressed(Input.Keys.UP) && player.b2body.getLinearVelocity().y == 0) {
               player.b2body.applyLinearImpulse(new Vector2(0, 4f), new Vector2(0.1f, 0.1f), true);
           }

           if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2) {
               player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
           }

           if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2) {
               player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
           }

           if (Gdx.input.isTouched() && Gdx.input.getY() < Gdx.graphics.getHeight() / 4) {
               if (player.b2body.getLinearVelocity().y == 0) {
                   player.b2body.applyLinearImpulse(new Vector2(0, 4f), new Vector2(0.1f, 0.1f), true);
               }

           }

           if (Gdx.input.isKeyPressed(Input.Keys.C) && player.getState() != Mario.State.JUMPING)
               player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, 6);

       }

    }

    public void update(float delta){
        handleInput(delta);
        //world.setGravity(new Vector2(0,-4));
        handleSpawningItems();

        world.step(1f/60f,1,1);

        player.update(delta);
        player.marioFalled();

        for (Enemy enemy : creator.getEnemies()){
            enemy.update(delta);
            if (enemy.getX() < player.getX() + 300/MarioBros.PPM)
                enemy.b2body.setActive(true);
        }

        for (Item item:items){
            item.update(delta);
        }

        hud.update(delta);

        if (player.currentState != Mario.State.DEAD){
            if (player.b2body.getPosition().x > gameCam.viewportWidth/2){
                gameCam.position.x = player.b2body.getPosition().x;
            }
        }

        gameCam.update();
        renderer.setView(gameCam);

        if (WorldContactListener.gameFinished){
            try {
                dispose();
                new GameOverScreen(game);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void render(float delta) {

        update(delta);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        /** render our game map*/
        renderer.render();

        /** renderer out Box2dDebugLines*/
        //b2dr.render(world,gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        player.draw(game.batch);

        for (Enemy enemy : creator.getEnemies()){
            enemy.draw(game.batch);
        }

        for (Item item:items){
            item.draw(game.batch);
        }

        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if (gameOver()){
            dispose();
            game.setScreen(new GameOverScreen(game));
        }

        if (player.b2body.getPosition().x > 35.290f && player.b2body.getPosition().x < 35.300f
                && player.b2body.getPosition().y < 0.4f){
            (game.manager.get("audio/music/mario_music.ogg", Music.class)).stop();
            game.setScreen( new FinalScene(game));

        }
    }

    public boolean gameOver(){
        if (player.currentState == Mario.State.DEAD  && player.getStateTimer() > 3 || hud.worldTimer <= 0 )
            return true;

        return false;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);

    }

    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    public MarioBros getGame(){
        return  game;
    }
}
