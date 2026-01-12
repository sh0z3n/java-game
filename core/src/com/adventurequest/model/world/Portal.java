package com.adventurequest.model.world;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
/**
 * Portal - Represents a transition zone between maps
 *
 * When the player enters a portal, they are transported to another map
 */
public class Portal {
    private String id;
    private Rectangle bounds;
    private String targetMapId;
    private Vector2 targetPosition;
    /**
     * Constructor
     */
    public Portal(String id, float x, float y, float width, float height,
                  String targetMapId, float targetX, float targetY) {
        this.id = id;
        this.bounds = new Rectangle(x, y, width, height);
        this.targetMapId = targetMapId;
        this.targetPosition = new Vector2(targetX, targetY);
    }
    /**
     * Check if a position is within this portal
     */
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }
    /**
     * Check if an entity's bounds overlap this portal
     */
    public boolean overlaps(Rectangle entityBounds) {
        return bounds.overlaps(entityBounds);
    }
    // Getters
    public String getId() {
        return id;
    }
    public Rectangle getBounds() {
        return bounds;
    }
    public String getTargetMapId() {
        return targetMapId;
    }
    public Vector2 getTargetPosition() {
        return targetPosition;
    }
    public float getTargetX() {
        return targetPosition.x;
    }
    public float getTargetY() {
        return targetPosition.y;
    }
}
