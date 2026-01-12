package com.adventurequest.effects;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public class ParticleSystem {
    List<Particle> p;
    List<DamageNumber> dn;
    ShapeRenderer sr;
    public ParticleSystem() {
        this.p = new ArrayList<>();
        this.dn = new ArrayList<>();
        this.sr = new ShapeRenderer();
    }
    public void createDamageEffect(float x, float y) {
        for (int i = 0; i < 8; i++) {
            float a = (float) Math.random() * 360f;
            float s = 200f + (float) Math.random() * 200f;
            Vector2 v = new Vector2(
                (float) Math.cos(Math.toRadians(a)) * s,
                (float) Math.sin(Math.toRadians(a)) * s
            );
            p.add(new Particle(
                x, y,
                v,
                new Color(1f, 0.3f, 0f, 1f),
                0.4f
            ));
        }
        Gdx.app.log("ParticleSystem", "💥 Created damage effect at " + x + "," + y);
    }
    public void createHealEffect(float x, float y) {
        for (int i = 0; i < 12; i++) {
            float a = (float) Math.random() * 360f;
            float s = 150f + (float) Math.random() * 150f;
            Vector2 v = new Vector2(
                (float) Math.cos(Math.toRadians(a)) * s,
                (float) Math.sin(Math.toRadians(a)) * s
            );
            p.add(new Particle(
                x, y,
                v,
                new Color(0.2f, 1f, 0.5f, 1f),
                0.5f
            ));
        }
        Gdx.app.log("ParticleSystem", "✨ Created heal effect at " + x + "," + y);
    }
    public void createPortalEffect(float x, float y) {
        for (int i = 0; i < 15; i++) {
            float a = (float) Math.random() * 360f;
            float s = 100f + (float) Math.random() * 200f;
            Vector2 v = new Vector2(
                (float) Math.cos(Math.toRadians(a)) * s,
                (float) Math.sin(Math.toRadians(a)) * s
            );
            p.add(new Particle(x, y, v, new Color(0.5f, 0.2f, 1f, 1f), 0.6f));
        }
        Gdx.app.log("ParticleSystem", "🌀 Portal at " + x + "," + y);
    }
    public void createCritEffect(float x, float y) {
        for (int i = 0; i < 10; i++) {
            float a = (float) Math.random() * 360f;
            float s = 250f + (float) Math.random() * 150f;
            Vector2 v = new Vector2(
                (float) Math.cos(Math.toRadians(a)) * s,
                (float) Math.sin(Math.toRadians(a)) * s
            );
            p.add(new Particle(x, y, v, new Color(1f, 1f, 0f, 1f), 0.35f));
        }
        Gdx.app.log("ParticleSystem", "⭐ Crit at " + x + "," + y);
    }
    /**
     * Create a floating damage number
     */
    public void createDamageNumber(float x, float y, int amount) {
        dn.add(DamageNumber.createDamage(x, y, amount));
        Gdx.app.log("ParticleSystem", "🔴 Damage number: -" + amount + " at " + x + "," + y);
    }
    /**
     * Create a floating heal number
     */
    public void createHealNumber(float x, float y, int amount) {
        dn.add(DamageNumber.createHeal(x, y, amount));
        Gdx.app.log("ParticleSystem", "🟢 Heal number: +" + amount + " at " + x + "," + y);
    }
    /**
     * Create a critical hit damage number
     */
    public void createCritNumber(float x, float y, int amount) {
        dn.add(DamageNumber.createCrit(x, y, amount));
        Gdx.app.log("ParticleSystem", "⭐ Crit number: " + amount + " at " + x + "," + y);
    }
    /**
     * Update all p
     */
    public void update(float dt) {
        Iterator<Particle> iter = p.iterator();
        while (iter.hasNext()) {
            Particle p = iter.next();
            p.update(dt);
            if (p.isDead()) {
                iter.remove();
            }
        }
        // Update damage numbers
        Iterator<DamageNumber> numIter = dn.iterator();
        while (numIter.hasNext()) {
            DamageNumber num = numIter.next();
            num.update(dt);
            if (num.isDead()) {
                numIter.remove();
            }
        }
    }
    /**
     * Render all p and damage numbers
     */
    public void render(SpriteBatch b, ShapeRenderer sr, com.badlogic.gdx.graphics.g2d.BitmapFont font) {
        // End b and render p with shape renderer
        b.end();
        sr.setProjectionMatrix(b.getProjectionMatrix());
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (Particle p : p) {
            p.render(sr);
        }
        sr.end();
        b.begin();
        // Render damage numbers
        for (DamageNumber num : dn) {
            num.render(b, font);
        }
    }
    /**
     * Render p only (legacy support)
     */
    public void render(SpriteBatch b, ShapeRenderer sr) {
        // End b and render p with shape renderer
        b.end();
        sr.setProjectionMatrix(b.getProjectionMatrix());
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (Particle p : p) {
            p.render(sr);
        }
        sr.end();
        b.begin();
    }
    public int getParticleCount() {
        return p.size();
    }
    /**
     * Clear all p
     */
    public void clear() {
        p.clear();
    }
    /**
     * Dispose resources
     */
    public void dispose() {
        p.clear();
        if (sr != null) {
            sr.dispose();
        }
    }
    /**
     * Inner class: Individual Particle
     */
    public static class Particle {
        private Vector2 pos;
        private Vector2 vel;
        private Color col;
        private float lf;
        private float maxLf;
        private float sz = 2f;
        public Particle(float x, float y, Vector2 vel, Color col, float lf) {
            this.pos = new Vector2(x, y);
            this.vel = vel;
            this.col = new Color(col);
            this.lf = lf;
            this.maxLf = lf;
        }
        public void update(float dt) {
            // Move particle
            pos.x += vel.x * dt;
            pos.y += vel.y * dt;
            // Apply gravity
            vel.y -= 300f * dt;
            // Fade out
            lf -= dt;
            float a = lf / maxLf;
            col.a = a;
            // Shrink
            sz = 2f * a;
        }
        public void render(ShapeRenderer sr) {
            // Draw as small coled circle
            sr.setColor(col);
            sr.circle(pos.x, pos.y, Math.max(0.5f, sz));
        }
        public boolean isDead() {
            return lf <= 0f;
        }
    }
}
