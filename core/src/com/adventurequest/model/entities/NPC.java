package com.adventurequest.model.entities;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.adventurequest.model.interfaces.Collidable;
import com.adventurequest.model.interfaces.Interactable;
/**
 * NPC (Non-Player Character) Entity
 *
 * Demonstrates OOP Concepts:
 * - Inheritance: Extends Entity
 * - Interfaces: Implements Interactable and Collidable
 * - Encapsulation: Dialogue system is internal
 */
public class NPC extends Entity implements Interactable, Collidable {
    // NPC-specific properties
    private String name;
    private String[] dialogues;
    private int currentDialogueIndex;
    private boolean isInteracting;
    private Texture texture;
    // Interaction settings
    private static final float INTERACTION_RANGE = 48f; // pixels
    /**
     * Constructor for NPC
     */
    public NPC(String id, float x, float y, String name, String[] dialogues) {
        super(id, x, y, 64, 64); // Increased to 64x64 for better visibility
        this.name = name;
        this.dialogues = dialogues;
        this.currentDialogueIndex = 0;
        this.isInteracting = false;
        // Load NPC texture (placeholder)
        try {
            texture = new Texture(Gdx.files.internal("sprites/npc.png"));
            Gdx.app.log("NPC", "NPC texture loaded successfully for: " + name);
        } catch (Exception e) {
            Gdx.app.log("NPC", "Could not load NPC texture: " + e.getMessage());
            texture = null;
        }
    }
    /**
     * Update NPC state
     */
    @Override
    public void update(float deltaTime) {
        // Static for now
    }
    /**
     * Render the NPC
     */
    @Override
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x, position.y, width, height);
        }
    }
    // Interactable interface implementation
    @Override
    public void interact(Entity player) {
        if (!canInteract()) {
            return;
        }
        // Check if player is in range
        if (distanceTo(player) <= INTERACTION_RANGE) {
            isInteracting = true;
            Gdx.app.log("NPC", name + " says: " + getCurrentDialogue());
        }
    }
    @Override
    public boolean canInteract() {
        return active && dialogues != null && dialogues.length > 0;
    }
    @Override
    public float getInteractionRange() {
        return INTERACTION_RANGE;
    }
    // Collidable interface implementation
    @Override
    public boolean isSolid() {
        return true; // NPCs block movement
    }
    @Override
    public void onCollision(Entity other) {
        // NPCs don't move, so no collision response needed
    }
    // Dialogue management
    /**
     * Get the current dialogue line
     */
    public String getCurrentDialogue() {
        if (dialogues == null || dialogues.length == 0) {
            return "";
        }
        return dialogues[currentDialogueIndex];
    }
    /**
     * Advance to the next dialogue line
     * @return The next dialogue line, or null if at the end
     */
    public String getNextDialogue() {
        if (dialogues == null || dialogues.length == 0) {
            return null;
        }
        currentDialogueIndex++;
        if (currentDialogueIndex >= dialogues.length) {
            currentDialogueIndex = 0;
            isInteracting = false;
            return null;
        }
        return dialogues[currentDialogueIndex];
    }
    /**
     * Reset dialogue to the beginning
     */
    public void resetDialogue() {
        currentDialogueIndex = 0;
        isInteracting = false;
    }
    // Getters
    public String getName() {
        return name;
    }
    public boolean isInteracting() {
        return isInteracting;
    }
    public void setInteracting(boolean interacting) {
        this.isInteracting = interacting;
    }
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
