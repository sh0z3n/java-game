package com.adventurequest.model.interfaces;
/**
 * Interface for entities that need per-frame updates
 * Example: Animated entities, moving entities
 */
public interface Updatable {
    /**
     * Update the entity's state
     * @param deltaTime Time elapsed since last frame in seconds
     */
    void update(float deltaTime);
}
