package io.github.diegobloise.flappybird;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    final FlappyBird game;

    private final float FLY_SPEED = -40;

    private float deltaTime;
    private float time;

    private Texture bootSplashTexture;
    private Texture groundTexture;
    private Texture birdTexture;
    private Texture btnPlayTexture;
    private Texture btnHighscoresTexture;
    private Texture btnRateTexture;

    private Animation<TextureRegion> birdAnimation;
    private float animationTime;

    private List<Sprite> grounds;

    private Sprite birdSprite;

    public MainMenuScreen(final FlappyBird game) {
        this.game = game;

        bootSplashTexture = new Texture(Gdx.files.internal("sprites/boot_splash.png"));
        btnPlayTexture = new Texture("sprites/btn_play.png");
        btnHighscoresTexture = new Texture("sprites/btn_highscores.png");
        btnRateTexture = new Texture("sprites/btn_rate.png");

        groundTexture = new Texture("sprites/ground.png");
        birdTexture = new Texture(Gdx.files.internal("sprites/bird_1.png"));

        initializeBirdAnimations();
        TextureRegion currentFrame = birdAnimation.getKeyFrame(animationTime);
        birdSprite = new Sprite(currentFrame);
        birdSprite.setOriginCenter();
        birdSprite.setOrigin(birdSprite.getOriginX() + 2, birdSprite.getOriginY());

        Vector2 birdStartPosition = new Vector2(game.viewport.getWorldWidth() / 2, game.viewport.getWorldHeight() / 2);

        initializeBirdAnimations();

        birdSprite.setRegion(birdAnimation.getKeyFrame(0)); // Define o primeiro quadro
        birdSprite.setPosition(birdStartPosition.x - birdSprite.getWidth() / 2,
                birdStartPosition.y - birdSprite.getHeight() / 2);

        grounds = new ArrayList<>();
        Sprite groundSprite;
        groundSprite = new Sprite(groundTexture);
        grounds.add(groundSprite);
        groundSprite = new Sprite(groundTexture);
        groundSprite.setX(groundTexture.getWidth() - 2);
        grounds.add(groundSprite);
    }

    public void show() {
    }

    public void render(float delta) {
        deltaTime = Gdx.graphics.getDeltaTime();

        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        animationTime += deltaTime;
        TextureRegion currentFrame = birdAnimation.getKeyFrame(animationTime);
        birdSprite.setRegion(currentFrame);

        time += deltaTime;
        float floatingOffset = (float) Math.sin(time * 7) * 2;
        float baseY = (game.viewport.getWorldHeight() / 2 + 7) + 10 - birdSprite.getHeight() / 2f;
        birdSprite.setY(baseY + floatingOffset);

        for (Sprite ground : grounds) {
            ground.translateX(FLY_SPEED * deltaTime);
            if (ground.getX() < -groundTexture.getWidth()) {
                ground.setX(groundTexture.getWidth() - 4);
            }
        }

        game.batch.begin();

        game.batch.draw(bootSplashTexture, 0, 10, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        birdSprite.draw(game.batch);
        for (Sprite ground : grounds) {
            ground.draw(game.batch);
        }

        game.batch.draw(
                btnPlayTexture,
                (game.viewport.getWorldWidth() / 2) - btnPlayTexture.getWidth() - 7,
                (game.viewport.getWorldHeight() / 2) - btnPlayTexture.getHeight() / 2 - 57 + 5);
        game.batch.draw(
                btnHighscoresTexture,
                (game.viewport.getWorldWidth() / 2) + 7,
                (game.viewport.getWorldHeight() / 2) - btnHighscoresTexture.getHeight() / 2 - 57
                        + 5);
        game.batch.draw(
                btnRateTexture,
                (game.viewport.getWorldWidth() / 2) - btnRateTexture.getWidth() / 2,
                (game.viewport.getWorldHeight() / 2) - 20);

        game.batch.end();

        if (Gdx.input.justTouched()) {
            dispose();
            game.setScreen(new GameScreen(game));
        }
    }

    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    public void pause() {
    }

    public void resume() {
    }

    public void hide() {
    }

    public void dispose() {
        bootSplashTexture.dispose();
        groundTexture.dispose();
        birdTexture.dispose();
        btnPlayTexture.dispose();
        btnHighscoresTexture.dispose();
        btnRateTexture.dispose();
    }

    private void initializeBirdAnimations() {
        int frameWidth = birdTexture.getWidth() / 4;
        int frameHeight = birdTexture.getHeight();
        TextureRegion[][] regions = TextureRegion.split(birdTexture, frameWidth, frameHeight);

        TextureRegion[] frames = regions[0];

        birdAnimation = new Animation<>(0.1f, frames);
        birdAnimation.setPlayMode(Animation.PlayMode.LOOP);

        animationTime = 0;
    }
}
