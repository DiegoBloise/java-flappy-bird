package io.github.diegobloise.flappybird;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.diegobloise.flappybird.screens.MainMenuScreen;

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
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/nokiafc22.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        parameter.size = 24;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;
        parameter.mono = true;

        font = generator.generateFont(parameter);

        generator.dispose();

        font.setUseIntegerPositions(false);
        // font.getData().setScale(viewport.getWorldHeight() /
        // Gdx.graphics.getHeight());

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
