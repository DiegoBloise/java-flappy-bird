package io.github.diegobloise.flappybird.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

import io.github.diegobloise.flappybird.FlappyBird;

public class Ground {
    final FlappyBird game;

    private Texture groundTexture;
    private Sprite groundSprite;

    private Rectangle groundRectangle;

    public Ground(FlappyBird game) {
        this.game = game;
        groundTexture = new Texture("sprites/ground.png");
        groundSprite = new Sprite(groundTexture);
        groundRectangle = new Rectangle(0, 0, groundTexture.getWidth(), groundTexture.getHeight());
    }

    public void dispose() {
        groundTexture.dispose();
    }

    public void draw() {
        groundSprite.draw(game.batch);
    }

    public void playAnimation(float gameSpeed, float deltaTime) {
        groundSprite.translateX(gameSpeed * deltaTime);
        if (groundSprite.getX() + groundSprite.getWidth() < game.viewport.getWorldWidth()) {
            groundSprite.setX(0);
        }
    }

    public Rectangle getGroundRectangle() {
        return groundRectangle;
    }

    public void setGroundRectangle(Rectangle groundRectangle) {
        this.groundRectangle = groundRectangle;
    }

    public Sprite getGroundSprite() {
        return groundSprite;
    }

    public void setGroundSprite(Sprite groundSprite) {
        this.groundSprite = groundSprite;
    }
}
