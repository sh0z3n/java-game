package com.adventurequest.model.entities;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
/**
 * Portal Entity - Teleports player between maps
 */
public class Portal extends Entity {
    private String targetMap;
    private float targetX;
    private float targetY;
    private ShapeRenderer shapeRenderer;
    private float animationTimer;
    public Portal(String id, float x, float y, float width, float height, String targetMap, float targetX, float targetY) {
        super(id, x, y, width, height);
        this.targetMap = targetMap;
        this.targetX = targetX;
        this.targetY = targetY;
        this.shapeRenderer = new ShapeRenderer();
        this.animationTimer = 0;
    }
    @Override
    public void update(float deltaTime) {
        animationTimer += deltaTime * 2; // Pulsing animation
    }
    @Override
    public void render(SpriteBatch batch) {
        // End batch to draw shapes
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Pulsing portal effect
        float alpha = 0.3f + (float)Math.sin(animationTimer) * 0.2f;
        shapeRenderer.setColor(0.2f, 0.6f, 1.0f, alpha);
        shapeRenderer.rect(position.x, position.y, width, height);
        // Border
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.4f, 0.8f, 1.0f, 0.8f);
        shapeRenderer.rect(position.x, position.y, width, height);
        shapeRenderer.end();
        // Restart batch
        batch.begin();
    }
    public String getTargetMap() {
        return targetMap;
    }
    public float getTargetX() {
        return targetX;
    }
    public float getTargetY() {
        return targetY;
    }
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
