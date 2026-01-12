package com.adventurequest.model.interfaces;
import com.adventurequest.model.entities.Entity;
/**
 * Interface for entities that can be interacted with by the player
 * Example: NPCs, chests, signs, doors
 */
public interface Interactable {
    /**
     * Called when the player interacts with this entity
     * @param player The player entity performing the interaction
     */
    void interact(Entity player);
    /**
     * Check if this entity can currently be interacted with
     * @return true if interaction is possible
     */
    boolean canInteract();
    /**
     * Get the interaction range (distance at which interaction is possible)
     * @return interaction range in pixels
     */
    float getInteractionRange();
}
