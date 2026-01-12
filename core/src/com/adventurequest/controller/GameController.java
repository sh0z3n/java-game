package com.adventurequest.controller;
import com.badlogic.gdx.Gdx;
import com.adventurequest.model.GameState;
import com.adventurequest.model.entities.Player;
import com.adventurequest.model.entities.PowerUp;
import com.adventurequest.model.world.GameMap;
import com.adventurequest.loader.TiledMapLoader;
public class GameController {
    private GameState gameState;
    private InputHandler inputHandler;
    private CollisionManager collisionManager;
    private TiledMapLoader mapLoader;
    private PortalManager portalManager;
    private SaveLoadManager saveLoadManager;
    /**
     * Constructor
     */
    public GameController() {
        this.gameState = new GameState();
        this.inputHandler = new InputHandler();
        this.collisionManager = new CollisionManager();
        this.mapLoader = new TiledMapLoader();
        this.portalManager = new PortalManager();
        this.saveLoadManager = new SaveLoadManager();
    }
    /**
     * Set renderer for visual effects
     */
    public void setRenderer(com.adventurequest.view.Renderer renderer) {
        this.collisionManager.setRenderer(renderer);
        this.portalManager.setRenderer(renderer);
    }
    /**
     * Initialize the game
     */
    public void initialize() {
        // Create player
        Player player = new Player("player1", 200, 200);
        player.setSpawn(200, 200);
        // Try to load battle_arena as main starting map
        GameMap gameMap = null;
        try {
            gameMap = mapLoader.loadMap("maps/battle_arena.tmx");
            Gdx.app.log("GameController", "🎮 Loaded battle_arena as main map");
        } catch (Exception e) {
            Gdx.app.log("GameController", "Could not load battle_arena, trying seaport_mega");
            try {
                gameMap = mapLoader.loadMap("maps/seaport_mega.tmx");
            } catch (Exception e2) {
                Gdx.app.log("GameController", "Could not load any map, using test map");
            }
        }
        // Fallback to test map if loading failed
        if (gameMap == null) {
            gameMap = new GameMap("test_map", null);
        }
        // Set map boundaries on player to prevent leaving map
        if (gameMap != null) {
            float mapWidthPixels = gameMap.getMapWidth() * gameMap.getTileWidth();
            float mapHeightPixels = gameMap.getMapHeight() * gameMap.getTileHeight();
            player.setMapBoundaries(32, mapWidthPixels - 32, 32, mapHeightPixels - 32);
            Gdx.app.log("GameController", "Map boundaries: " + mapWidthPixels + "x" + mapHeightPixels + " pixels");
        }
        // Initialize game state
        gameState.initialize(player, gameMap);
        // Spawn power-ups in the map
        spawnPowerUps(gameState);
        Gdx.app.log("GameController", "Game initialized");
        Gdx.app.log("GameController", "Entities in map: " + gameState.getEntities().size());
    }
    /**
     * Update game logic
     */
    public void update(float deltaTime) {
        // Handle input
        inputHandler.update(gameState, deltaTime);
        // Update game state
        gameState.update(deltaTime);
        // Check collisions
        collisionManager.checkCollisions(gameState);
        // Check for portal teleportation
        portalManager.checkPortals(gameState, deltaTime);
    }
    /**
     * Save the current game
     */
    public boolean saveGame() {
        return saveLoadManager.saveGame(gameState);
    }
    /**
     * Load a saved game
     */
    public boolean loadGame() {
        SaveLoadManager.SaveData saveData = saveLoadManager.loadGame();
        if (saveData != null) {
            // Load the map
            try {
                GameMap map = mapLoader.loadMap("maps/" + saveData.currentMap + ".tmx");
                gameState.setCurrentMap(map);
                // Set player position
                gameState.getPlayer().setPosition(saveData.playerX, saveData.playerY);
                Gdx.app.log("GameController", "Game loaded: " + saveData.currentMap);
                return true;
            } catch (Exception e) {
                Gdx.app.log("GameController", "Error loading game: " + e.getMessage());
                return false;
            }
        }
        return false;
    }
    /**
     * Check if a save file exists
     */
    public boolean hasSaveFile() {
        return saveLoadManager.saveExists();
    }
    /**
     * Get the current game state
     */
    public GameState getGameState() {
        return gameState;
    }
    /**
     * Spawn power-ups in the current map
     */
    private void spawnPowerUps(GameState gameState) {
        try {
            // Temporarily store powerups to add after getting textures from renderer
            // For now, add a few default ones per map
            java.util.Random rand = new java.util.Random();
            String currentMapId = gameState.getCurrentMap().getMapId();
            // Each map gets 3-5 power-ups randomly placed
            int powerUpCount = 3 + rand.nextInt(3);
            for (int i = 0; i < powerUpCount; i++) {
                float x = 100f + rand.nextFloat() * 1600f;
                float y = 100f + rand.nextFloat() * 800f;
                PowerUp.Type t = PowerUp.Type.values()[rand.nextInt(3)];
                gameState.addPowerUp(new PowerUp(x, y, t, null));
            }
            Gdx.app.log("GameController", "✨ Spawned " + powerUpCount + " power-ups in " + currentMapId);
        } catch (Exception e) {
            Gdx.app.error("GameController", "Error spawning power-ups: " + e.getMessage());
        }
    }
    /**
     * Clean up resources
     */
    public void dispose() {
        Gdx.app.log("GameController", "Controller disposed");
    }
}
