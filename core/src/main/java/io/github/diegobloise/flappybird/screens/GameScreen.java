package io.github.diegobloise.flappybird.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.diegobloise.flappybird.FlappyBird;
import io.github.diegobloise.flappybird.entities.Bird;
import io.github.diegobloise.flappybird.entities.Ground;
import io.github.diegobloise.flappybird.entities.Pipe;

public class GameScreen implements Screen {

    final FlappyBird game;

    private final float GAME_SPEED = -40;
    private final int HORIZONTAL_PIPE_GAP = 70;

    private float deltaTime;

    private Bird bird;
    private Ground ground;

    private List<Pipe> pipes;

    private List<Texture> backgroundTextures;

    private Texture backgroundTexture;

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

    private Integer score;

    private ShapeRenderer shape;
    private Boolean debugMode;

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
        bird.dispose();
        for (Pipe pipe : pipes) {
            pipe.dispose();
        }
        ground.dispose();

        for (Texture texture : backgroundTextures) {
            texture.dispose();
        }

        backgroundTexture.dispose();

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

    private void drawGetReady() {
        game.batch.draw(
                getReadyTexture,
                (game.viewport.getWorldWidth() / 2) - getReadyTexture.getWidth() / 2,
                (game.viewport.getWorldHeight() / 2) - getReadyTexture.getHeight() / 2 + tapTexture.getHeight());
        game.batch.draw(
                tapTexture,
                (game.viewport.getWorldWidth() / 2) - tapTexture.getWidth() / 2,
                (game.viewport.getWorldHeight() / 2) - tapTexture.getHeight() / 2);
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched()) {
            if (!isPlaying) {
                isPlaying = true;
            }

            if (!gameOver) {
                if (bird.getSprite().getY() < game.viewport.getWorldHeight() - 25 && bird.getSprite().getY() > 0) {
                    // birdVelocity += FLAP_FORCE; += or = ?
                    bird.flap();
                    wingSound.play();
                } else {
                    bird.setVelocity(0);
                }
            }

            if (gameOver && birdIsOnGround) {
                resetGame();
            }

            if (Gdx.input.getY() < game.viewport.getScreenHeight() / 6) {
                debugMode = !debugMode;
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
                addPipes();
                removePipes();
                for (Pipe pipe : pipes) {
                    pipe.move(GAME_SPEED, deltaTime);
                }
            }
            bird.applyPhisics(ground.getGroundRectangle(), deltaTime);
        }

        if (!gameOver) {
            ground.playAnimation(GAME_SPEED, deltaTime);
        }

        bird.playAnimation(gameOver, isPlaying, deltaTime);

        if (isPlaying && !gameOver) {
            updateCollisions();
            checkCollisions();
        }

        if (gameOver && bird.getSprite().getRotation() < 20 && !dieSoundPlayed) {
            dieSound.play();
            dieSoundPlayed = true;
        }

        birdIsOnGround = bird.getSprite().getY() <= ground.getGroundRectangle().height;
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        drawBackground();
        if (!isPlaying) {
            drawGetReady();
        }

        for (Pipe pipe : pipes) {
            pipe.draw();
        }
        bird.draw();
        ground.draw();

        if (isPlaying && !gameOver && !debugMode) {
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

    private void resetGame() {
        bird = new Bird(this.game);
        ground = new Ground(this.game);

        pipes = new ArrayList<>();
        pipes.add(new Pipe(this.game));

        score = 0;
        isPlaying = false;
        gameOver = false;
        dieSoundPlayed = false;

        backgroundTexture = backgroundTextures.get(MathUtils.random(backgroundTextures.size() - 1));
    }

    private void updateCollisions() {
        for (Pipe pipe : pipes) {
            pipe.updateCollisions(GAME_SPEED, deltaTime);
        }
        bird.updateCollision();
    }

    private void checkCollision(Rectangle rectangle) {
        if (bird.getRectangle().overlaps(rectangle)) {
            gameOver = true;
            hitSound.play();
        }
    }

    private void checkCollisions() {
        for (Pipe pipe : pipes) {
            checkCollision(pipe.getTopRectangle());
            checkCollision(pipe.getBottomRectangle());
        }
        checkCollision(ground.getGroundRectangle());
        checkScore();
    }

    private void checkScore() {
        if (bird.getRectangle().overlaps(pipes.get(0).getScoreRectangle())) {
            score++;
            pipes.get(0).setScoreRectangle(new Rectangle());
            pointSound.play();
        }
    }

    private void addPipes() {
        if (pipes.get(pipes.size() - 1).getBottomRectangle().getX() < game.viewport.getWorldWidth()
                - HORIZONTAL_PIPE_GAP) {
            pipes.add(new Pipe(this.game));
        }
    }

    private void removePipes() {
        if (pipes.get(0).getBottomSprite().getX() < -pipes.get(0).getBottomSprite().getWidth()) {
            pipes.remove(0);
        }
    }

    @SuppressWarnings("unused")
    private void debug() {
        if (debugMode) {
            shape.begin(ShapeType.Filled);

            // Pipes Collisions
            for (Pipe pipe : pipes) {
                shape.setColor(Color.RED);
                shape.rect(
                        pipe.getTopRectangle().getX() * 2,
                        pipe.getTopRectangle().getY() * 2,
                        pipe.getTopRectangle().getWidth() * 2,
                        pipe.getTopRectangle().getHeight() * 2);
                shape.rect(
                        pipe.getBottomRectangle().getX() * 2,
                        pipe.getBottomRectangle().getY() * 2,
                        pipe.getBottomRectangle().getWidth() * 2,
                        pipe.getBottomRectangle().getHeight() * 2);
            }

            // Score area
            for (Pipe pipe : pipes) {
                shape.setColor(Color.GREEN);
                shape.rect(
                        pipe.getScoreRectangle().getX() * 2,
                        pipe.getScoreRectangle().getY() * 2,
                        pipe.getScoreRectangle().getWidth() * 2,
                        pipe.getScoreRectangle().getHeight() * 2);
            }

            // Bird Collision
            shape.setColor(Color.YELLOW);
            shape.rect(
                    bird.getRectangle().getX() * 2,
                    bird.getRectangle().getY() * 2,
                    bird.getRectangle().getWidth() * 2,
                    bird.getRectangle().getHeight() * 2);

            // Ground Collision
            shape.setColor(Color.RED);
            shape.rect(
                    ground.getGroundRectangle().getX() * 2,
                    ground.getGroundRectangle().getY() * 2,
                    ground.getGroundRectangle().getWidth() * 2,
                    ground.getGroundRectangle().getHeight() * 2);

            shape.end();

            // Debug text
            game.batch.begin();
            game.font.getData().setScale(0.55f);
            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, "COLLISIONS: " + pipes.size() * 2, 0,
                    game.viewport.getWorldHeight());
            game.font.draw(game.batch, "SCORE AREAS: " + pipes.size(), 0,
                    game.viewport.getWorldHeight() - 14);
            game.font.draw(game.batch, "TOTAL PIPES: " + pipes.size() * 2, 0,
                    game.viewport.getWorldHeight() - 28);
            game.font.draw(game.batch, "SCORE: " + score, 0,
                    game.viewport.getWorldHeight() - 50);
            game.batch.end();
        } else {
            game.font.getData().setScale(1f);
        }
    }
}
