package io.github.diegobloise.flappybird.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.diegobloise.flappybird.FlappyBird;

public class Pipe {
    private final int VERTICAL_PIPE_GAP = 27;

    final FlappyBird game;

    private final float minPipesHeight;
    private final float maxPipesHeight;

    private List<Texture> pipeTextures;
    private Texture pipeTexture;

    private Sprite topSprite;
    private Sprite bottomSprite;

    private Rectangle topRectangle;
    private Rectangle bottomRectangle;

    private Rectangle scoreRectangle;

    public Pipe(FlappyBird game) {
        this.game = game;

        pipeTextures = new ArrayList<>();
        pipeTextures.add(new Texture(Gdx.files.internal("sprites/pipe_green.png")));
        pipeTextures.add(new Texture(Gdx.files.internal("sprites/pipe_red.png")));
        pipeTexture = pipeTextures.get(MathUtils.random(pipeTextures.size() - 1));

        minPipesHeight = (game.viewport.getWorldHeight() / 2) - 20;
        maxPipesHeight = (game.viewport.getWorldHeight() / 2) + 70;

        topSprite = new Sprite(pipeTexture);
        bottomSprite = new Sprite(pipeTexture);

        float randomPosition = MathUtils.random(minPipesHeight, maxPipesHeight);
        Vector2 pipePosition = new Vector2(game.viewport.getWorldWidth(), randomPosition);

        // Create Top pipe
        topSprite.setBounds(pipePosition.x, pipePosition.y + VERTICAL_PIPE_GAP, pipeTexture.getWidth(),
                pipeTexture.getHeight());
        topSprite.setFlip(false, true);
        topRectangle = new Rectangle(pipePosition.x, pipePosition.y + VERTICAL_PIPE_GAP, pipeTexture.getWidth(),
                pipeTexture.getHeight());

        // Create Bottom pipe
        bottomSprite.setBounds(pipePosition.x, pipePosition.y - pipeTexture.getHeight() - VERTICAL_PIPE_GAP,
                pipeTexture.getWidth(),
                pipeTexture.getHeight());
        bottomRectangle = new Rectangle(pipePosition.x, pipePosition.y - pipeTexture.getHeight() - VERTICAL_PIPE_GAP,
                pipeTexture.getWidth(),
                pipeTexture.getHeight());

        // Create Score Area
        scoreRectangle = new Rectangle(
                pipePosition.x + pipeTexture.getWidth() + 5,
                pipePosition.y - (VERTICAL_PIPE_GAP * 1.5f),
                20,
                VERTICAL_PIPE_GAP * 3);
    }

    public void dispose() {
        for (Texture texture : pipeTextures) {
            texture.dispose();
        }
        pipeTexture.dispose();
    }

    public void draw() {
        topSprite.draw(game.batch);
        bottomSprite.draw(game.batch);
    }

    public void move(float gameSpeed, float deltaTime) {
        topSprite.translateX(gameSpeed * deltaTime);
        bottomSprite.setX(topSprite.getX());
    }

    public void updateCollisions(float gameSpeed, float deltaTime) {
        scoreRectangle.setX(scoreRectangle.getX() + gameSpeed * deltaTime);
        topRectangle.setX(topSprite.getX());
        bottomRectangle.setX(bottomSprite.getX());
    }

	public Rectangle getScoreRectangle() {
		return scoreRectangle;
	}

	public void setScoreRectangle(Rectangle scoreRectangle) {
		this.scoreRectangle = scoreRectangle;
	}

    public Rectangle getTopRectangle() {
        return topRectangle;
    }

    public void setTopRectangle(Rectangle topRectangle) {
        this.topRectangle = topRectangle;
    }

    public Rectangle getBottomRectangle() {
        return bottomRectangle;
    }

    public void setBottomRectangle(Rectangle bottomRectangle) {
        this.bottomRectangle = bottomRectangle;
    }

	public Sprite getBottomSprite() {
		return bottomSprite;
	}

	public void setBottomSprite(Sprite bottomSprite) {
		this.bottomSprite = bottomSprite;
	}


}
