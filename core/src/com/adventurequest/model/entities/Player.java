package com.adventurequest.model.entities;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.adventurequest.model.interfaces.Collidable;
import com.adventurequest.model.interfaces.Updatable;
/**
 * Player Entity
 *
 * Demonstrates OOP Concepts:
 * - Inheritance: Extends Entity
 * - Interfaces: Implements Collidable and Updatable
 * - Polymorphism: Can be treated as Entity, Collidable, or Updatable
 */
public class Player extends Entity implements Collidable, Updatable {
    // Player-specific properties
    private float speed;
    private Direction currentDirection;
    private Texture currentTexture;
    // Directional textures
    private Texture textureUp;
    private Texture textureDown;
    private Texture textureLeft;
    private Texture textureRight;
    // Jump textures
    private Texture textureJumpStart;
    private Texture textureJumpLoop;
    private Texture textureFalling;
    // Jump mechanics
    private float velocityY;
    private boolean isJumping;
    private boolean isGrounded;
    private float groundY;
    private JumpState jumpState;
    // Animation timer
    private float animationTimer;
    // Movement constants
    private static final float DEFAULT_SPEED = 150f; // pixels per second (increased)
    private static final float JUMP_VELOCITY = 350f; // jump strength
    private static final float GRAVITY = -800f; // gravity force
    private static final float JUMP_START_DURATION = 0.15f;
    private static final float MAX_FALL_SPEED = -500f;
    // Map boundaries
    private float mapMinX = 0;
    private float mapMaxX = 10000; // Will be set properly
    private float mapMinY = 0;
    private float mapMaxY = 10000; // Will be set properly
    // Health
    private float maxHealth = 100f;
    private float health = 100f;
    // Respawn point
    private float spawnX;
    private float spawnY;
    /**
     * Jump state enumeration
     */
    public enum JumpState {
        GROUNDED, JUMP_START, JUMP_LOOP, FALLING
    }
    /**
     * Direction enumeration for player movement
     */
    public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }
    /**
     * Constructor for Player
     */
    public Player(String id, float x, float y) {
        super(id, x, y, 64, 64); // Increased to 64x64 for better visibility
        this.speed = DEFAULT_SPEED;
        this.currentDirection = Direction.NONE;
        this.velocityY = 0;
        this.isJumping = false;
        this.isGrounded = true;
        this.groundY = y;
        this.jumpState = JumpState.GROUNDED;
        this.animationTimer = 0;
        this.spawnX = x;
        this.spawnY = y;
        // Load directional player textures
        try {
            textureUp = new Texture(Gdx.files.internal("sprites/player_up.png"));
            textureDown = new Texture(Gdx.files.internal("sprites/player_down.png"));
            textureLeft = new Texture(Gdx.files.internal("sprites/player_left.png"));
            textureRight = new Texture(Gdx.files.internal("sprites/player_right.png"));
            // Load jump textures
            textureJumpStart = new Texture(Gdx.files.internal("sprites/player_jump_start.png"));
            textureJumpLoop = new Texture(Gdx.files.internal("sprites/player_jump_loop.png"));
            textureFalling = new Texture(Gdx.files.internal("sprites/player_falling.png"));
            currentTexture = textureDown; // Default facing down
            Gdx.app.log("Player", "Player textures loaded successfully");
        } catch (Exception e) {
            Gdx.app.log("Player", "Could not load player textures: " + e.getMessage());
            currentTexture = null;
        }
    }
    /**
     * Update player state
     * Called every frame
     */
    @Override
    public void update(float deltaTime) {
        animationTimer += deltaTime;
        // Apply gravity and update vertical position
        if (!isGrounded) {
            velocityY += GRAVITY * deltaTime;
            // Clamp fall speed
            if (velocityY < MAX_FALL_SPEED) {
                velocityY = MAX_FALL_SPEED;
            }
            // Update position
            position.y += velocityY * deltaTime;
            // Check if landed
            if (position.y <= groundY) {
                position.y = groundY;
                velocityY = 0;
                isGrounded = true;
                isJumping = false;
                jumpState = JumpState.GROUNDED;
            }
        }
        // Update jump animation state
        updateJumpState(deltaTime);
    }
    /**
     * Update jump animation state
     */
    private void updateJumpState(float deltaTime) {
        if (isGrounded) {
            jumpState = JumpState.GROUNDED;
        } else if (isJumping) {
            if (animationTimer < JUMP_START_DURATION) {
                jumpState = JumpState.JUMP_START;
                currentTexture = textureJumpStart;
            } else if (velocityY > 0) {
                jumpState = JumpState.JUMP_LOOP;
                currentTexture = textureJumpLoop;
            } else {
                jumpState = JumpState.FALLING;
                currentTexture = textureFalling;
            }
        } else if (velocityY < 0) {
            jumpState = JumpState.FALLING;
            currentTexture = textureFalling;
        }
    }
    /**
     * Make the player jump
     */
    public void jump() {
        if (isGrounded) {
            velocityY = JUMP_VELOCITY;
            isGrounded = false;
            isJumping = true;
            animationTimer = 0;
            jumpState = JumpState.JUMP_START;
            currentTexture = textureJumpStart;
            Gdx.app.log("Player", "Jump!");
        }
    }
    /**
     * Move the player in a direction
     * @param direction Direction to move
     * @param deltaTime Time elapsed since last frame
     */
    public void moveInDirection(Direction direction, float deltaTime) {
        this.currentDirection = direction;
        // Only update directional texture if grounded (not during jump)
        if (isGrounded || jumpState == JumpState.GROUNDED) {
            switch (direction) {
                case UP:
                    currentTexture = textureUp;
                    break;
                case DOWN:
                    currentTexture = textureDown;
                    break;
                case LEFT:
                    currentTexture = textureLeft;
                    break;
                case RIGHT:
                    currentTexture = textureRight;
                    break;
                case NONE:
                    // Keep current texture
                    break;
            }
        }
        float distance = speed * deltaTime;
        float oldX = position.x;
        float oldY = position.y;
        switch (direction) {
            case UP:
                move(0, distance);
                break;
            case DOWN:
                move(0, -distance);
                break;
            case LEFT:
                move(-distance, 0);
                break;
            case RIGHT:
                move(distance, 0);
                break;
            case NONE:
                break;
        }
        // Clamp position to map boundaries
        if (position.x < mapMinX) {
            position.x = mapMinX;
        }
        if (position.x + width > mapMaxX) {
            position.x = mapMaxX - width;
        }
        if (position.y < mapMinY) {
            position.y = mapMinY;
        }
        if (position.y + height > mapMaxY) {
            position.y = mapMaxY - height;
        }
        // Update bounds
        bounds.setPosition(position.x, position.y);
    }
    /**
     * Apply damage to the player.
     * @return true if player is dead (health <= 0)
     */
    public boolean damage(float amount) {
        health -= amount;
        if (health < 0) health = 0;
        Gdx.app.log("Player", "Health: " + health + "/" + maxHealth);
        return health <= 0;
    }
    /**
     * Respawn player at spawn point and restore health.
     */
    public void revive() {
        this.health = maxHealth;
        setPosition(spawnX, spawnY);
        this.velocityY = 0;
        this.isJumping = false;
        this.isGrounded = true;
        this.jumpState = JumpState.GROUNDED;
        this.currentTexture = textureDown != null ? textureDown : currentTexture;
        Gdx.app.log("Player", "Revived at (" + spawnX + "," + spawnY + ")");
    }
    public void setSpawn(float x, float y) {
        this.spawnX = x;
        this.spawnY = y;
    }
    public float getHealth() {
        return health;
    }
    public void setHealth(float health) {
        this.health = Math.max(0, Math.min(health, maxHealth));
        Gdx.app.log("Player", "Health set to: " + this.health + "/" + maxHealth);
    }
    public float getMaxHealth() {
        return maxHealth;
    }
    /**
     * Set map boundaries to prevent player from leaving map
     */
    public void setMapBoundaries(float minX, float maxX, float minY, float maxY) {
        this.mapMinX = minX;
        this.mapMaxX = maxX;
        this.mapMinY = minY;
        this.mapMaxY = maxY;
        Gdx.app.log("Player", "Map boundaries set: (" + minX + "," + minY + ") to (" + maxX + "," + maxY + ")");
    }
    /**
     * Render the player
     */
    @Override
    public void render(SpriteBatch batch) {
        if (currentTexture != null) {
            batch.draw(currentTexture, position.x, position.y, width, height);
        } else {
            // Placeholder rendering (colored square)
            // This will be handled by the renderer
        }
    }
    // Collidable interface implementation
    @Override
    public boolean isSolid() {
        return true;
    }
    @Override
    public void onCollision(Entity other) {
        // Handle collision response
        // For now, just log it
        Gdx.app.log("Player", "Collided with: " + other.getId());
    }
    // Getters and Setters
    public float getSpeed() {
        return speed;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public Direction getCurrentDirection() {
        return currentDirection;
    }
    public void dispose() {
        if (textureUp != null) textureUp.dispose();
        if (textureDown != null) textureDown.dispose();
        if (textureLeft != null) textureLeft.dispose();
        if (textureRight != null) textureRight.dispose();
        if (textureJumpStart != null) textureJumpStart.dispose();
        if (textureJumpLoop != null) textureJumpLoop.dispose();
        if (textureFalling != null) textureFalling.dispose();
    }
    // Additional getters
    public boolean isJumping() {
        return isJumping;
    }
    public boolean isGrounded() {
        return isGrounded;
    }
    public JumpState getJumpState() {
        return jumpState;
    }
    public void setGroundY(float groundY) {
        this.groundY = groundY;
    }
}
