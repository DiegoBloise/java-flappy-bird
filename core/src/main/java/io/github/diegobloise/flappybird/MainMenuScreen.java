package io.github.diegobloise.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    final FlappyBird game;

    public MainMenuScreen(final FlappyBird game) {
        this.game = game;
    }

    public void show() {
    }

    public void render(float delta) {
        ScreenUtils.clear(Color.WHITE);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        game.font.setColor(Color.BLACK);
        game.font.draw(game.batch, "Java Flappy Bird ",
                90,
                (game.viewport.getWorldHeight() / 2) + 30);
        game.font.draw(game.batch, "Tap anywhere to begin!",
                90,
                (game.viewport.getWorldHeight() / 2) - 10);

        game.batch.end();

        if (Gdx.input.isTouched()) {
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
    }
}
