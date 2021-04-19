package com.tahirkaplan.mariobros.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tahirkaplan.mariobros.MarioBros;
import com.tahirkaplan.mariobros.Scenes.Hud;
import com.tahirkaplan.mariobros.Screens.PlayScreen;
import com.tahirkaplan.mariobros.Sprites.Items.ItemDef;
import com.tahirkaplan.mariobros.Sprites.Items.Mushroom;

public class Coin extends InteractiveTileObject {

    private static TiledMapTileSet tileset;
    private static final int BLANK_COIN = 28;

    public Coin(PlayScreen screen, MapObject object){
        super(screen,object);
        tileset = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log("Coin","Collision");
        //setCategoryFilter(MarioBros.DESTROYED_BIT);
        if(getCell().getTile().getId() == BLANK_COIN){
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
        }else {
            if (object.getProperties().containsKey("mushroom")){
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x,body.getPosition().y + 16/MarioBros.PPM)
                        , Mushroom.class));
                MarioBros.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            }else
                MarioBros.manager.get("audio/sounds/coin.wav", Sound.class).play();


        }
        getCell().setTile(tileset.getTile(BLANK_COIN));
        Hud.addScore(100);

    }
}
