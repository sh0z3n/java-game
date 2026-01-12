package com.adventurequest.controller;
import com.adventurequest.model.GameState;
import com.adventurequest.model.entities.Entity;
import com.adventurequest.model.entities.PowerUp;
import com.adventurequest.model.interfaces.Collidable;
import java.util.List;
public class CollisionManager {
    float dmgCd = 0f;
    static final float DMG_INT = 0.5f;
    boolean first = true;
    com.adventurequest.view.Renderer rend = null;
    public void setRenderer(com.adventurequest.view.Renderer r) {
        this.rend = r;
    }
    public void checkCollisions(GameState gameState) {
        if (dmgCd > 0) {
            dmgCd -= com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        }
        if (gameState == null) return;
        List<Entity> entities = gameState.getEntities();
        if (first) {
            int cnt = 0;
            for (Entity e : entities) {
                if (e instanceof com.adventurequest.model.entities.Obstacle) {
                    com.adventurequest.model.entities.Obstacle obs = (com.adventurequest.model.entities.Obstacle) e;
                    cnt++;
                }
            }
            com.badlogic.gdx.Gdx.app.log("CollisionManager", "Total obstacles: " + cnt);
            first = false;
        }
        for (int i = 0; i < entities.size(); i++) {
            Entity a = entities.get(i);
            if (!a.isActive()) continue;
            for (int j = i + 1; j < entities.size(); j++) {
                Entity b = entities.get(j);
                if (!b.isActive()) continue;
                if (a.overlaps(b)) {
                    hdlCol(a, b);
                }
            }
        }
        com.adventurequest.model.entities.Player p = gameState.getPlayer();
        if (p != null) {
            List<PowerUp> pus = gameState.getPowerUps();
            for (int i = 0; i < pus.size(); i++) {
                PowerUp pu = pus.get(i);
                if (!pu.isCollected() && pu.checkCollision(p.getBounds())) {
                    applyPU(p, pu);
                    pu.setCollected(true);
                }
            }
        }
    }
    private void hdlCol(Entity a, Entity b) {
        if (a instanceof Collidable) {
            ((Collidable) a).onCollision(b);
        }
        if (b instanceof Collidable) {
            ((Collidable) b).onCollision(a);
        }
        if (a instanceof com.adventurequest.model.entities.Player && b instanceof com.adventurequest.model.entities.Obstacle) {
            com.adventurequest.model.entities.Obstacle o = (com.adventurequest.model.entities.Obstacle) b;
            dmgPly((com.adventurequest.model.entities.Player) a, o);
        } else if (b instanceof com.adventurequest.model.entities.Player && a instanceof com.adventurequest.model.entities.Obstacle) {
            com.adventurequest.model.entities.Obstacle o = (com.adventurequest.model.entities.Obstacle) a;
            dmgPly((com.adventurequest.model.entities.Player) b, o);
        }
    }
    private void dmgPly(com.adventurequest.model.entities.Player p, com.adventurequest.model.entities.Obstacle o) {
        if (o.getObstacleType().contains("heal")) {
            if (dmgCd <= 0) {
                float oh = p.getHealth();
                float mh = p.getMaxHealth();
                float nh = Math.min(oh + 20f, mh);
                p.setHealth(nh);
                dmgCd = DMG_INT;
                com.badlogic.gdx.Gdx.app.log("Collision", "🟢 HEALED! +20 HP [" + nh + "/" + mh + "]");
                if (rend != null) {
                    rend.createHealEffect(o.getX() + o.getWidth()/2, o.getY() + o.getHeight()/2, 20);
                }
            }
        } else {
            if (dmgCd <= 0) {
                boolean d = p.damage(15f);
                dmgCd = DMG_INT;
                com.badlogic.gdx.Gdx.app.log("Collision", "💥 DAMAGE! " + o.getObstacleType() + " -15 HP");
                if (rend != null) {
                    rend.createDamageEffect(p.getX() + p.getWidth()/2, p.getY() + p.getHeight()/2, 15);
                }
                if (d) {
                    com.badlogic.gdx.Gdx.app.log("Collision", "☠️ PLAYER DIED! Respawning...");
                    p.revive();
                }
            }
        }
    }
    private void applyPU(com.adventurequest.model.entities.Player p, PowerUp pu) {
        switch (pu.getType()) {
            case HP:
                float h = Math.min(pu.getValue(), p.getMaxHealth() - p.getHealth());
                p.setHealth(p.getHealth() + h);
                if (rend != null) {
                    rend.createHealEffect(p.getX() + p.getWidth()/2, p.getY() + p.getHeight()/2, (int)pu.getValue());
                }
                com.badlogic.gdx.Gdx.app.log("Collision", "🟢 HEALTH BOOST! +" + (int)pu.getValue() + " HP");
                break;
            case DMG:
                if (rend != null) {
                    rend.createPortalEffect(p.getX() + p.getWidth()/2, p.getY() + p.getHeight()/2);
                }
                com.badlogic.gdx.Gdx.app.log("Collision", "⚡ DAMAGE BOOST! +30% damage for 10 seconds");
                break;
            case SHLD:
                if (rend != null) {
                    rend.createPortalEffect(p.getX() + p.getWidth()/2, p.getY() + p.getHeight()/2);
                }
                com.badlogic.gdx.Gdx.app.log("Collision", "🛡️ SHIELD GRANTED! " + (int)pu.getValue() + " HP protection");
                break;
        }
    }
    /**
     * Check if an entity would collide with map collision layer
     */
    public boolean wouldCollideWithMap(Entity entity, GameState gameState, float newX, float newY) {
        if (gameState.getCurrentMap() == null) {
            return false;
        }
        // Check corners of the entity bounds
        boolean topLeft = gameState.getCurrentMap().isCollisionAt(newX, newY + entity.getHeight());
        boolean topRight = gameState.getCurrentMap().isCollisionAt(newX + entity.getWidth(), newY + entity.getHeight());
        boolean bottomLeft = gameState.getCurrentMap().isCollisionAt(newX, newY);
        boolean bottomRight = gameState.getCurrentMap().isCollisionAt(newX + entity.getWidth(), newY);
        return topLeft || topRight || bottomLeft || bottomRight;
    }
}
