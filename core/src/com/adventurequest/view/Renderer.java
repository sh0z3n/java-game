package com.adventurequest.view;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.adventurequest.effects.ParticleSystem;
import com.adventurequest.model.GameState;
import com.adventurequest.model.entities.Entity;
import com.adventurequest.model.entities.Player;
import com.adventurequest.model.entities.NPC;
import com.adventurequest.model.entities.Obstacle;
import com.adventurequest.model.entities.Chest;
import com.adventurequest.model.entities.Portal;
/**
 * Renderer - Main rendering class (View in MVC)
 *
 * Handles all visual output:
 * - Map rendering
 * - Entity rendering
 * - UI rendering
 */
public class Renderer {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private MapRenderer mapRenderer;
    private BitmapFont font;
    private ParticleSystem particleSystem;
    // Screen dimensions
    private int screenWidth;
    private int screenHeight;
    // Visual effects
    private float damageFlashTimer = 0f;
    private float healFlashTimer = 0f;
    private float screenShakeTimer = 0f;
    private float screenShakeIntensity = 0f;
    // Sound effects
    private Sound damageSound;
    private Sound healSound;
    // Power-up textures
    private Texture powerupHealthTexture;
    private Texture powerupDamageTexture;
    private Texture powerupShieldTexture;
    /**
     * Constructor
     */
    public Renderer(SpriteBatch batch) {
        this.batch = batch;
        this.shapeRenderer = new ShapeRenderer();
        this.mapRenderer = new MapRenderer();
        this.particleSystem = new ParticleSystem();
        this.font = new BitmapFont();
        this.font.getData().setScale(1.5f);
        this.font.setColor(Color.WHITE);
        this.screenWidth = 800;
        this.screenHeight = 600;
        // Load sound effects
        loadSounds();
    }
    /**
     * Load sound effects and textures
     */
    private void loadSounds() {
        try {
            // Load damage sound (hit/impact sound)
            if (Gdx.files.internal("sounds/damage.wav").exists()) {
                damageSound = Gdx.audio.newSound(Gdx.files.internal("sounds/damage.wav"));
                Gdx.app.log("Renderer", "✅ Loaded damage sound");
            } else {
                Gdx.app.log("Renderer", "⚠️ sounds/damage.wav not found");
            }
            // Load heal sound (positive/restore sound)
            if (Gdx.files.internal("sounds/heal.wav").exists()) {
                healSound = Gdx.audio.newSound(Gdx.files.internal("sounds/heal.wav"));
                Gdx.app.log("Renderer", "✅ Loaded heal sound");
            } else {
                Gdx.app.log("Renderer", "⚠️ sounds/heal.wav not found");
            }
            // Load power-up textures
            try {
                powerupHealthTexture = new Texture("sprites/powerup_health.png");
                powerupDamageTexture = new Texture("sprites/powerup_damage.png");
                powerupShieldTexture = new Texture("sprites/powerup_shield.png");
                Gdx.app.log("Renderer", "✅ Loaded power-up textures");
            } catch (Exception e) {
                Gdx.app.error("Renderer", "⚠️ Could not load power-up textures: " + e.getMessage());
            }
        } catch (Exception e) {
            Gdx.app.error("Renderer", "❌ Error loading sounds/textures: " + e.getMessage());
        }
    }
    /**
     * Render the game
     */
    public void render(GameState gameState) {
        if (gameState == null) {
            return;
        }
        // Update visual effect timers
        float deltaTime = 0.016f;
        if (damageFlashTimer > 0) damageFlashTimer -= deltaTime;
        if (healFlashTimer > 0) healFlashTimer -= deltaTime;
        if (screenShakeTimer > 0) {
            screenShakeTimer -= deltaTime;
            screenShakeIntensity = screenShakeTimer * 10f; // Decay intensity
        }
        // Render map first
        if (gameState.getCurrentMap() != null) {
            // Update camera to follow player
            if (gameState.getPlayer() != null) {
                float playerX = gameState.getPlayer().getX() + gameState.getPlayer().getWidth() / 2;
                float playerY = gameState.getPlayer().getY() + gameState.getPlayer().getHeight() / 2;
                // Apply screen shake
                if (screenShakeTimer > 0) {
                    playerX += (float)(Math.random() - 0.5) * screenShakeIntensity;
                    playerY += (float)(Math.random() - 0.5) * screenShakeIntensity;
                }
                mapRenderer.setCameraPosition(playerX, playerY);
            }
            // Update camera with smooth interpolation (call before render)
            mapRenderer.update(0.016f); // Approximate deltaTime
            mapRenderer.render(gameState.getCurrentMap());
        }
        // Set batch to use the same projection matrix as map camera
        batch.setProjectionMatrix(mapRenderer.getCamera().combined);
        batch.begin();
        // Render all entities (no color tinting)
        batch.setColor(1, 1, 1, 1); // Always normal color
        renderEntities(gameState);
        // Render power-ups
        renderPowerUps(gameState);
        // Update and render particles (including damage numbers)
        particleSystem.update(deltaTime);
        particleSystem.render(batch, shapeRenderer, font);
        batch.end();
        // Render HUD (health/speed bars)
        renderHUD(gameState);
        // Render UI elements (dialogue, etc.)
        // renderUI(gameState);
    }
    public void dmgFlash() {
        damageFlashTimer = 0.2f;
        screenShakeTimer = 0.3f;
        screenShakeIntensity = 5f;
        if (damageSound != null) {
            damageSound.play(0.7f);
        }
    }
    public void createDamageEffect(float x, float y) {
        dmgFlash();
        shake(8f);
        particleSystem.createDamageEffect(x, y);
    }
    public void createDamageEffect(float x, float y, int damage) {
        dmgFlash();
        shake(8f);
        particleSystem.createDamageEffect(x, y);
        particleSystem.createDamageNumber(x, y, damage);
    }
    public void triggerHealFlash() {
        healFlashTimer = 0.3f;
        if (healSound != null) {
            healSound.play(0.6f);
        }
    }
    public void createHealEffect(float x, float y) {
        triggerHealFlash();
        particleSystem.createHealEffect(x, y);
    }
    public void createHealEffect(float x, float y, int healAmount) {
        triggerHealFlash();
        particleSystem.createHealEffect(x, y);
        particleSystem.createHealNumber(x, y, healAmount);
    }
    public void createPortalEffect(float x, float y) {
        particleSystem.createPortalEffect(x, y);
    }
    public void shake(float i) {
        mapRenderer.shake(i);
    }
    /**
     * Create critical hit effect
     */
    public void createCritEffect(float x, float y) {
        particleSystem.createCritEffect(x, y);
    }
    /**
     * Render HUD elements (health bars, speed indicators, etc.)
     */
    private void renderHUD(GameState gameState) {
        Player player = gameState.getPlayer();
        if (player == null) return;
        // Use screen coordinates (not world coordinates)
        batch.setProjectionMatrix(batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        batch.begin();
        // Draw entity count
        font.setColor(Color.WHITE);
        font.draw(batch, "Entities: " + gameState.getEntities().size(), 10, Gdx.graphics.getHeight() - 10);
        // Draw player position
        font.draw(batch, String.format("Position: (%.0f, %.0f)", player.getX(), player.getY()), 10, Gdx.graphics.getHeight() - 35);
        // Draw player speed
        font.draw(batch, "Speed: " + (int)player.getSpeed(), 10, Gdx.graphics.getHeight() - 60);
        // Draw health text on health bar
        font.setColor(Color.WHITE);
        String healthText = String.format("HP: %.0f / %.0f", player.getHealth(), player.getMaxHealth());
        font.draw(batch, healthText, 15, Gdx.graphics.getHeight() - 83);
        batch.end();
        // Draw health bar
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Health bar background (dark gray)
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(10, Gdx.graphics.getHeight() - 100, 200, 20);
        // Health bar (green) - REAL-TIME health display
        float healthPercent = player.getHealth() / player.getMaxHealth();
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(10, Gdx.graphics.getHeight() - 100, 200 * healthPercent, 20);
        // Health bar border (white)
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(10, Gdx.graphics.getHeight() - 100, 200, 20);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Speed bar (cyan)
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(10, Gdx.graphics.getHeight() - 130, 200, 15);
        shapeRenderer.setColor(Color.CYAN);
        float speedPercent = player.getSpeed() / 300f; // Max speed assumption
        shapeRenderer.rect(10, Gdx.graphics.getHeight() - 130, 200 * speedPercent, 15);
        shapeRenderer.end();
    }
    /**
     * Render all entities
     */
    private void renderEntities(GameState gameState) {
        for (Entity entity : gameState.getEntities()) {
            if (entity.isActive()) {
                // Entities will render themselves with textures
                entity.render(batch);
            }
        }
    }
    /**
     * Render all active power-ups
     */
    private void renderPowerUps(GameState gameState) {
        for (com.adventurequest.model.entities.PowerUp powerUp : gameState.getPowerUps()) {
            if (!powerUp.isCollected()) {
                com.badlogic.gdx.graphics.Texture texture = null;
                switch (powerUp.getType()) {
                    case HP:
                        texture = powerupHealthTexture;
                        break;
                    case DMG:
                        texture = powerupDamageTexture;
                        break;
                    case SHLD:
                        texture = powerupShieldTexture;
                        break;
                }
                if (texture != null) {
                    powerUp.update(0.016f); // Update animation
                    powerUp.render(batch);
                }
            }
        }
    }
    /**
     * Handle window resize
     */
    public void resize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }
    /**
     * Clean up resources
     */
    public void dispose() {
        shapeRenderer.dispose();
        mapRenderer.dispose();
        particleSystem.dispose();
        // Dispose sound effects
        if (damageSound != null) {
            damageSound.dispose();
        }
        if (healSound != null) {
            healSound.dispose();
        }
    }
    // Getters
    public int getScreenWidth() {
        return screenWidth;
    }
    public int getScreenHeight() {
        return screenHeight;
    }
}
