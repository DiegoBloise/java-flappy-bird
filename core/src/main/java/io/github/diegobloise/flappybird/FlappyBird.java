package io.github.diegobloise.flappybird;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class FlappyBird extends Game {

    public final int SCREEN_WIDTH = 144;
    public final int SCREEN_HEIGHT = 256;

    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;

    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT);

        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        screen.dispose();
    }
}
