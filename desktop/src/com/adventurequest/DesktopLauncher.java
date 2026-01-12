package com.adventurequest;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
/**
 * Desktop Launcher for Adventure Quest Engine
 * Platform: Linux
 */
public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // Window configuration
        config.setTitle("Adventure Quest Engine");
        config.setWindowedMode(800, 600);
        config.setForegroundFPS(60);
        config.useVsync(true);
        // Launch the game
        new Lwjgl3Application(new AdventureQuestGame(), config);
    }
}
