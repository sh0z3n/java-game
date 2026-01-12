package com.adventurequest.model.entities;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
/**
 * Abstract base class for all game entities
 *
 * Demonstrates OOP Concepts:
 * - Abstraction: Cannot instantiate Entity directly
 * - Encapsulation: Private fields with public getters/setters
 * - Inheritance: All game objects extend this class
 *
 * This is the foundation of the entity system
 */
public abstract class Entity {
    // Core properties
    protected String id;
    protected Vector2 position;
    protected Rectangle bounds;
    protected boolean active;
    protected float width;
    protected float height;
    /**
     * Constructor for Entity
     * @param id Unique identifier for this entity
     * @param x Initial X position
     * @param y Initial Y position
     * @param width Entity width
     * @param height Entity height
     */
    public Entity(String id, float x, float y, float width, float height) {
        this.id = id;
        this.position = new Vector2(x, y);
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x, y, width, height);
        this.active = true;
    }
    /**
     * Update the entity (called every frame)
     * Must be implemented by subclasses
     * @param deltaTime Time elapsed since last frame
     */
    public abstract void update(float deltaTime);
    /**
     * Render the entity
     * Must be implemented by subclasses
     * @param batch SpriteBatch for drawing
     */
    public abstract void render(SpriteBatch batch);
    /**
     * Set the position of this entity
     * Also updates collision bounds
     */
    public void setPosition(float x, float y) {
        this.position.set(x, y);
        this.bounds.setPosition(x, y);
    }
    /**
     * Move the entity by a delta amount
     */
    public void move(float dx, float dy) {
        setPosition(position.x + dx, position.y + dy);
    }
    // Getters and Setters (Encapsulation)
    public String getId() {
        return id;
    }
    public Vector2 getPosition() {
        return position;
    }
    public float getX() {
        return position.x;
    }
    public float getY() {
        return position.y;
    }
    public Rectangle getBounds() {
        return bounds;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }
    /**
     * Check if this entity overlaps with another
     */
    public boolean overlaps(Entity other) {
        return this.bounds.overlaps(other.getBounds());
    }
    /**
     * Get distance to another entity
     */
    public float distanceTo(Entity other) {
        return this.position.dst(other.getPosition());
    }
}
