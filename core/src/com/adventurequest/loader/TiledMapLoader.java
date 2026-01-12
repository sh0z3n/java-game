package com.adventurequest.loader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.adventurequest.model.entities.Entity;
import com.adventurequest.model.entities.NPC;
import com.adventurequest.model.entities.Obstacle;
import com.adventurequest.model.world.GameMap;
import com.adventurequest.model.world.Portal;
import java.util.ArrayList;
import java.util.List;
/**
 * TiledMapLoader - Loads Tiled maps and parses entities
 *
 * This class reads .tmx files created in Tiled and converts them
 * into GameMap objects with entities
 */
public class TiledMapLoader {
    private TmxMapLoader tmxLoader;
    private EntityFactory entityFactory;
    /**
     * Constructor
     */
    public TiledMapLoader() {
        this.tmxLoader = new TmxMapLoader();
        this.entityFactory = new EntityFactory();
    }
    /**
     * Load a map from a .tmx file
     * @param mapPath Path to the .tmx file (relative to assets folder)
     * @return GameMap object
     */
    public GameMap loadMap(String mapPath) {
        try {
            Gdx.app.log("TiledMapLoader", "Loading map: " + mapPath);
            // Load the Tiled map
            TiledMap tiledMap = tmxLoader.load(mapPath);
            // Get map ID from filename
            String mapId = mapPath.substring(mapPath.lastIndexOf('/') + 1, mapPath.lastIndexOf('.'));
            // Create GameMap
            GameMap gameMap = new GameMap(mapId, tiledMap);
            // Parse map dimensions
            MapProperties properties = tiledMap.getProperties();
            int mapWidth = properties.get("width", Integer.class);
            int mapHeight = properties.get("height", Integer.class);
            int tileWidth = properties.get("tilewidth", Integer.class);
            int tileHeight = properties.get("tileheight", Integer.class);
            gameMap.setMapDimensions(mapWidth, mapHeight);
            // Parse collision layer
            int[][] collisionLayer = parseCollisionLayer(tiledMap, "collision");
            if (collisionLayer != null) {
                gameMap.setCollisionLayer(collisionLayer);
            }
            // Parse entities from object layers
            List<Entity> entities = parseEntities(tiledMap);
            for (Entity entity : entities) {
                gameMap.addEntity(entity);
            }
            // Parse portals
            List<Portal> portals = parsePortals(tiledMap);
            for (Portal portal : portals) {
                gameMap.addPortal(portal);
            }
            Gdx.app.log("TiledMapLoader", "Map loaded successfully: " + mapId);
            Gdx.app.log("TiledMapLoader", "  - Dimensions: " + mapWidth + "x" + mapHeight);
            Gdx.app.log("TiledMapLoader", "  - Entities: " + entities.size());
            Gdx.app.log("TiledMapLoader", "  - Portals: " + portals.size());
            return gameMap;
        } catch (Exception e) {
            Gdx.app.error("TiledMapLoader", "Failed to load map: " + mapPath, e);
            return null;
        }
    }
    /**
     * Parse collision layer from Tiled map
     */
    private int[][] parseCollisionLayer(TiledMap tiledMap, String layerName) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(layerName);
        if (layer == null) {
            Gdx.app.log("TiledMapLoader", "No collision layer found");
            return null;
        }
        int width = layer.getWidth();
        int height = layer.getHeight();
        int[][] collision = new int[height][width];
        // Parse tiles (1 = collision, 0 = no collision)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                collision[y][x] = (cell != null && cell.getTile() != null) ? 1 : 0;
            }
        }
        return collision;
    }
    /**
     * Parse entities from object layers
     */
    private List<Entity> parseEntities(TiledMap tiledMap) {
        List<Entity> entities = new ArrayList<>();
        try {
            // Look for "entities" object layer
            MapObjects objects = tiledMap.getLayers().get("entities").getObjects();
            if (objects == null) {
                Gdx.app.log("TiledMapLoader", "No entities layer found");
                return entities;
            }
            Gdx.app.log("TiledMapLoader", "Found " + objects.getCount() + " objects in entities layer");
            for (MapObject object : objects) {
                String objectName = object.getName();
                String objectType = object.getProperties().get("type", String.class);
                Gdx.app.log("TiledMapLoader", "Processing object: " + objectName + " (type: " + objectType + ")");
                Entity entity = entityFactory.createEntity(object);
                if (entity != null) {
                    entities.add(entity);
                    Gdx.app.log("TiledMapLoader", "  ✓ Entity created successfully");
                } else {
                    Gdx.app.log("TiledMapLoader", "  ✗ Entity creation failed");
                }
            }
        } catch (Exception e) {
            Gdx.app.error("TiledMapLoader", "Error parsing entities: " + e.getMessage(), e);
        }
        return entities;
    }
    /**
     * Parse portals from object layers (both "portals" and "entities" layers)
     */
    private List<Portal> parsePortals(TiledMap tiledMap) {
        List<Portal> portals = new ArrayList<>();
        // Try to parse from "portals" layer first
        try {
            MapObjects objects = tiledMap.getLayers().get("portals").getObjects();
            if (objects != null) {
                parsePortalsFromLayer(objects, portals);
            }
        } catch (Exception e) {
            Gdx.app.log("TiledMapLoader", "No dedicated portals layer found");
        }
        // Also check "entities" layer for Portal type objects
        try {
            MapObjects objects = tiledMap.getLayers().get("entities").getObjects();
            if (objects != null) {
                for (MapObject object : objects) {
                    // Check if this object is marked as a Portal type
                    String type = object.getProperties().get("type", String.class);
                    if (type != null && type.equals("Portal")) {
                        if (object instanceof RectangleMapObject) {
                            RectangleMapObject rectObject = (RectangleMapObject) object;
                            Rectangle rect = rectObject.getRectangle();
                            // Get portal properties - support both old and new format
                            String id = object.getName();
                            String destination = object.getProperties().get("destination", String.class);
                            String targetMap = object.getProperties().get("targetMap", String.class);
                            // Use 'destination' if available, otherwise use 'targetMap'
                            String mapName = (destination != null) ? destination : targetMap;
                            if (mapName != null) {
                                // For map transitions, spawn at start position
                                Portal portal = new Portal(
                                    id != null ? id : "portal_" + portals.size(),
                                    rect.x, rect.y, rect.width, rect.height,
                                    mapName, 200, 200  // Default spawn position
                                );
                                portals.add(portal);
                                Gdx.app.log("TiledMapLoader", "🚪 Portal created: " + id + " -> " + mapName + " at (" + rect.x + "," + rect.y + ")");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Gdx.app.log("TiledMapLoader", "Error parsing portals from entities layer: " + e.getMessage());
        }
        return portals;
    }
    /**
     * Helper method to parse portals from a specific layer
     */
    private void parsePortalsFromLayer(MapObjects objects, List<Portal> portals) {
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                // Get portal properties
                String id = object.getName();
                String targetMap = object.getProperties().get("targetMap", String.class);
                String destination = object.getProperties().get("destination", String.class);
                Float targetX = object.getProperties().get("targetX", Float.class);
                Float targetY = object.getProperties().get("targetY", Float.class);
                String mapName = (destination != null) ? destination : targetMap;
                Float spawnX = (targetX != null) ? targetX : 200f;
                Float spawnY = (targetY != null) ? targetY : 200f;
                if (mapName != null) {
                    Portal portal = new Portal(
                        id != null ? id : "portal_" + portals.size(),
                        rect.x, rect.y, rect.width, rect.height,
                        mapName, spawnX, spawnY
                    );
                    portals.add(portal);
                    Gdx.app.log("TiledMapLoader", "Portal created: " + id + " -> " + mapName);
                }
            }
        }
    }
}
