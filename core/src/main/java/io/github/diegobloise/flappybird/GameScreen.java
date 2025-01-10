package io.github.diegobloise.flappybird;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {

    final FlappyBird game;

    private final float GRAVITY = 18;
    private final float FLAP_FORCE = 14 * GRAVITY;
    private final float FLY_SPEED = -40;
    private final float BIRD_ROTATION_SPEED = 5;

    private float deltaTime;
    private float time;

    private Vector2 birdStartPosition;
    private float birdVelocity;

    private final int VERTICAL_PIPE_GAP = 27;
    private final int HORIZONTAL_PIPE_GAP = 70;
    private final float minPipesHeight;
    private final float maxPipesHeight;

    private List<Sprite> pipes;
    private List<Rectangle> scoreRectangles;
    private List<Rectangle> pipeRectangles;

    private List<Texture> backgroundTextures;
    private List<Texture> birdTextures;
    private List<Texture> pipeTextures;

    private Texture backgroundTexture;
    private Texture groundTexture;
    private Texture birdTexture;
    private Texture pipeTexture;
    private Texture tapTexture;
    private Texture getReadyTexture;
    private Texture scoreTexture;
    private Texture gameOverTexture;
    private Texture btnPlayTexture;
    private Texture btnHighscoresTexture;

    private Sound wingSound;
    private Sound hitSound;
    private Sound dieSound;
    private Sound pointSound;

    private Sprite birdSprite;
    private Sprite groundSprite;

    private Rectangle birdRectangle;
    private Rectangle groundRectangle;

    private Integer score;

    private ShapeRenderer shape;

    private Boolean debugMode;

    private static final int QUANTITY_OF_FRAMES = 4;
    private float animationTime;
    private Animation<TextureRegion> birdAnimation;

    private boolean isPlaying;

    private boolean gameOver;

    private boolean dieSoundPlayed;

    private boolean birdIsOnGround;

    public GameScreen(final FlappyBird game) {
        this.game = game;

        backgroundTextures = new ArrayList<>();
        backgroundTextures.add(new Texture(Gdx.files.internal("sprites/background_day.png")));
        backgroundTextures.add(new Texture(Gdx.files.internal("sprites/background_night.png")));
        backgroundTexture = backgroundTextures.get(0);

        birdTextures = new ArrayList<>();
        birdTextures.add(new Texture(Gdx.files.internal("sprites/bird_1.png")));
        birdTextures.add(new Texture(Gdx.files.internal("sprites/bird_2.png")));
        birdTextures.add(new Texture(Gdx.files.internal("sprites/bird_3.png")));
        birdTexture = birdTextures.get(0);

        pipeTextures = new ArrayList<>();
        pipeTextures.add(new Texture(Gdx.files.internal("sprites/pipe_green.png")));
        pipeTextures.add(new Texture(Gdx.files.internal("sprites/pipe_red.png")));
        pipeTexture = pipeTextures.get(0);

        groundTexture = new Texture("sprites/ground.png");
        groundSprite = new Sprite(groundTexture);

        tapTexture = new Texture("sprites/tap.png");
        getReadyTexture = new Texture("sprites/get_ready.png");
        scoreTexture = new Texture("sprites/score.png");
        gameOverTexture = new Texture("sprites/game_over.png");
        btnPlayTexture = new Texture("sprites/btn_play.png");
        btnHighscoresTexture = new Texture("sprites/btn_highscores.png");

        wingSound = Gdx.audio.newSound(Gdx.files.internal("audio/sfx_wing.wav"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("audio/sfx_hit.wav"));
        dieSound = Gdx.audio.newSound(Gdx.files.internal("audio/sfx_die.wav"));
        pointSound = Gdx.audio.newSound(Gdx.files.internal("audio/sfx_point.wav"));

        initializeBirdAnimations();
        TextureRegion currentFrame = birdAnimation.getKeyFrame(animationTime);
        birdSprite = new Sprite(currentFrame);
        birdSprite.setOriginCenter();
        birdSprite.setOrigin(birdSprite.getOriginX() + 2, birdSprite.getOriginY());

        birdRectangle = new Rectangle();
        groundRectangle = new Rectangle(0, 0, groundTexture.getWidth(), groundTexture.getHeight());

        minPipesHeight = (game.viewport.getWorldHeight() / 2) - 20;
        maxPipesHeight = (game.viewport.getWorldHeight() / 2) + 70;

        shape = new ShapeRenderer();

        debugMode = false;

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
        tapTexture.dispose();
        getReadyTexture.dispose();
        gameOverTexture.dispose();
        btnPlayTexture.dispose();
        btnHighscoresTexture.dispose();

        wingSound.dispose();
        dieSound.dispose();
        pointSound.dispose();
    }

    private void drawBackground() {
        game.batch.draw(backgroundTexture, 0, 10, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
    }

    private void drawTap() {
        game.batch.draw(
                getReadyTexture,
                (game.viewport.getWorldWidth() / 2) - getReadyTexture.getWidth() / 2,
                (game.viewport.getWorldHeight() / 2) - getReadyTexture.getHeight() / 2 + tapTexture.getHeight());
        game.batch.draw(
                tapTexture,
                (game.viewport.getWorldWidth() / 2) - tapTexture.getWidth() / 2,
                (game.viewport.getWorldHeight() / 2) - tapTexture.getHeight() / 2);
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
        groundSprite.draw(game.batch);
    }

    private void drawGameOverScreen() {
        game.batch.draw(
                gameOverTexture,
                (game.viewport.getWorldWidth() / 2) - gameOverTexture.getWidth() / 2,
                (game.viewport.getWorldHeight() / 2) - gameOverTexture.getHeight() / 2 + tapTexture.getHeight());
        game.batch.draw(
                scoreTexture,
                (game.viewport.getWorldWidth() / 2) - scoreTexture.getWidth() / 2,
                (game.viewport.getWorldHeight() / 2) - scoreTexture.getHeight() / 2);
        game.batch.draw(
                btnPlayTexture,
                (game.viewport.getWorldWidth() / 2) - btnPlayTexture.getWidth() - 7,
                (game.viewport.getWorldHeight() / 2) - btnPlayTexture.getHeight() / 2 - scoreTexture.getHeight() + 5);
        game.batch.draw(
                btnHighscoresTexture,
                (game.viewport.getWorldWidth() / 2) + 7,
                (game.viewport.getWorldHeight() / 2) - btnHighscoresTexture.getHeight() / 2 - scoreTexture.getHeight()
                        + 5);
    }

    private void input() {
        float worldHeight = game.viewport.getWorldHeight();
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched()) {
            if (!isPlaying) {
                isPlaying = true;
            }

            if (!gameOver) {
                if (birdSprite.getY() < worldHeight - 25 && birdSprite.getY() > 0) {
                    // birdVelocity += FLAP_FORCE; += or = ?
                    birdVelocity = FLAP_FORCE;
                    wingSound.play();
                } else {
                    birdVelocity = 0;
                }
            }

            if (gameOver && birdIsOnGround) {
                resetGame();
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

        if (isPlaying) {
            if (!gameOver) {
                pipesHandler();
            }
            applyBirdPhisics();
        }

        if (!gameOver) {
            groundAnimation();
        }

        birdAnimation();

        if (isPlaying && !gameOver) {
            updateCollisions();
            checkCollisions();
        }

        if (gameOver && birdSprite.getRotation() < 20 && !dieSoundPlayed) {
            dieSound.play();
            dieSoundPlayed = true;
        }

        birdIsOnGround = birdSprite.getY() <= groundRectangle.height;
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        drawBackground();
        if (!isPlaying) {
            drawTap();
        }
        drawPipes();
        drawBird();
        drawGround();

        if (isPlaying && !gameOver) {
            GlyphLayout scoreText = new GlyphLayout();
            scoreText.setText(game.font, score.toString());

            game.font.setColor(Color.WHITE);
            game.font.draw(
                    game.batch,
                    scoreText,
                    (game.viewport.getWorldWidth() / 2) - scoreText.width / 2,
                    game.viewport.getWorldHeight() - 30);
        }

        if (gameOver && birdIsOnGround) {
            drawGameOverScreen();
        }

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
        groundSprite.translateX(FLY_SPEED * deltaTime);
        if (groundSprite.getX() + groundSprite.getWidth() < game.viewport.getWorldWidth()) {
            groundSprite.setX(0);
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
                birdSprite.getHeight() - 5,
                birdSprite.getHeight() - 5);
        birdRectangle.setCenter(birdSprite.getX() + birdSprite.getWidth() / 2,
                birdSprite.getY() + birdSprite.getHeight() / 2);
    }

    private void resetGame() {
        score = 0;
        isPlaying = false;
        gameOver = false;
        dieSoundPlayed = false;

        birdStartPosition = new Vector2(35, (game.viewport.getWorldHeight() / 2) + 10);

        backgroundTexture = backgroundTextures.get(MathUtils.random(backgroundTextures.size() - 1));
        pipeTexture = pipeTextures.get(MathUtils.random(pipeTextures.size() - 1));
        birdTexture = birdTextures.get(MathUtils.random(birdTextures.size() - 1));

        initializeBirdAnimations();

        birdSprite.setRegion(birdAnimation.getKeyFrame(0)); // Define o primeiro quadro
        birdSprite.setPosition(birdStartPosition.x, birdStartPosition.y);
        birdSprite.setRotation(0);
        birdVelocity = 0;

        pipes = new ArrayList<>();
        scoreRectangles = new ArrayList<>();
        pipeRectangles = new ArrayList<>();

        createPipes();
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
                pipePosition.x + pipeTexture.getWidth() + 5,
                pipePosition.y - (VERTICAL_PIPE_GAP * 1.5f),
                20,
                VERTICAL_PIPE_GAP * 3));
    }

    private void checkCollision(Rectangle rectangle) {
        if (birdRectangle.overlaps(rectangle)) {
            gameOver = true;
            hitSound.play();
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
        birdSprite.setY(MathUtils.clamp(birdSprite.getY(), groundRectangle.height - 5,
                game.viewport.getWorldHeight() - birdSprite.getHeight()));
    }

    private void initializeBirdAnimations() {
        Texture birdSheet = birdTextures.get(MathUtils.random(birdTextures.size() - 1));

        int frameWidth = birdSheet.getWidth() / QUANTITY_OF_FRAMES;
        int frameHeight = birdSheet.getHeight();
        TextureRegion[][] regions = TextureRegion.split(birdSheet, frameWidth, frameHeight);

        TextureRegion[] frames = regions[0];

        birdAnimation = new Animation<>(0.1f, frames);
        birdAnimation.setPlayMode(Animation.PlayMode.LOOP);

        animationTime = 0;
    }

    private void birdAnimation() {
        if (!gameOver) {
            // Flap animation
            animationTime += deltaTime;
            TextureRegion currentFrame = birdAnimation.getKeyFrame(animationTime);
            birdSprite.setRegion(currentFrame);
        }

        // Bird rotation animation
        if (isPlaying) {
            if (birdVelocity > 0) {
                birdSprite.setRotation(
                        MathUtils.lerpAngleDeg(birdSprite.getRotation(), 30.0f,
                                (BIRD_ROTATION_SPEED + 25) * deltaTime));
            } else {
                birdSprite.setRotation(
                        MathUtils.lerpAngleDeg(birdSprite.getRotation(), -50.0f,
                                (BIRD_ROTATION_SPEED - 2) * deltaTime));
            }
            if (gameOver) {
                birdSprite.setRotation(
                        MathUtils.lerpAngleDeg(birdSprite.getRotation(), -70.0f,
                                (BIRD_ROTATION_SPEED + 5) * deltaTime));
            }
        } else {
            // time += deltaTime;
            // float floatingOffset = (float) Math.sin(time * 2) * 5;
            // float baseY = (game.viewport.getWorldHeight() / 2) + 10 -
            // birdSprite.getHeight() / 2f;
            // birdSprite.setY(baseY + floatingOffset);
            time += deltaTime;
            float floatingOffset = (float) Math.sin(time * 7) * 2;
            float baseY = (game.viewport.getWorldHeight() / 2) + 10 - birdSprite.getHeight() / 2f;
            birdSprite.setY(baseY + floatingOffset);
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
            game.font.draw(game.batch, "PIPES COLLISIONS: " + pipeRectangles.size(), 0, game.viewport.getWorldHeight());
            game.font.draw(game.batch, "SCORE AREAS: " + scoreRectangles.size(), 0,
                    game.viewport.getWorldHeight() - 10);
            game.font.draw(game.batch, "TOTAL PIPES: " + pipes.size(), 0, game.viewport.getWorldHeight() - 20);
            game.batch.end();
        }
    }
}
