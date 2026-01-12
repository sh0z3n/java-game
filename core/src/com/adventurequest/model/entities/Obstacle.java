package com.adventurequest.model.entities;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.adventurequest.model.interfaces.Collidable;
/**
 * Obstacle Entity
 *
 * Represents static objects that block movement
 * Examples: rocks, trees, walls, buildings
 *
 * Demonstrates OOP Concepts:
 * - Inheritance: Extends Entity
 * - Interfaces: Implements Collidable
 */
public class Obstacle extends Entity implements Collidable {
    // Obstacle-specific properties
    private boolean isSolid;
    private String obstacleType;
    private Texture texture;
    /**
     * Constructor for Obstacle
     */
    public Obstacle(String id, float x, float y, float width, float height, String obstacleType) {
        super(id, x, y, width, height);
        this.obstacleType = obstacleType;
        this.isSolid = true; // By default, obstacles block movement
        // Load obstacle texture based on type
        try {
            String texturePath = "sprites/" + obstacleType + ".png";
            texture = new Texture(Gdx.files.internal(texturePath));
            Gdx.app.log("Obstacle", "Loaded texture: " + texturePath);
        } catch (Exception e) {
            Gdx.app.log("Obstacle", "Could not load texture for " + obstacleType + ": " + e.getMessage());
            texture = null;
        }
    }
    /**
     * Update obstacle state (usually nothing for static obstacles)
     */
    @Override
    public void update(float deltaTime) {
        // Static obstacles don't need updates
        // Can add animated obstacles here later
    }
    /**
     * Render the obstacle
     */
    @Override
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x, position.y, width, height);
        }
    }
    // Collidable interface implementation
    @Override
    public boolean isSolid() {
        return isSolid;
    }
    @Override
    public void onCollision(Entity other) {
        // Obstacles are static; damage is handled in CollisionManager
    }
    // Getters and Setters
    public String getObstacleType() {
        return obstacleType;
    }
    public void setSolid(boolean solid) {
        this.isSolid = solid;
    }
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
