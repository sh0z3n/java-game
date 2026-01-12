package com.adventurequest.model.entities;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
/**
 * Chest - A collectible chest entity that contains items or gold
 */
public class Chest extends Entity {
    private Texture closedTexture;
    private Texture openTexture;
    private boolean isOpen;
    private String contents;
    private int goldValue;
    /**
     * Constructor
     */
    public Chest(String id, float x, float y, String contents, int goldValue) {
        super(id, x, y, 32, 32); // Chests are 32x32
        this.contents = contents;
        this.goldValue = goldValue;
        this.isOpen = false;
        // Load textures
        loadTextures();
    }
    private void loadTextures() {
        try {
            // Try to load chest textures
            // For now, we'll use a placeholder or create simple colored boxes
            // In a real game, you'd have chest_closed.png and chest_open.png
            Gdx.app.log("Chest", "Chest textures would be loaded here");
        } catch (Exception e) {
            Gdx.app.log("Chest", "Error loading chest textures: " + e.getMessage());
        }
    }
    /**
     * Open the chest
     */
    public void open() {
        if (!isOpen) {
            isOpen = true;
            Gdx.app.log("Chest", "Opened chest: " + contents + " (Gold: " + goldValue + ")");
        }
    }
    /**
     * Render the chest
     */
    @Override
    public void render(SpriteBatch batch) {
        if (!active) return;
        // For now, we'll just log that rendering would happen here
        // In a real implementation, we'd draw the appropriate texture
        // batch.draw(isOpen ? openTexture : closedTexture, x, y);
    }
    /**
     * Update the chest
     */
    @Override
    public void update(float deltaTime) {
        // Chests don't need updates unless animated
    }
    /**
     * Check if chest is open
     */
    public boolean isOpen() {
        return isOpen;
    }
    /**
     * Get chest contents description
     */
    public String getContents() {
        return contents;
    }
    /**
     * Get gold value
     */
    public int getGoldValue() {
        return goldValue;
    }
}
