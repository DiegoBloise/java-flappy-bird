package io.github.diegobloise.flappybird;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {

    final FlappyBird game;

    private final float GRAVITY = 35;
    private final float FLAP_FORCE = 13 * GRAVITY;
    private final float FLY_SPEED = -50;

    private float deltaTime;

    private final float BIRD_POSITION_X = 80;
    private float birdVelocity;

    private final int VERTICAL_PIPE_GAP = 50;
    private final int HORIZONTAL_PIPE_GAP = 150;
    private final float minPipesHeight;
    private final float maxPipesHeight;

    private List<Sprite> pipes;
    private List<Sprite> grounds;
    private List<Rectangle> scoreRectangles;

    private List<Texture> backgroundTextures;
    private List<Texture> birdTextures;
    private List<Texture> pipeTextures;

    private Texture backgroundTexture;
    private Texture groundTexture;
    private Texture birdTexture;
    private Texture pipeTexture;

    private Sound wingSound;
    private Sound hitSound;
    private Sound pointSound;

    private Sprite birdSprite;

    private Rectangle birdRectangle;
    private Rectangle pipeRectangle;
    private Rectangle groundRectangle;

    private Integer score;

    private ShapeRenderer shape;

    private Boolean debugMode;

    private final float BIRD_ROTATION_SPEED = 5;

    public GameScreen(final FlappyBird game) {
        this.game = game;

        backgroundTextures = new ArrayList<>();
        backgroundTextures.add(new Texture("assets/sprites/background-day.png"));
        backgroundTextures.add(new Texture("assets/sprites/background-night.png"));
        backgroundTexture = backgroundTextures.get(0);

        birdTextures = new ArrayList<>();
        birdTextures.add(new Texture("assets/sprites/yellowbird-downflap.png"));
        birdTextures.add(new Texture("assets/sprites/redbird-downflap.png"));
        birdTextures.add(new Texture("assets/sprites/bluebird-downflap.png"));
        birdTexture = birdTextures.get(0);

        pipeTextures = new ArrayList<>();
        pipeTextures.add(new Texture("assets/sprites/pipe-green.png"));
        pipeTextures.add(new Texture("assets/sprites/pipe-red.png"));
        pipeTexture = pipeTextures.get(0);

        groundTexture = new Texture("assets/sprites/base.png");

        wingSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/wing.wav"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/hit.wav"));
        pointSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/point.wav"));

        birdSprite = new Sprite(birdTexture);
        birdSprite.setX(BIRD_POSITION_X);
        birdSprite.setOriginCenter();
        birdSprite.setOrigin(birdSprite.getOriginX() + 2, birdSprite.getOriginY());

        birdRectangle = new Rectangle();
        pipeRectangle = new Rectangle();
        groundRectangle = new Rectangle();

        minPipesHeight = (game.SCREEN_HEIGHT / 2) - 40;
        maxPipesHeight = (game.SCREEN_HEIGHT / 2) + 150;

        shape = new ShapeRenderer();

        debugMode = false;

        createGround();

        resetGame();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.graphics.setTitle("Java Flappy Bird (FPS: " + Integer.toString(Gdx.graphics.getFramesPerSecond()) + ")");
        input();
        logic();
        draw();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        for (Texture texture : backgroundTextures) {
            texture.dispose();
        }
        for (Texture texture : birdTextures) {
            texture.dispose();
        }
        for (Texture texture : pipeTextures) {
            texture.dispose();
        }
        backgroundTexture.dispose();
        birdTexture.dispose();
        pipeTexture.dispose();
        groundTexture.dispose();
        wingSound.dispose();
        hitSound.dispose();
        pointSound.dispose();
    }

    private void drawBackground() {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        game.batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
    }

    private void drawBird() {
        birdSprite.draw(game.batch);
    }

    private void drawPipes() {
        for (Sprite pipe : pipes) {
            pipe.draw(game.batch);
        }
    }

    private void drawGround() {
        for (Sprite ground : grounds) {
            ground.draw(game.batch);
        }
    }

    private void input() {
        float worldHeight = game.viewport.getWorldHeight();
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched()) {
            if (birdSprite.getY() < worldHeight - 50 && birdSprite.getY() > 0) {
                // birdVelocity += FLAP_FORCE; += or = ?
                birdVelocity = FLAP_FORCE;
                wingSound.play();
            } else {
                birdVelocity = 0;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            debugMode = !debugMode;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetGame();
        }
    }

    private void logic() {
        deltaTime = Gdx.graphics.getDeltaTime();

        pipesHandler();
        groundHandler();
        scoreHandler();

        float worldHeight = game.viewport.getWorldHeight();
        float birdHeight = birdSprite.getHeight();

        birdVelocity -= GRAVITY;

        birdSprite.translateY(birdVelocity * deltaTime);
        birdSprite.setY(MathUtils.clamp(birdSprite.getY(), 0, worldHeight - birdHeight));

        if (birdVelocity > 0) {
            birdSprite.setRotation(
                    MathUtils.lerpAngleDeg(birdSprite.getRotation(), 50.0f, (BIRD_ROTATION_SPEED + 5) * deltaTime));
        } else {
            birdSprite.setRotation(
                    MathUtils.lerpAngleDeg(birdSprite.getRotation(), -50.0f, BIRD_ROTATION_SPEED * deltaTime));
        }

        // Bird CollisionBox
        birdRectangle.set(
                birdSprite.getX() + birdSprite.getWidth() / 2,
                birdSprite.getY() + birdSprite.getHeight() / 2,
                birdSprite.getHeight() - 7,
                birdSprite.getHeight() - 7);
        birdRectangle.setCenter(birdSprite.getX() + birdSprite.getWidth() / 2,
                birdSprite.getY() + birdSprite.getHeight() / 2);

        checkScore();
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        drawBackground();
        drawBird();
        drawPipes();
        drawGround();

        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(2);
        game.font.draw(game.batch, score.toString(), game.viewport.getWorldWidth() / 2,
                game.viewport.getWorldHeight() - 50);

        game.batch.end();

        debug();
    }

    private void pipesHandler() {
        // Move pipes towards player
        for (Sprite pipe : pipes) {
            pipe.translateX(FLY_SPEED * deltaTime);
            pipeRectangle.set(pipe.getX(), pipe.getY(), pipe.getWidth(), pipe.getHeight());
            checkCollision(pipeRectangle);
        }

        // Add new pipes
        if (pipes.get(pipes.size() - 1).getX() < game.viewport.getWorldWidth() - HORIZONTAL_PIPE_GAP) {
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
            groundRectangle.set(ground.getX(), ground.getY(), ground.getWidth(), ground.getHeight());
            checkCollision(groundRectangle);
        }
    }

    private void scoreHandler() {
        for (Rectangle scoreRectangle : scoreRectangles) {
            scoreRectangle.setX(scoreRectangle.getX() + FLY_SPEED * deltaTime);
        }
    }

    private void resetGame() {
        backgroundTexture = backgroundTextures.get(MathUtils.random(backgroundTextures.size() - 1));
        pipeTexture = pipeTextures.get(MathUtils.random(pipeTextures.size() - 1));
        birdTexture = birdTextures.get(MathUtils.random(birdTextures.size() - 1));

        birdSprite.setY(game.viewport.getWorldHeight() / 2);
        birdVelocity = 550;

        pipes = new ArrayList<>();
        scoreRectangles = new ArrayList<>();

        createPipes();

        score = 0;
    }

    private void createPipes() {
        Sprite pipeSprite;
        float randomPosition = MathUtils.random(minPipesHeight, maxPipesHeight);
        Vector2 pipePosition = new Vector2(game.viewport.getWorldWidth(), randomPosition);

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

        // Score Area
        scoreRectangles.add(new Rectangle(
                pipePosition.x + pipeTexture.getWidth() + 20,
                pipePosition.y - VERTICAL_PIPE_GAP,
                30,
                VERTICAL_PIPE_GAP * 2));
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

    private void checkCollision(Rectangle rectangle) {
        if (birdRectangle.overlaps(rectangle)) {
            hitSound.play();
            resetGame();
        }
    }

    private void checkScore() {
        for (int i = scoreRectangles.size() - 1; i >= 0; i--) {
            if (birdRectangle.overlaps(scoreRectangles.get(i))) {
                score++;
                scoreRectangles.remove(i);
                pointSound.play();
            }
        }
    }

    @SuppressWarnings("unused")
    private void debug() {
        if (debugMode) {
            for (Rectangle rect : scoreRectangles) {
                shape.begin(ShapeType.Filled);
                shape.setColor(Color.RED);
                shape.rect(rect.getX(), rect.getY(), rect.getWidth(),
                        rect.getHeight());
                shape.end();
            }

            shape.begin(ShapeType.Filled);
            shape.setColor(Color.GREEN);
            shape.rect(birdRectangle.getX(), birdRectangle.getY(), birdRectangle.getWidth(),
                    birdRectangle.getHeight());
            shape.end();
        }
    }
}
