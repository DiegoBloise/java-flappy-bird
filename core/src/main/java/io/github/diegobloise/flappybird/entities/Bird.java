package io.github.diegobloise.flappybird.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.diegobloise.flappybird.FlappyBird;

public class Bird {

    final FlappyBird game;

    public static final float GRAVITY = 18;
    public final float FLAP_FORCE = 14 * GRAVITY;
    public final float ROTATION_SPEED = 5;

    private float time;

    private Vector2 startPosition;

    private float velocity;

    private List<Texture> textures;

    private Texture texture;

    private Sprite sprite;

    private Rectangle rectangle;

    private static final int QUANTITY_OF_FRAMES = 4;
    private float animationTime;
    private Animation<TextureRegion> birdAnimation;

    public Bird(FlappyBird game) {
        this.game = game;

        textures = new ArrayList<>();
        textures.add(new Texture(Gdx.files.internal("sprites/bird_1.png")));
        textures.add(new Texture(Gdx.files.internal("sprites/bird_2.png")));
        textures.add(new Texture(Gdx.files.internal("sprites/bird_3.png")));
        texture = textures.get(MathUtils.random(textures.size() - 1));

        this.setStartPosition(new Vector2(35, (game.viewport.getWorldHeight() / 2) + 10));

        initializeBirdAnimations();
        TextureRegion currentFrame = birdAnimation.getKeyFrame(animationTime);
        sprite = new Sprite(currentFrame);
        sprite.setOriginCenter();
        sprite.setOrigin(sprite.getOriginX() + 2, sprite.getOriginY());
        sprite.setRegion(birdAnimation.getKeyFrame(0));
        sprite.setPosition(startPosition.x, startPosition.y);
        sprite.setRotation(0);
        velocity = 0;

        rectangle = new Rectangle();
    }

    public void draw() {
        sprite.draw(game.batch);
    }

    public void dispose() {
        for (Texture texture : textures) {
            texture.dispose();
        }
        texture.dispose();
    }

    private void initializeBirdAnimations() {
        Texture birdSheet = textures.get(MathUtils.random(textures.size() - 1));

        int frameWidth = birdSheet.getWidth() / QUANTITY_OF_FRAMES;
        int frameHeight = birdSheet.getHeight();
        TextureRegion[][] regions = TextureRegion.split(birdSheet, frameWidth, frameHeight);

        TextureRegion[] frames = regions[0];

        birdAnimation = new Animation<>(0.1f, frames);
        birdAnimation.setPlayMode(Animation.PlayMode.LOOP);

        animationTime = 0;
    }

    public void flap() {
        this.velocity = FLAP_FORCE;
    }

    public void updateCollision() {
        rectangle.set(
                sprite.getX() + sprite.getWidth() / 2,
                sprite.getY() + sprite.getHeight() / 2,
                sprite.getHeight() - 5,
                sprite.getHeight() - 5);
        rectangle.setCenter(sprite.getX() + sprite.getWidth() / 2,
                sprite.getY() + sprite.getHeight() / 2);
    }

    public void applyPhisics(Rectangle groundRectangle, float deltaTime) {
        velocity -= GRAVITY;
        sprite.translateY(velocity * deltaTime);
        sprite.setY(MathUtils.clamp(sprite.getY(), groundRectangle.height - 5,
                game.viewport.getWorldHeight() - sprite.getHeight()));
    }

    public void playAnimation(boolean gameOver, boolean isPlaying, float deltaTime) {
        if (!gameOver) {
            // Flap animation
            animationTime += deltaTime;
            TextureRegion currentFrame = birdAnimation.getKeyFrame(animationTime);
            sprite.setRegion(currentFrame);
        }

        // Bird rotation animation
        if (isPlaying) {
            if (velocity > 0) {
                sprite.setRotation(
                        MathUtils.lerpAngleDeg(sprite.getRotation(), 30.0f,
                                (ROTATION_SPEED + 25) * deltaTime));
            } else {
                sprite.setRotation(
                        MathUtils.lerpAngleDeg(sprite.getRotation(), -50.0f,
                                (ROTATION_SPEED - 2) * deltaTime));
            }
            if (gameOver) {
                sprite.setRotation(
                        MathUtils.lerpAngleDeg(sprite.getRotation(), -70.0f,
                                (ROTATION_SPEED + 5) * deltaTime));
            }
        } else {
            // time += deltaTime;
            // float floatingOffset = (float) Math.sin(time * 2) * 5;
            // float baseY = (game.viewport.getWorldHeight() / 2) + 10 -
            // birdSprite.getHeight() / 2f;
            // birdSprite.setY(baseY + floatingOffset);
            time += deltaTime;
            float floatingOffset = (float) Math.sin(time * 7) * 2;
            float baseY = (game.viewport.getWorldHeight() / 2) + 10 - sprite.getHeight() / 2f;
            sprite.setY(baseY + floatingOffset);
        }
    }

    public Vector2 getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Vector2 startPosition) {
        this.startPosition = startPosition;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public float getAnimationTime() {
        return animationTime;
    }

    public void setAnimationTime(float animationTime) {
        this.animationTime = animationTime;
    }
}
