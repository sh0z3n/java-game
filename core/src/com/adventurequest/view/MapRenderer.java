package com.adventurequest.view;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.adventurequest.model.world.GameMap;
/**
 * MapRenderer - Renders Tiled maps
 */
public class MapRenderer {
    private OrthogonalTiledMapRenderer tiledRenderer;
    private OrthographicCamera camera;
    // Smooth camera following
    private float cameraLerpSpeed = 5.0f;
    private float targetX;
    private float targetY;
    // Map boundaries to clamp camera
    private float mapWidth = 0;
    private float mapHeight = 0;
    float shakeI = 0;
    float shakeT = 0;
    float shakeDur = 0.15f;
    public MapRenderer() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        camera.update();
        targetX = camera.position.x;
        targetY = camera.position.y;
    }
    /**
     * Render a game map
     */
    public void render(GameMap gameMap) {
        if (gameMap == null || gameMap.getTiledMap() == null) {
            return;
        }
        // Create renderer if needed
        if (tiledRenderer == null || tiledRenderer.getMap() != gameMap.getTiledMap()) {
            if (tiledRenderer != null) {
                tiledRenderer.dispose();
            }
            tiledRenderer = new OrthogonalTiledMapRenderer(gameMap.getTiledMap());
            // Cache map dimensions for camera bounds
            TiledMap tiledMap = gameMap.getTiledMap();
            TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
            if (layer != null) {
                mapWidth = layer.getWidth() * layer.getTileWidth();
                mapHeight = layer.getHeight() * layer.getTileHeight();
            }
        }
        // Render the map
        tiledRenderer.setView(camera);
        tiledRenderer.render();
    }
    /**
     * Update camera position (to follow player) - smooth lerp
     */
    public void setCameraPosition(float x, float y) {
        targetX = x;
        targetY = y;
    }
    public void update(float deltaTime) {
        if (shakeT > 0) {
            shakeT -= deltaTime;
            double sx = (Math.random() - 0.5) * shakeI * 2;
            double sy = (Math.random() - 0.5) * shakeI * 2;
            camera.position.x += sx;
            camera.position.y += sy;
        }
        // Smooth camera lerp to target
        float lerpFactor = cameraLerpSpeed * deltaTime;
        camera.position.x += (targetX - camera.position.x) * lerpFactor;
        camera.position.y += (targetY - camera.position.y) * lerpFactor;
        // Clamp camera to map bounds (no black areas)
        float cameraHalfWidth = camera.viewportWidth / 2f;
        float cameraHalfHeight = camera.viewportHeight / 2f;
        if (mapWidth > 0) {
            camera.position.x = Math.max(cameraHalfWidth, Math.min(camera.position.x, mapWidth - cameraHalfWidth));
        }
        if (mapHeight > 0) {
            camera.position.y = Math.max(cameraHalfHeight, Math.min(camera.position.y, mapHeight - cameraHalfHeight));
        }
        camera.update();
    }
    /**
     * Resize camera viewport
     */
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }
    /**
     * Get the camera
     */
    public OrthographicCamera getCamera() {
        return camera;
    }
    public void shake(float i) {
        shakeI = i;
        shakeT = shakeDur;
    }
    /**
     * Clean up resources
     */
    public void dispose() {
        if (tiledRenderer != null) {
            tiledRenderer.dispose();
        }
    }
}
