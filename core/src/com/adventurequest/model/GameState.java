package com.adventurequest.model;
import com.adventurequest.model.entities.Entity;
import com.adventurequest.model.entities.Player;
import com.adventurequest.model.entities.PowerUp;
import com.adventurequest.model.world.GameMap;
import java.util.ArrayList;
import java.util.List;
/**
 * GameState - Represents the current state of the game
 *
 * Holds all game data:
 * - Current map
 * - Player
 * - All entities
 * - Game state flags
 *
 * This is the "Model" in MVC
 */
public class GameState {
    // Current game state
    private Player player;
    private GameMap currentMap;
    private List<Entity> entities;
    private List<PowerUp> powerUps;
    // Game state flags
    private boolean paused;
    private boolean dialogueActive;
    /**
     * Constructor
     */
    public GameState() {
        this.entities = new ArrayList<>();
        this.powerUps = new ArrayList<>();
        this.paused = false;
        this.dialogueActive = false;
    }
    /**
     * Initialize the game state with a player and starting map
     */
    public void initialize(Player player, GameMap startingMap) {
        this.player = player;
        this.currentMap = startingMap;
        // Clear existing entities
        entities.clear();
        // Add player to entities list FIRST
        entities.add(player);
        // Add all map entities to game state for collision detection
        // Make sure we don't add the player again if it's in the map entities
        if (startingMap != null && startingMap.getMapEntities() != null) {
            for (Entity mapEntity : startingMap.getMapEntities()) {
                if (mapEntity != null && mapEntity != player && !entities.contains(mapEntity)) {
                    entities.add(mapEntity);
                }
            }
            com.badlogic.gdx.Gdx.app.log("GameState", "Added " + startingMap.getMapEntities().size() + " map entities (player + " + (entities.size() - 1) + " others)");
        }
    }
    /**
     * Update all entities
     */
    public void update(float deltaTime) {
        if (paused) {
            return;
        }
        // Update all entities
        for (Entity entity : entities) {
            if (entity.isActive()) {
                entity.update(deltaTime);
            }
        }
    }
    /**
     * Add an entity to the game
     */
    public void addEntity(Entity entity) {
        if (!entities.contains(entity)) {
            entities.add(entity);
        }
    }
    /**
     * Remove an entity from the game
     */
    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }
    /**
     * Get entity by ID
     */
    public Entity getEntityById(String id) {
        for (Entity entity : entities) {
            if (entity.getId().equals(id)) {
                return entity;
            }
        }
        return null;
    }
    // Getters and Setters
    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
    public GameMap getCurrentMap() {
        return currentMap;
    }
    public void setCurrentMap(GameMap map) {
        this.currentMap = map;
    }
    public List<Entity> getEntities() {
        return entities;
    }
    public boolean isPaused() {
        return paused;
    }
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
    public boolean isDialogueActive() {
        return dialogueActive;
    }
    public void setDialogueActive(boolean dialogueActive) {
        this.dialogueActive = dialogueActive;
    }
    public List<PowerUp> getPowerUps() {
        return powerUps;
    }
    public void addPowerUp(PowerUp powerUp) {
        powerUps.add(powerUp);
    }
    public void removePowerUp(PowerUp powerUp) {
        powerUps.remove(powerUp);
    }
}
