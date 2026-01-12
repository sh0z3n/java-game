package com.adventurequest.loader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.adventurequest.model.entities.Entity;
import com.adventurequest.model.entities.NPC;
import com.adventurequest.model.entities.Obstacle;
import com.adventurequest.model.entities.Player;
import com.adventurequest.model.entities.Portal;
import com.adventurequest.model.entities.Chest;
/**
 * EntityFactory - Creates entities from Tiled map objects
 *
 * Demonstrates Factory Design Pattern:
 * - Central place for entity creation
 * - Creates entities based on type property
 * - Easy to extend with new entity types
 */
public class EntityFactory {
    private int entityIdCounter = 0;
    /**
     * Create an entity from a Tiled MapObject
     * @param object The MapObject from Tiled
     * @return Entity instance or null if type is unknown
     */
    public Entity createEntity(MapObject object) {
        // Get object properties
        String type = object.getProperties().get("type", String.class);
        if (type == null) {
            Gdx.app.log("EntityFactory", "Object has no type property: " + object.getName());
            return null;
        }
        // Get position from rectangle
        if (!(object instanceof RectangleMapObject)) {
            Gdx.app.log("EntityFactory", "Object is not a rectangle: " + object.getName());
            return null;
        }
        Rectangle rect = ((RectangleMapObject) object).getRectangle();
        float x = rect.x;
        float y = rect.y;
        float width = rect.width;
        float height = rect.height;
        // Create entity based on type
        switch (type.toUpperCase()) {
            case "NPC":
                return createNPC(object, x, y);
            case "OBSTACLE":
                return createObstacle(object, x, y, width, height);
            case "PORTAL":
                return createPortal(object, x, y, width, height);
            case "CHEST":
                return createChest(object, x, y);
            case "PLAYER":
                return createPlayer(object, x, y);
            case "SPIKE":
                return createSpike(object, x, y, width, height);
            default:
                Gdx.app.log("EntityFactory", "Unknown entity type: " + type);
                return null;
        }
    }
    /**
     * Create an NPC from map object
     */
    private NPC createNPC(MapObject object, float x, float y) {
        String id = object.getName() != null ? object.getName() : "npc_" + (entityIdCounter++);
        String name = object.getProperties().get("name", "Unknown", String.class);
        String dialogueStr = object.getProperties().get("dialogue", String.class);
        // Parse dialogue (separated by |)
        String[] dialogues;
        if (dialogueStr != null && !dialogueStr.isEmpty()) {
            dialogues = dialogueStr.split("\\|");
        } else {
            dialogues = new String[]{"Hello!", "Nice to meet you!"};
        }
        NPC npc = new NPC(id, x, y, name, dialogues);
        Gdx.app.log("EntityFactory", "Created NPC: " + name + " at (" + x + "," + y + ")");
        return npc;
    }
    /**
     * Create an Obstacle from map object
     */
    private Obstacle createObstacle(MapObject object, float x, float y, float width, float height) {
        String id = object.getName() != null ? object.getName() : "obstacle_" + (entityIdCounter++);
        String obstacleType = object.getProperties().get("obstacleType", "generic", String.class);
        Obstacle obstacle = new Obstacle(id, x, y, width, height, obstacleType);
        Gdx.app.log("EntityFactory", "Created Obstacle: " + obstacleType + " at (" + x + "," + y + ")");
        return obstacle;
    }
    /**
     * Create a Portal from map object
     */
    private Portal createPortal(MapObject object, float x, float y, float width, float height) {
        String id = object.getName() != null ? object.getName() : "portal_" + (entityIdCounter++);
        // Support both "destination" and "targetMap" property names
        String targetMap = object.getProperties().get("destination", String.class);
        if (targetMap == null) {
            targetMap = object.getProperties().get("targetMap", String.class);
        }
        if (targetMap == null) {
            targetMap = "seaport_mega"; // Default fallback
        }
        Float targetXObj = object.getProperties().get("targetX", Float.class);
        Float targetYObj = object.getProperties().get("targetY", Float.class);
        float targetX = (targetXObj != null) ? targetXObj : 200f; // Default spawn position
        float targetY = (targetYObj != null) ? targetYObj : 200f;
        Portal portal = new Portal(id, x, y, width, height, targetMap, targetX, targetY);
        Gdx.app.log("EntityFactory", "🚪 Created Portal to " + targetMap + " at (" + x + "," + y + ")");
        return portal;
    }
    /**
     * Create a Chest from map object
     */
    private Chest createChest(MapObject object, float x, float y) {
        String id = object.getName() != null ? object.getName() : "chest_" + (entityIdCounter++);
        String contents = object.getProperties().get("contents", "Treasure", String.class);
        Integer goldValueObj = object.getProperties().get("goldValue", Integer.class);
        int goldValue = (goldValueObj != null) ? goldValueObj : 50;
        Chest chest = new Chest(id, x, y, contents, goldValue);
        Gdx.app.log("EntityFactory", "Created Chest: " + contents + " (" + goldValue + " gold) at (" + x + "," + y + ")");
        return chest;
    }
    /**
     * Create a Spike hazard
     */
    private com.adventurequest.model.entities.Spike createSpike(MapObject object, float x, float y, float width, float height) {
        String id = object.getName() != null ? object.getName() : "spike_" + (entityIdCounter++);
        return new com.adventurequest.model.entities.Spike(id, x, y, width, height);
    }
    /**
     * Create a Player from map object (for spawn points)
     */
    private Player createPlayer(MapObject object, float x, float y) {
        String id = object.getName() != null ? object.getName() : "player_spawn";
        Player player = new Player(id, x, y);
        Gdx.app.log("EntityFactory", "Created Player spawn at (" + x + "," + y + ")");
        return player;
    }
}
