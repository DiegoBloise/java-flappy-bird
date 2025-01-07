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
    private final float FLY_SPEED = -90;
    private final float BIRD_ROTATION_SPEED = 5;

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
    private List<Rectangle> pipeRectangles;

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
    private Rectangle groundRectangle;

    private Integer score;

    private ShapeRenderer shape;

    private Boolean debugMode;

    public GameScreen(final FlappyBird game) {
        this.game = game;

        backgroundTextures = new ArrayList<>();
        backgroundTextures.add(new Texture(Gdx.files.internal("sprites/background-day.png")));
        backgroundTextures.add(new Texture(Gdx.files.internal("sprites/background-night.png")));
        backgroundTexture = backgroundTextures.get(0);

        birdTextures = new ArrayList<>();
        birdTextures.add(new Texture(Gdx.files.internal("sprites/yellowbird-midflap.png")));
        birdTextures.add(new Texture(Gdx.files.internal("sprites/redbird-midflap.png")));
        birdTextures.add(new Texture(Gdx.files.internal("sprites/bluebird-midflap.png")));
        birdTexture = birdTextures.get(2);

        pipeTextures = new ArrayList<>();
        pipeTextures.add(new Texture(Gdx.files.internal("sprites/pipe-green.png")));
        pipeTextures.add(new Texture(Gdx.files.internal("sprites/pipe-red.png")));
        pipeTexture = pipeTextures.get(0);

        groundTexture = new Texture("sprites/base.png");

        wingSound = Gdx.audio.newSound(Gdx.files.internal("audio/wing.wav"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("audio/hit.wav"));
        pointSound = Gdx.audio.newSound(Gdx.files.internal("audio/point.wav"));

        birdSprite = new Sprite(birdTexture);
        birdSprite.setX(BIRD_POSITION_X);
        birdSprite.setOriginCenter();
        birdSprite.setOrigin(birdSprite.getOriginX() + 2, birdSprite.getOriginY());

        birdRectangle = new Rectangle();
        groundRectangle = new Rectangle(0, 0, groundTexture.getWidth(), groundTexture.getHeight());

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
        applyBirdPhisics();

        groundAnimation();
        birdAnimation();

        updateCollisions();
        checkCollisions();
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
        }

        // Add new pipes
        if (pipes.get(pipes.size() - 1).getX() < game.viewport.getWorldWidth() - HORIZONTAL_PIPE_GAP) {
            createPipes();
        }

        // Remove pipes off the screen
        if (pipes.get(0).getX() < -pipeTexture.getWidth()) {
            pipes.remove(0);
            pipeRectangles.remove(0);
        }
    }

    private void groundAnimation() {
        for (Sprite ground : grounds) {
            ground.translateX(FLY_SPEED * deltaTime);

            // Move ground to front
            if (ground.getX() < -groundTexture.getWidth()) {
                ground.setX(groundTexture.getWidth() - 4);
            }
        }
    }

    private void updateCollisions() {
        for (Rectangle scoreRectangle : scoreRectangles) {
            scoreRectangle.setX(scoreRectangle.getX() + FLY_SPEED * deltaTime);
        }
        for (Rectangle pipe : pipeRectangles) {
            pipe.setX(pipe.getX() + FLY_SPEED * deltaTime);
        }
        // Bird CollisionBox
        birdRectangle.set(
                birdSprite.getX() + birdSprite.getWidth() / 2,
                birdSprite.getY() + birdSprite.getHeight() / 2,
                birdSprite.getHeight() - 7,
                birdSprite.getHeight() - 7);
        birdRectangle.setCenter(birdSprite.getX() + birdSprite.getWidth() / 2,
                birdSprite.getY() + birdSprite.getHeight() / 2);
    }

    private void resetGame() {
        backgroundTexture = backgroundTextures.get(MathUtils.random(backgroundTextures.size() - 1));
        pipeTexture = pipeTextures.get(MathUtils.random(pipeTextures.size() - 1));
        birdTexture = birdTextures.get(MathUtils.random(birdTextures.size() - 1));

        birdSprite.setTexture(birdTexture);
        birdSprite.setY(game.viewport.getWorldHeight() / 2);
        birdVelocity = 550;

        pipes = new ArrayList<>();
        scoreRectangles = new ArrayList<>();
        pipeRectangles = new ArrayList<>();

        createPipes();

        score = 0;
    }

    private void createPipes() {
        Sprite pipeSprite;
        float randomPosition = MathUtils.random(minPipesHeight, maxPipesHeight);
        Vector2 pipePosition = new Vector2(game.viewport.getWorldWidth(), randomPosition);

        // Create Top pipe
        pipeSprite = new Sprite(pipeTexture);
        pipeSprite.setBounds(pipePosition.x, pipePosition.y + VERTICAL_PIPE_GAP, pipeTexture.getWidth(),
                pipeTexture.getHeight());
        pipeSprite.setFlip(false, true);
        pipes.add(pipeSprite);
        pipeRectangles.add(new Rectangle(pipePosition.x, pipePosition.y + VERTICAL_PIPE_GAP, pipeTexture.getWidth(),
                pipeTexture.getHeight()));

        // Create Bottom pipe
        pipeSprite = new Sprite(pipeTexture);
        pipeSprite.setBounds(pipePosition.x, pipePosition.y - pipeTexture.getHeight() - VERTICAL_PIPE_GAP,
                pipeTexture.getWidth(),
                pipeTexture.getHeight());
        pipes.add(pipeSprite);
        pipeRectangles.add(new Rectangle(pipePosition.x, pipePosition.y - pipeTexture.getHeight() - VERTICAL_PIPE_GAP,
                pipeTexture.getWidth(),
                pipeTexture.getHeight()));

        // Create Score Area
        scoreRectangles.add(new Rectangle(
                pipePosition.x + pipeTexture.getWidth() + 15,
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
        groundSprite.setX(groundTexture.getWidth() - 2);
        groundSprite.setY(0);
        grounds.add(groundSprite);
    }

    private void checkCollision(Rectangle rectangle) {
        if (birdRectangle.overlaps(rectangle)) {
            hitSound.play();
            resetGame();
        }
    }

    private void checkScore() {
        if (birdRectangle.overlaps(scoreRectangles.get(0))) {
            score++;
            scoreRectangles.remove(0);
            pointSound.play();
        }
    }

    private void applyBirdPhisics() {
        birdVelocity -= GRAVITY;

        birdSprite.translateY(birdVelocity * deltaTime);
        birdSprite.setY(MathUtils.clamp(birdSprite.getY(), 0, game.viewport.getWorldHeight() - birdSprite.getHeight()));
    }

    private void birdAnimation() {
        // Bird rotation animation
        if (birdVelocity > 0) {
            birdSprite.setRotation(
                    MathUtils.lerpAngleDeg(birdSprite.getRotation(), 30.0f, (BIRD_ROTATION_SPEED + 25) * deltaTime));
        } else {
            birdSprite.setRotation(
                    MathUtils.lerpAngleDeg(birdSprite.getRotation(), -50.0f, (BIRD_ROTATION_SPEED - 2) * deltaTime));
        }
    }

    private void checkCollisions() {
        for (Rectangle pipeRectangle : pipeRectangles) {
            checkCollision(pipeRectangle);
        }
        checkCollision(groundRectangle);
        checkScore();
    }

    @SuppressWarnings("unused")
    private void debug() {
        if (debugMode) {
            shape.begin(ShapeType.Filled);

            // Pipes Collisions
            for (Rectangle rect : pipeRectangles) {
                shape.setColor(Color.RED);
                shape.rect(rect.getX(), rect.getY(), rect.getWidth(),
                        rect.getHeight());
            }

            // Score area
            for (Rectangle rect : scoreRectangles) {
                shape.setColor(Color.GREEN);
                shape.rect(rect.getX(), rect.getY(), rect.getWidth(),
                        rect.getHeight());
            }

            // Bird Collision
            shape.setColor(Color.YELLOW);
            shape.rect(birdRectangle.getX(), birdRectangle.getY(), birdRectangle.getWidth(),
                    birdRectangle.getHeight());

            // Ground Collision
            shape.setColor(Color.RED);
            shape.rect(groundRectangle.getX(), groundRectangle.getY(), groundRectangle.getWidth(),
                    groundRectangle.getHeight());

            shape.end();

            // Debug text
            game.batch.begin();
            game.font.setColor(Color.WHITE);
            game.font.getData().setScale(1);
            game.font.draw(game.batch, "PIPES COLLISIONS: " + pipeRectangles.size(), 0, game.viewport.getWorldHeight());
            game.font.draw(game.batch, "SCORE AREAS: " + scoreRectangles.size(), 0,
                    game.viewport.getWorldHeight() - 20);
            game.font.draw(game.batch, "TOTAL PIPES: " + pipes.size(), 0, game.viewport.getWorldHeight() - 40);
            game.font.draw(game.batch, "TOTAL GROUNDS: " + grounds.size(), 0, game.viewport.getWorldHeight() - 60);
            game.batch.end();
        }
    }
}
