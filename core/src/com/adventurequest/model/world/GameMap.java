package com.adventurequest.model.world;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.adventurequest.model.entities.Entity;
import java.util.ArrayList;
import java.util.List;
/**
 * GameMap - Represents a game map/level
 *
 * Contains:
 * - Tiled map data
 * - Entities on this map
 * - Collision data
 * - Portals to other maps
 */
public class GameMap {
    private String mapId;
    private TiledMap tiledMap;
    private List<Entity> mapEntities;
    private List<Portal> portals;
    private int[][] collisionLayer;
    private int mapWidth;
    private int mapHeight;
    private int tileWidth;
    private int tileHeight;
    /**
     * Constructor
     */
    public GameMap(String mapId, TiledMap tiledMap) {
        this.mapId = mapId;
        this.tiledMap = tiledMap;
        this.mapEntities = new ArrayList<>();
        this.portals = new ArrayList<>();
        // Extract map dimensions from Tiled map
        // This will be properly implemented when we add the Tiled loader
        this.tileWidth = 32;
        this.tileHeight = 32;
    }
    /**
     * Check if a tile position has collision
     */
    public boolean isCollision(int tileX, int tileY) {
        if (collisionLayer == null) {
            return false;
        }
        if (tileX < 0 || tileY < 0 || tileX >= mapWidth || tileY >= mapHeight) {
            return true; // Out of bounds = collision
        }
        return collisionLayer[tileY][tileX] == 1;
    }
    /**
     * Check if a pixel position has collision
     */
    public boolean isCollisionAt(float x, float y) {
        int tileX = (int) (x / tileWidth);
        int tileY = (int) (y / tileHeight);
        return isCollision(tileX, tileY);
    }
    /**
     * Get portal at position (if any)
     */
    public Portal getPortalAt(float x, float y) {
        for (Portal portal : portals) {
            if (portal.contains(x, y)) {
                return portal;
            }
        }
        return null;
    }
    /**
     * Add an entity to this map
     */
    public void addEntity(Entity entity) {
        if (!mapEntities.contains(entity)) {
            mapEntities.add(entity);
        }
    }
    /**
     * Remove an entity from this map
     */
    public void removeEntity(Entity entity) {
        mapEntities.remove(entity);
    }
    /**
     * Add a portal to this map
     */
    public void addPortal(Portal portal) {
        portals.add(portal);
    }
    // Getters and Setters
    public String getMapId() {
        return mapId;
    }
    public String getName() {
        return mapId;
    }
    public TiledMap getTiledMap() {
        return tiledMap;
    }
    public List<Entity> getMapEntities() {
        return mapEntities;
    }
    public List<Portal> getPortals() {
        return portals;
    }
    public void setCollisionLayer(int[][] collisionLayer) {
        this.collisionLayer = collisionLayer;
        if (collisionLayer != null && collisionLayer.length > 0) {
            this.mapHeight = collisionLayer.length;
            this.mapWidth = collisionLayer[0].length;
        }
    }
    public int getMapWidth() {
        return mapWidth;
    }
    public int getMapHeight() {
        return mapHeight;
    }
    public int getTileWidth() {
        return tileWidth;
    }
    public int getTileHeight() {
        return tileHeight;
    }
    public void setMapDimensions(int width, int height) {
        this.mapWidth = width;
        this.mapHeight = height;
    }
}
