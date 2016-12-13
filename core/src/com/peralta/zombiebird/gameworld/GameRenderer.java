package com.peralta.zombiebird.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.peralta.zombiebird.gameobjects.Bird;
import com.peralta.zombiebird.gameobjects.Grass;
import com.peralta.zombiebird.gameobjects.Pipe;
import com.peralta.zombiebird.gameobjects.ScrollHandler;
import com.peralta.zombiebird.zbhelpers.AssetLoader;

/**
 * Created by rafae on 10/12/2016.
 */

public class GameRenderer {
    private final String LABEL_START = "Hora do Show";
    private final String LABEL_GAME_OVER = "Deu ruim";
    private final String LABEL_AGAIN = "Bora de novo?";

    private GameWorld myWorld;
    private OrthographicCamera cam;
    private ShapeRenderer shapeRenderer;

    private SpriteBatch batcher;

    private int midPointY;
    private int midPointX;
    private int gameHeight;

    // Game Objects
    private Bird bird;
    private ScrollHandler scroller;
    private Grass frontGrass, backGrass;
    private Pipe pipe1, pipe2, pipe3;

    // Game Assets
    private TextureRegion bg, grass;
    private Animation birdAnimation;
    private TextureRegion birdMid, birdDown, birdUp;
    private TextureRegion skullUp, skullDown, bar;

    private GlyphLayout glyphLayoutFont;
    private GlyphLayout glyphLayoutFontShadow;

    public GameRenderer(GameWorld world, int gameHeight, int midPointY) {
        myWorld = world;

        this.gameHeight = gameHeight;
        this.midPointY = midPointY;
        this.midPointX = 136 / 2;

        cam = new OrthographicCamera();
        cam.setToOrtho(true, 136, gameHeight);

        batcher = new SpriteBatch();
        batcher.setProjectionMatrix(cam.combined);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);

