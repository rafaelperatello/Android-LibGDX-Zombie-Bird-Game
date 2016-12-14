package com.peralta.zombiebird;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.peralta.zombiebird.intefaces.GameListener;
import com.peralta.zombiebird.screens.GameScreen;
import com.peralta.zombiebird.zbhelpers.AssetLoader;

public class ZBGame extends Game {
    public static GameListener listener;
    public static int lastHighScore;

    public ZBGame(GameListener listener, int highScore) {
        super();
        ZBGame.listener = listener;
        ZBGame.lastHighScore = highScore;
    }

    @Override
    public void create() {
        Gdx.app.log("ZBGame", "created");
        AssetLoader.load();
        setScreen(new GameScreen());
    }

    @Override
    public void dispose() {
        super.dispose();
        AssetLoader.dispose();
    }
}
