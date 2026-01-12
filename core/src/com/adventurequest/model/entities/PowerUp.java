package com.adventurequest.model.entities;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
public class PowerUp {
    float x, y;
    static final float SZ = 24f;
    Rectangle bnd;
    Type typ;
    Texture tex;
    float rot = 0f;
    boolean col = false;
    public enum Type {
        HP(20), DMG(30), SHLD(50);
        public final float val;
        Type(float v) { this.val = v; }
    }
    public PowerUp(float x, float y, Type t, Texture tex) {
        this.x = x;
        this.y = y;
        this.typ = t;
        this.tex = tex;
        this.bnd = new Rectangle(x, y, SZ, SZ);
    }
    public void update(float dt) {
        rot += 180f * dt;
        if (rot >= 360f) rot -= 360f;
    }
    public void render(SpriteBatch b) {
        if (col || tex == null) return;
        b.setColor(1f, 1f, 1f, 0.9f);
        b.draw(tex, x, y, SZ/2, SZ/2, SZ, SZ, 1f, 1f, rot, 0, 0,
            (int)tex.getWidth(), (int)tex.getHeight(), false, false);
        b.setColor(1f, 1f, 1f, 1f);
    }
    public boolean chk(Rectangle pb) {
        return bnd.overlaps(pb);
    }
    public Type getType() { return typ; }
    public boolean checkCollision(Rectangle pb) { return chk(pb); }
    public float getX() { return x; }
    public float getY() { return y; }
    public Rectangle getBounds() { return bnd; }
    public Rectangle getBnd() { return bnd; }
    public boolean isCollected() { return col; }
    public boolean isCol() { return col; }
    public void setCollected(boolean c) { this.col = c; }
    public void setCol(boolean c) { this.col = c; }
    public float getValue() { return typ.val; }
}