        // Call helper methods to initialize instance variables
        initGameObjects();
        initAssets();
    }

    public void render(float runTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw Background color
        shapeRenderer.setColor(55 / 255.0f, 80 / 255.0f, 100 / 255.0f, 1);
        shapeRenderer.rect(0, 0, 136, midPointY + 66);

        // Draw Grass
        shapeRenderer.setColor(111 / 255.0f, 186 / 255.0f, 45 / 255.0f, 1);
        shapeRenderer.rect(0, midPointY + 66, 136, 11);

        // Draw Dirt
        shapeRenderer.setColor(147 / 255.0f, 80 / 255.0f, 27 / 255.0f, 1);
        shapeRenderer.rect(0, midPointY + 77, 136, 52);

        shapeRenderer.end();

        batcher.begin();
        batcher.disableBlending();
        batcher.draw(bg, 0, midPointY + 23, 136, 43);

        // 1. Draw Grass
        drawGrass();

        // 2. Draw Pipes
        drawPipes();
        batcher.enableBlending();

        // 3. Draw Skulls (requires transparency)
        drawSkulls();

        if (bird.shouldntFlap()) {
            batcher.draw(birdMid, bird.getX(), bird.getY(),
                    bird.getWidth() / 2.0f, bird.getHeight() / 2.0f,
                    bird.getWidth(), bird.getHeight(), 1, 1, bird.getRotation());

        } else {
            batcher.draw(birdAnimation.getKeyFrame(runTime), bird.getX(),
                    bird.getY(), bird.getWidth() / 2.0f,
                    bird.getHeight() / 2.0f, bird.getWidth(), bird.getHeight(),
                    1, 1, bird.getRotation());
        }

        if (myWorld.isReady()) {
            glyphLayoutFontShadow.setText(AssetLoader.shadow, LABEL_START);
            glyphLayoutFont.setText(AssetLoader.font, LABEL_START);
            AssetLoader.shadow.draw(batcher, LABEL_START, midPointX - (glyphLayoutFontShadow.width / 2), 76);
            AssetLoader.font.draw(batcher, LABEL_START, midPointX - (glyphLayoutFont.width / 2), 75);
        } else {
            if (myWorld.isGameOver()) {
                glyphLayoutFontShadow.setText(AssetLoader.shadow, LABEL_GAME_OVER);
                glyphLayoutFont.setText(AssetLoader.shadow, LABEL_GAME_OVER);
                AssetLoader.shadow.draw(batcher, LABEL_GAME_OVER, midPointX - (glyphLayoutFontShadow.width / 2), 56);
                AssetLoader.font.draw(batcher, LABEL_GAME_OVER, midPointX - (glyphLayoutFont.width / 2), 55);

                glyphLayoutFontShadow.setText(AssetLoader.shadow, LABEL_AGAIN);
                glyphLayoutFont.setText(AssetLoader.shadow, LABEL_AGAIN);
                AssetLoader.shadow.draw(batcher, LABEL_AGAIN, midPointX - (glyphLayoutFontShadow.width / 2), 76);
                AssetLoader.font.draw(batcher, LABEL_AGAIN, midPointX - (glyphLayoutFont.width / 2), 75);
            }

            // Convert integer into String
            String score = myWorld.getScore() + "";

            // Draw shadow first
            AssetLoader.shadow.draw(batcher, "" + myWorld.getScore(), (136 / 2)
                    - (3 * score.length()), 12);
            // Draw text
            AssetLoader.font.draw(batcher, "" + myWorld.getScore(), (136 / 2)
                    - (3 * score.length() - 1), 11);
        }

        batcher.end();
    }

    private void initGameObjects() {
        glyphLayoutFont = new GlyphLayout();
        glyphLayoutFontShadow = new GlyphLayout();

        bird = myWorld.getBird();
        scroller = myWorld.getScroller();
        frontGrass = scroller.getFrontGrass();
        backGrass = scroller.getBackGrass();
        pipe1 = scroller.getPipe1();
        pipe2 = scroller.getPipe2();
        pipe3 = scroller.getPipe3();
    }

    private void initAssets() {
        bg = AssetLoader.bg;
        grass = AssetLoader.grass;
        birdAnimation = AssetLoader.birdAnimation;
        birdMid = AssetLoader.bird;
        birdDown = AssetLoader.birdDown;
        birdUp = AssetLoader.birdUp;
        skullUp = AssetLoader.skullUp;
        skullDown = AssetLoader.skullDown;
        bar = AssetLoader.bar;
    }

    private void drawGrass() {
        // Draw the grass
        batcher.draw(grass, frontGrass.getX(), frontGrass.getY(),
                frontGrass.getWidth(), frontGrass.getHeight());
        batcher.draw(grass, backGrass.getX(), backGrass.getY(),
                backGrass.getWidth(), backGrass.getHeight());
    }

    private void drawSkulls() {
        // Temporary code! Sorry about the mess :)
        // We will fix this when we finish the Pipe class.

        batcher.draw(skullUp, pipe1.getX() - 1,
                pipe1.getY() + pipe1.getHeight() - 14, 24, 14);
        batcher.draw(skullDown, pipe1.getX() - 1,
                pipe1.getY() + pipe1.getHeight() + 45, 24, 14);

        batcher.draw(skullUp, pipe2.getX() - 1,
                pipe2.getY() + pipe2.getHeight() - 14, 24, 14);
        batcher.draw(skullDown, pipe2.getX() - 1,
                pipe2.getY() + pipe2.getHeight() + 45, 24, 14);

        batcher.draw(skullUp, pipe3.getX() - 1,
                pipe3.getY() + pipe3.getHeight() - 14, 24, 14);
        batcher.draw(skullDown, pipe3.getX() - 1,
                pipe3.getY() + pipe3.getHeight() + 45, 24, 14);
    }

    private void drawPipes() {
        // Temporary code! Sorry about the mess :)
        // We will fix this when we finish the Pipe class.
        batcher.draw(bar, pipe1.getX(), pipe1.getY(), pipe1.getWidth(),
                pipe1.getHeight());
        batcher.draw(bar, pipe1.getX(), pipe1.getY() + pipe1.getHeight() + 45,
                pipe1.getWidth(), midPointY + 66 - (pipe1.getHeight() + 45));

        batcher.draw(bar, pipe2.getX(), pipe2.getY(), pipe2.getWidth(),
                pipe2.getHeight());
        batcher.draw(bar, pipe2.getX(), pipe2.getY() + pipe2.getHeight() + 45,
                pipe2.getWidth(), midPointY + 66 - (pipe2.getHeight() + 45));

        batcher.draw(bar, pipe3.getX(), pipe3.getY(), pipe3.getWidth(),
                pipe3.getHeight());
        batcher.draw(bar, pipe3.getX(), pipe3.getY() + pipe3.getHeight() + 45,
                pipe3.getWidth(), midPointY + 66 - (pipe3.getHeight() + 45));
    }

}

