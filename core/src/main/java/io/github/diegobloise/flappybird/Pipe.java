package io.github.diegobloise.flappybird;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Pipe {
    private final int VERTICAL_PIPE_GAP = 27;

    final FlappyBird game;

    private final int HORIZONTAL_PIPE_GAP = 70;
    private final float minPipesHeight;
    private final float maxPipesHeight;

    private List<Sprite> pipes;
    private List<Rectangle> scoreRectangles;
    private List<Rectangle> pipeRectangles;

    private List<Texture> pipeTextures;
    private Texture pipeTexture;

    Pipe(FlappyBird game) {
        this.game = game;

        pipeTextures = new ArrayList<>();
        pipeTextures.add(new Texture(Gdx.files.internal("sprites/pipe_green.png")));
        pipeTextures.add(new Texture(Gdx.files.internal("sprites/pipe_red.png")));
        pipeTexture = pipeTextures.get(MathUtils.random(pipeTextures.size() - 1));

        minPipesHeight = (game.viewport.getWorldHeight() / 2) - 20;
        maxPipesHeight = (game.viewport.getWorldHeight() / 2) + 70;

        pipes = new ArrayList<>();
        scoreRectangles = new ArrayList<>();
        pipeRectangles = new ArrayList<>();

        createPipes();
    }

    public void dispose() {
        for (Texture texture : pipeTextures) {
            texture.dispose();
        }
        pipeTexture.dispose();
    }

    public void draw() {
        for (Sprite pipe : pipes) {
            pipe.draw(game.batch);
        }
    }

    public void update(float gameSpeed, float deltaTime) {
        movePipes(gameSpeed, deltaTime);
        addPipes();
        removePipes();
    }

    private void movePipes(float gameSpeed, float deltaTime) {
        for (Sprite pipe : pipes) {
            pipe.translateX(gameSpeed * deltaTime);
        }
    }

    private void addPipes() {
        if (pipes.get(pipes.size() - 1).getX() < game.viewport.getWorldWidth() - HORIZONTAL_PIPE_GAP) {
            createPipes();
        }
    }

    private void removePipes() {
        if (pipes.get(0).getX() < -pipeTexture.getWidth()) {
            pipes.remove(0);
            pipeRectangles.remove(0);
        }
    }

    public void updateCollisions(float gameSpeed, float deltaTime) {
        for (Rectangle scoreRectangle : scoreRectangles) {
            scoreRectangle.setX(scoreRectangle.getX() + gameSpeed * deltaTime);
        }
        for (Rectangle pipe : pipeRectangles) {
            pipe.setX(pipe.getX() + gameSpeed * deltaTime);
        }
    }

    public void createPipes() {
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

    public List<Rectangle> getScoreRectangles() {
        return scoreRectangles;
    }

    public void setScoreRectangles(List<Rectangle> scoreRectangles) {
        this.scoreRectangles = scoreRectangles;
    }

    public List<Rectangle> getPipeRectangles() {
        return pipeRectangles;
    }

    public void setPipeRectangles(List<Rectangle> pipeRectangles) {
        this.pipeRectangles = pipeRectangles;
    }

    public List<Sprite> getPipes() {
        return pipes;
    }

    public void setPipes(List<Sprite> pipes) {
        this.pipes = pipes;
    }
}
