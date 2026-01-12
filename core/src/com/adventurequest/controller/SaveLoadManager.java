package com.adventurequest.controller;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.adventurequest.model.GameState;
import com.adventurequest.model.entities.Player;
import java.io.*;
/**
 * SaveLoadManager - Handles game save and load functionality
 */
public class SaveLoadManager {
    private static final String SAVE_DIR = "saves/";
    private static final String SAVE_FILE = "savegame.dat";
    public SaveLoadManager() {
        // Ensure save directory exists
        FileHandle saveDir = Gdx.files.local(SAVE_DIR);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
    }
    /**
     * Save the current game state
     */
    public boolean saveGame(GameState gameState) {
        if (gameState == null || gameState.getPlayer() == null) {
            Gdx.app.log("SaveLoadManager", "Cannot save: invalid game state");
            return false;
        }
        try {
            FileHandle saveFile = Gdx.files.local(SAVE_DIR + SAVE_FILE);
            // Create save data
            SaveData saveData = new SaveData();
            Player player = gameState.getPlayer();
            saveData.playerX = player.getX();
            saveData.playerY = player.getY();
            saveData.currentMap = gameState.getCurrentMap() != null ?
                                  gameState.getCurrentMap().getName() : "seaport";
            saveData.timestamp = System.currentTimeMillis();
            // Write to file
            String saveString = saveData.toString();
            saveFile.writeString(saveString, false);
            Gdx.app.log("SaveLoadManager", "Game saved successfully");
            return true;
        } catch (Exception e) {
            Gdx.app.log("SaveLoadManager", "Error saving game: " + e.getMessage());
            return false;
        }
    }
    /**
     * Load a saved game
     */
    public SaveData loadGame() {
        try {
            FileHandle saveFile = Gdx.files.local(SAVE_DIR + SAVE_FILE);
            if (!saveFile.exists()) {
                Gdx.app.log("SaveLoadManager", "No save file found");
                return null;
            }
            String saveString = saveFile.readString();
            SaveData saveData = SaveData.fromString(saveString);
            Gdx.app.log("SaveLoadManager", "Game loaded successfully");
            return saveData;
        } catch (Exception e) {
            Gdx.app.log("SaveLoadManager", "Error loading game: " + e.getMessage());
            return null;
        }
    }
    /**
     * Check if a save file exists
     */
    public boolean saveExists() {
        FileHandle saveFile = Gdx.files.local(SAVE_DIR + SAVE_FILE);
        return saveFile.exists();
    }
    /**
     * Delete save file
     */
    public boolean deleteSave() {
        try {
            FileHandle saveFile = Gdx.files.local(SAVE_DIR + SAVE_FILE);
            if (saveFile.exists()) {
                saveFile.delete();
                Gdx.app.log("SaveLoadManager", "Save file deleted");
                return true;
            }
            return false;
        } catch (Exception e) {
            Gdx.app.log("SaveLoadManager", "Error deleting save: " + e.getMessage());
            return false;
        }
    }
    /**
     * SaveData class - Simple data structure for save files
     */
    public static class SaveData {
        public float playerX;
        public float playerY;
        public String currentMap;
        public long timestamp;
        @Override
        public String toString() {
            return playerX + "," + playerY + "," + currentMap + "," + timestamp;
        }
        public static SaveData fromString(String str) {
            String[] parts = str.split(",");
            SaveData data = new SaveData();
            data.playerX = Float.parseFloat(parts[0]);
            data.playerY = Float.parseFloat(parts[1]);
            data.currentMap = parts[2];
            data.timestamp = Long.parseLong(parts[3]);
            return data;
        }
    }
}
