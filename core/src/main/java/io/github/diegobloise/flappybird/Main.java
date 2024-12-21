package io.github.diegobloise.flappybird;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main implements ApplicationListener {

    private final float SCREEN_HEIGHT = 480;
    private final float SCREEN_WIDTH = 640;

    private final float GRAVITY = 35;
    private final float FLAP_FORCE = 25 * GRAVITY;

    private float deltaTime;

    private float BIRD_RADIUS = 10;

    private float birdPosition;
    private float birdVelocity;

    private final int PIPE_WIDTH = 65;
    private final int PIPE_HEIGHT = 500;
    private final int PIPE_GAP = 55;

    private int pipeCount;

    private List<Vector2> pipes;

    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        resetGame();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        deltaTime = Gdx.graphics.getDeltaTime();
        handleInput();
        gameLogic();
        pipes();
        ScreenUtils.clear(Color.valueOf("#0066FF"));
        drawBird();
        drawPipes();
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
    }

    private void drawBird() {
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(100, birdPosition, BIRD_RADIUS);
        shapeRenderer.end();
    }

    private void drawPipes() {
        for (Vector2 pipe : pipes) {
            // Top pipe
            shapeRenderer.begin(ShapeType.Filled);
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(pipe.x, pipe.y + PIPE_GAP, PIPE_WIDTH, PIPE_HEIGHT);
            shapeRenderer.end();

            // Bottom pipe
            shapeRenderer.begin(ShapeType.Filled);
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(pipe.x, pipe.y - PIPE_HEIGHT - PIPE_GAP, PIPE_WIDTH, PIPE_HEIGHT);
            shapeRenderer.end();
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (birdPosition < SCREEN_HEIGHT - 50) {
                birdVelocity += FLAP_FORCE;
            } else if (birdPosition > SCREEN_HEIGHT) {
                birdVelocity = 0;
                birdPosition = SCREEN_HEIGHT + BIRD_RADIUS;
            }
        }
    }

    private void gameLogic() {
        birdVelocity -= GRAVITY;

        birdPosition += birdVelocity * deltaTime;

        if (birdPosition < -50) {
            resetGame();
        }
    }

    private void pipes() {

        // Move pipes towards player
        for (Vector2 pipe : pipes) {
            pipe.x -= 50 * deltaTime;
        }

        // Add new pipes
        if (pipes.get(pipeCount).x < SCREEN_WIDTH - 180) {
            pipeCount++;
            addNewPipe();
            System.out.println(pipes.size());
        }

        // Remove pipes off the screen
        if (pipes.get(0).x < -PIPE_WIDTH) {
            pipeCount--;
            pipes.remove(0);
        }
    }

    private void resetGame() {
        birdPosition = SCREEN_HEIGHT / 2;
        birdVelocity = 0;
        pipes = new ArrayList<>();
        pipeCount = 0;
        addNewPipe();
    }

    private void addNewPipe() {
        float randomPosition = MathUtils.random((SCREEN_HEIGHT / 2) - 100, (SCREEN_HEIGHT / 2) + 100);
        Vector2 pipePosition = new Vector2(SCREEN_WIDTH, randomPosition);
        pipes.add(pipePosition);
    }
}
