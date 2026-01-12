package com.adventurequest.controller;
import com.badlogic.gdx.Gdx;
import com.adventurequest.model.GameState;
import com.adventurequest.model.entities.Entity;
import com.adventurequest.model.entities.Player;
import com.adventurequest.model.entities.Portal;
import com.adventurequest.model.world.GameMap;
import com.adventurequest.loader.TiledMapLoader;
import java.util.List;
/**
 * PortalManager - Handles portal teleportation between maps
 */
public class PortalManager {
    private TiledMapLoader mapLoader;
    private float portalCooldown;
    private static final float PORTAL_COOLDOWN_TIME = 1.0f; // 1 second cooldown
    private com.adventurequest.view.Renderer renderer = null;
    public PortalManager() {
        this.mapLoader = new TiledMapLoader();
        this.portalCooldown = 0;
    }
    /**
     * Set renderer for visual effects
     */
    public void setRenderer(com.adventurequest.view.Renderer renderer) {
        this.renderer = renderer;
    }
    /**
     * Check if player is touching any portal and handle teleportation
     */
    public void checkPortals(GameState gameState, float deltaTime) {
        if (gameState == null || gameState.getPlayer() == null) {
            return;
        }
        // Update cooldown
        if (portalCooldown > 0) {
            portalCooldown -= deltaTime;
            return;
        }
        Player player = gameState.getPlayer();
        List<Entity> entities = gameState.getEntities();
        // Check each portal
        for (Entity entity : entities) {
            if (entity instanceof Portal && entity.isActive()) {
                Portal portal = (Portal) entity;
                // Check if player overlaps portal
                if (player.overlaps(portal)) {
                    teleportPlayer(gameState, portal);
                    portalCooldown = PORTAL_COOLDOWN_TIME;
                    break;
                }
            }
        }
    }
    /**
     * Teleport player through portal
     */
    private void teleportPlayer(GameState gameState, Portal portal) {
        String targetMap = portal.getTargetMap();
        float targetX = portal.getTargetX();
        float targetY = portal.getTargetY();
        Gdx.app.log("PortalManager", "Teleporting to " + targetMap + " at (" + targetX + "," + targetY + ")");
        // Create portal effect
        if (renderer != null) {
            renderer.createPortalEffect(
                gameState.getPlayer().getX() + gameState.getPlayer().getWidth()/2,
                gameState.getPlayer().getY() + gameState.getPlayer().getHeight()/2
            );
        }
        // Load target map
        try {
            GameMap newMap = mapLoader.loadMap("maps/" + targetMap + ".tmx");
            if (newMap != null) {
                // Update player position
                gameState.getPlayer().setPosition(targetX, targetY);
                // Switch to new map
                gameState.setCurrentMap(newMap);
                Gdx.app.log("PortalManager", "Successfully teleported to " + targetMap);
            } else {
                Gdx.app.log("PortalManager", "Failed to load map: " + targetMap);
            }
        } catch (Exception e) {
            Gdx.app.log("PortalManager", "Error loading map " + targetMap + ": " + e.getMessage());
        }
    }
    public void dispose() {
        // Cleanup if needed
    }
}
