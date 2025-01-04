package io.github.diegobloise.flappybird;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main implements ApplicationListener {

    private final int SCREEN_HEIGHT = 480;
    private final int SCREEN_WIDTH = 320;

    private final float GRAVITY = 35;
    // private final float GRAVITY = 0;
    private final float FLAP_FORCE = 25 * GRAVITY;
    private final float FLY_SPEED = -50;

    private float deltaTime;

    private final float BIRD_POSITION_X = 80;
    private float birdVelocity;

    private final int VERTICAL_PIPE_GAP = 50;
    private final int HORIZONTAL_PIPE_GAP = 150;
    private final float MIN_PIPES_HEIGHT = (SCREEN_HEIGHT / 2) - 40;
    private final float MAX_PIPES_HEIGHT = (SCREEN_HEIGHT / 2) + 150;

    private List<Sprite> pipes;
    private List<Sprite> grounds;

    private ShapeRenderer shapeRenderer;

    private Texture backgroundTexture;
    private Texture groundTexture;
    private Texture birdTexture;
    private Texture pipeTexture;
    private Sound wingSound;

    private SpriteBatch spriteBatch;
    private FitViewport viewport;

    private Sprite birdSprite;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();

        backgroundTexture = new Texture("assets/sprites/background-day.png");
        groundTexture = new Texture("assets/sprites/base.png");
        birdTexture = new Texture("assets/sprites/yellowbird-downflap.png");
        pipeTexture = new Texture("assets/sprites/pipe-green.png");

        wingSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/wing.wav"));

        birdSprite = new Sprite(birdTexture);
        birdSprite.setX(BIRD_POSITION_X);

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT);

        createGround();

        resetGame();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
    }

    private void drawBackground() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
    }

    private void drawBird() {
        // shapeRenderer.begin(ShapeType.Filled);
        // shapeRenderer.setColor(Color.RED);
        // shapeRenderer.circle(birdPositionX, birdPosition, BIRD_RADIUS);
        // shapeRenderer.end();
        birdSprite.draw(spriteBatch);
    }

    private void drawPipes() {
        for (Sprite pipe : pipes) {
            pipe.draw(spriteBatch);
        }
    }

    private void drawGround() {
        for (Sprite ground : grounds) {
            ground.draw(spriteBatch);
        }
    }

    private void input() {
        float worldHeight = viewport.getWorldHeight();
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (birdSprite.getY() < worldHeight - 50 && birdSprite.getY() > 0) {
                birdVelocity += FLAP_FORCE;
                wingSound.play();
            } else {
                birdVelocity = 0;
            }
        }
    }

    private void logic() {
        deltaTime = Gdx.graphics.getDeltaTime();

        pipesHandler();
        groundHandler();

        float worldHeight = viewport.getWorldHeight();
        float birdHeight = birdSprite.getHeight();

        birdVelocity -= GRAVITY;

        birdSprite.setY(birdSprite.getY() + birdVelocity * deltaTime);
        birdSprite.setY(MathUtils.clamp(birdSprite.getY(), 0, worldHeight - birdHeight));

        if (birdSprite.getY() < 1) {
            resetGame();
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.valueOf("#0066FF"));
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        drawBackground();
        drawBird();
        drawPipes();
        drawGround();

        spriteBatch.end();
    }

    private void pipesHandler() {
        // Move pipes towards player
        for (Sprite pipe : pipes) {
            pipe.translateX(FLY_SPEED * deltaTime);
        }

        // Add new pipes
        if (pipes.get(pipes.size() - 1).getX() < SCREEN_WIDTH - HORIZONTAL_PIPE_GAP) {
            createPipes();
        }

        // Remove pipes off the screen
        if (pipes.get(0).getX() < -pipeTexture.getWidth()) {
            pipes.remove(0);
        }
    }

    private void groundHandler() {
        // Move ground towards player
        for (Sprite ground : grounds) {
            ground.translateX(FLY_SPEED * deltaTime);
            // Move ground to front
            if (ground.getX() < -groundTexture.getWidth()) {
                ground.setX(groundTexture.getWidth() - 1);
            }
        }
    }

    private void resetGame() {
        birdSprite.setY(SCREEN_HEIGHT / 2);
        birdVelocity = 0;
        pipes = new ArrayList<>();
        createPipes();
    }

    private void createPipes() {
        Sprite pipeSprite;
        float randomPosition = MathUtils.random(MIN_PIPES_HEIGHT, MAX_PIPES_HEIGHT);
        Vector2 pipePosition = new Vector2(SCREEN_WIDTH, randomPosition);

        // Top pipe
        pipeSprite = new Sprite(pipeTexture);
        pipeSprite.setBounds(pipePosition.x, pipePosition.y + VERTICAL_PIPE_GAP, pipeTexture.getWidth(),
                pipeTexture.getHeight());
        pipeSprite.setFlip(false, true);
        pipes.add(pipeSprite);

        // Bottom pipe
        pipeSprite = new Sprite(pipeTexture);
        pipeSprite.setBounds(pipePosition.x, pipePosition.y - pipeTexture.getHeight() - VERTICAL_PIPE_GAP,
                pipeTexture.getWidth(),
                pipeTexture.getHeight());
        pipes.add(pipeSprite);
    }

    private void createGround() {
        grounds = new ArrayList<>();
        Sprite groundSprite;
        groundSprite = new Sprite(groundTexture);
        grounds.add(groundSprite);
        groundSprite = new Sprite(groundTexture);
        groundSprite.setX(groundTexture.getWidth());
        grounds.add(groundSprite);
    }
}
