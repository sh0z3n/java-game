package com.adventurequest.model.entities;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.adventurequest.model.interfaces.Collidable;
/**
 * Spike hazard: damages/kills player on touch.
 * Non-solid so the player can overlap and take damage.
 */
public class Spike extends Entity implements Collidable {
    private Texture texture;
    public Spike(String id, float x, float y, float width, float height) {
        super(id, x, y, width, height);
        try {
            texture = new Texture(Gdx.files.internal("sprites/spike.png"));
        } catch (Exception e) {
            texture = null; // fallback: invisible but still harms
        }
    }
    @Override
    public void update(float deltaTime) {
        // Static hazard
    }
    @Override
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x, position.y, width, height);
        }
    }
    @Override
    public boolean isSolid() {
        return false; // allow overlap to apply damage
    }
    @Override
    public void onCollision(Entity other) {
        // Damage handled in CollisionManager
    }
    public void dispose() {
        if (texture != null) texture.dispose();
    }
}
