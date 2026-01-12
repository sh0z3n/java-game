package com.adventurequest.model.interfaces;
import com.adventurequest.model.entities.Entity;
/**
 * Interface for entities that have collision detection
 * Example: Obstacles, walls, NPCs, player
 */
public interface Collidable {
    /**
     * Check if this entity is currently solid (blocks movement)
     * @return true if the entity blocks movement
     */
    boolean isSolid();
    /**
     * Called when this entity collides with another
     * @param other The entity this collided with
     */
    void onCollision(Entity other);
}
