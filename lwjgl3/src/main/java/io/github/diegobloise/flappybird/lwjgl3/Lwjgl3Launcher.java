package io.github.diegobloise.flappybird.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import io.github.diegobloise.flappybird.FlappyBird;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {

    static FlappyBird game;

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired())
            return; // This handles macOS support and helps on Windows.
        game = new FlappyBird();
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(game, getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Java Flappy Bird");
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        configuration.setWindowedMode(game.SCREEN_WIDTH * 2, game.SCREEN_HEIGHT * 2);
        configuration.setWindowIcon(
                "icons/icon_8.png",
                "icons/icon_7.png",
                "icons/icon_6.png",
                "icons/icon_5.png",
                "icons/icon_4.png",
                "icons/icon_3.png",
                "icons/icon_2.png",
                "icons/icon_1.png");
        configuration.setResizable(true);
        return configuration;
    }
}
