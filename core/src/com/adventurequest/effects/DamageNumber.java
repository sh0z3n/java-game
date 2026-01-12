package com.adventurequest.effects;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
public class DamageNumber {
    Vector2 pos;
    String txt;
    Color col;
    float life, maxLife;
    float spd = 60f;
    float scl = 1f;
    public DamageNumber(float x, float y, String t, Color c, float dur) {
        pos = new Vector2(x, y);
        txt = t;
        col = new Color(c);
        life = dur;
        maxLife = dur;
    }
    public static DamageNumber dmg(float x, float y, int amt) {
        return new DamageNumber(x, y, "-" + amt, new Color(1f, 0.3f, 0f, 1f), 1.2f);
    }
    public static DamageNumber createDamage(float x, float y, int amt) {
        return dmg(x, y, amt);
    }
    public static DamageNumber heal(float x, float y, int amt) {
        return new DamageNumber(x, y, "+" + amt, new Color(0.2f, 1f, 0.5f, 1f), 1.5f);
    }
    public static DamageNumber createHeal(float x, float y, int amt) {
        return heal(x, y, amt);
    }
    public static DamageNumber crit(float x, float y, int amt) {
        DamageNumber n = new DamageNumber(x, y, "CRIT!" + amt, new Color(1f, 1f, 0f, 1f), 1.0f);
        n.scl = 1.5f;
        return n;
    }
    public static DamageNumber createCrit(float x, float y, int amt) {
        return crit(x, y, amt);
    }
    public void update(float dt) {
        pos.y += spd * dt;
        life -= dt;
        float a = Math.max(0f, life / maxLife);
        col.a = a;
        scl = 1f + (1f - a) * 0.3f;
    }
    public void render(SpriteBatch b, BitmapFont f) {
        f.setColor(col);
        f.getData().setScale(scl);
        float w = txt.length() * 8f * scl;
        f.draw(b, txt, pos.x - w / 2, pos.y);
        f.getData().setScale(1.5f);
    }
    public boolean isDead() {
        return life <= 0f;
    }
}
