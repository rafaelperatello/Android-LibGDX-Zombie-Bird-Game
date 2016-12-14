package com.peralta.zombiebird.gameworld;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.peralta.zombiebird.ZBGame;
import com.peralta.zombiebird.gameobjects.Bird;
import com.peralta.zombiebird.gameobjects.ScrollHandler;
import com.peralta.zombiebird.zbhelpers.AssetLoader;

/**
 * Created by Rafael on 10/12/2016.
 */

public class GameWorld {
    public enum GameState {
        READY, RUNNING, GAMEOVER
    }

    private GameState currentState;

    private int score = 0;
    public int highScore = 0;
    private int midPointY;

    private Bird bird;

    private ScrollHandler scroller;
    private Rectangle ground;

    public GameWorld(int midPointY) {
        currentState = GameState.READY;
        this.midPointY = midPointY;

        bird = new Bird(33, midPointY - 5, 17, 12);
        // The grass should start 66 pixels below the midPointY
        scroller = new ScrollHandler(this, midPointY + 66);
        ground = new Rectangle(0, midPointY + 66, 136, 11);

        highScore = ZBGame.lastHighScore;
    }

    public void update(float delta) {

        switch (currentState) {
            case READY:
                updateReady(delta);
                break;

            case RUNNING:
            default:
                updateRunning(delta);
                break;
        }

    }

    private void updateReady(float delta) {
        // Do nothing for now
    }

    public void updateRunning(float delta) {
        if (delta > .15f) {
            delta = .15f;
        }

        bird.update(delta);
        scroller.update(delta);

        if (scroller.collides(bird) && bird.isAlive()) {
            scroller.stop();
            bird.die();
            AssetLoader.dead.play();
        }

        if (currentState != GameState.GAMEOVER) {
            if (Intersector.overlaps(bird.getBoundingCircle(), ground)) {
                scroller.stop();
                bird.die();
                bird.decelerate();
                currentState = GameState.GAMEOVER;

                if (score > highScore) {
                    highScore = score;
                    ZBGame.listener.onHighScoreUpdate(score);
                }
            }
        }
    }

    public int getScore() {
        return score;
    }

    public void addScore(int increment) {
        score += increment;
    }

    public boolean isReady() {
        return currentState == GameState.READY;
    }

    public void start() {
        currentState = GameState.RUNNING;
    }

    public void restart() {
        currentState = GameState.READY;
        score = 0;
        bird.onRestart(midPointY - 5);
        scroller.onRestart();
        currentState = GameState.READY;
    }

    public boolean isGameOver() {
        return currentState == GameState.GAMEOVER;
    }

    public Bird getBird() {
        return bird;

    }

    public ScrollHandler getScroller() {
        return scroller;
    }
}
