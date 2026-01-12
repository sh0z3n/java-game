package com.adventurequest.view;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Pixmap;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class SaveLoadScreen {
    public enum ScreenType {
        SAVE,
        LOAD
    }
    private ScreenType screenType;
    private BitmapFont font;
    private BitmapFont titleFont;
    private ShapeRenderer shapeRenderer;
    private int selectedSlot;
    private List<SaveSlotInfo> saveSlots;
    private boolean isConfirming;
    private String confirmMessage;
    private Texture[] bgTextures;
    private int currentBgIndex;
    private final Color tint = new Color(0, 0, 0, 0.25f);
    private Texture px;
    private final GlyphLayout gl = new GlyphLayout();
    public static class SaveSlotInfo {
        public String slotName;
        public String timestamp;
        public String playerInfo;
        public boolean exists;
        public SaveSlotInfo(String slotName, String timestamp, String playerInfo, boolean exists) {
            this.slotName = slotName;
            this.timestamp = timestamp;
            this.playerInfo = playerInfo;
            this.exists = exists;
        }
    }
    public SaveLoadScreen(ScreenType screenType) {
        this.screenType = screenType;
        this.font = new BitmapFont();
        this.titleFont = new BitmapFont();
        this.shapeRenderer = new ShapeRenderer();
        this.selectedSlot = 0;
        this.isConfirming = false;
        this.confirmMessage = "";
        this.saveSlots = new ArrayList<>();
        font.getData().setScale(2.0f);
        titleFont.getData().setScale(3.0f);
        px = makePixel();
        loadBackgrounds();
        loadSaveSlots();
    }

    private Texture makePixel() {
        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(1, 1, 1, 1);
        p.fill();
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }
    private void loadBackgrounds() {
        bgTextures = new Texture[5];
        for (int i = 0; i < 5; i++) {
            try {
                bgTextures[i] = new Texture(Gdx.files.internal("bg/" + i + ".jpg"));
                bgTextures[i].setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            } catch (Exception e) {
                bgTextures[i] = null;
            }
        }
        currentBgIndex = (int)(Math.random() * 5);
    }
    private void loadSaveSlots() {
        saveSlots.clear();
        File saveDir = new File("saves");
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        for (int i = 1; i <= 5; i++) {
            File slotFile = new File(saveDir, "slot_" + i + ".dat");
            if (slotFile.exists()) {
                long lastModified = slotFile.lastModified();
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(lastModified));
                saveSlots.add(new SaveSlotInfo("SLOT " + i, timestamp, "[Saved Game]", true));
            } else {
                saveSlots.add(new SaveSlotInfo("SLOT " + i, "-- EMPTY --", "", false));
            }
        }
    }
    public void render(SpriteBatch batch) {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        if (bgTextures != null && bgTextures[currentBgIndex] != null) {
            batch.draw(bgTextures[currentBgIndex], 0, 0, screenWidth, screenHeight);
        }
        batch.setColor(tint);
        batch.draw(px, 0, 0, screenWidth, screenHeight);
        batch.setColor(1, 1, 1, 1);

        titleFont.setColor(Color.GOLD);
        String title = screenType == ScreenType.SAVE ? "SAVE GAME" : "LOAD GAME";
        gl.setText(titleFont, title);
        titleFont.draw(batch, title, (screenWidth - gl.width) / 2f, screenHeight - 80);
        font.setColor(Color.LIGHT_GRAY);
        float startY = screenHeight - 180;
        for (int i = 0; i < saveSlots.size(); i++) {
            SaveSlotInfo slot = saveSlots.get(i);
            float slotY = startY - i * 70;
            if (i == selectedSlot) {
                font.setColor(Color.YELLOW);
                font.draw(batch, "> " + slot.slotName + " <", 100, slotY);
            } else {
                font.setColor(slot.exists ? Color.WHITE : Color.GRAY);
                font.draw(batch, slot.slotName, 120, slotY);
            }
            font.getData().setScale(1.5f);
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, slot.timestamp, 350, slotY);
            if (slot.exists) {
                font.setColor(Color.GREEN);
                font.draw(batch, slot.playerInfo, 700, slotY);
            }
            font.getData().setScale(2.0f);
        }
        if (isConfirming) {
            drawConfirmDialog(batch, screenWidth, screenHeight);
        }
        font.setColor(Color.GRAY);
        font.getData().setScale(1.5f);
        font.draw(batch, "[W/S] Select | [ENTER] Choose | [Y] Confirm | [N] Cancel | [ESC] Back", 30, 50);
        font.getData().setScale(2.0f);
        batch.end();
    }
    private void drawConfirmDialog(SpriteBatch batch, int screenWidth, int screenHeight) {
        int dialogWidth = 500;
        int dialogHeight = 200;
        int dialogX = (screenWidth - dialogWidth) / 2;
        int dialogY = (screenHeight - dialogHeight) / 2;
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 0.9f);
        shapeRenderer.rect(dialogX, dialogY, dialogWidth, dialogHeight);
        shapeRenderer.setColor(1, 1, 1, 0.8f);
        shapeRenderer.rect(dialogX + 5, dialogY + 5, dialogWidth - 10, dialogHeight - 10);
        shapeRenderer.end();
        batch.begin();
        font.setColor(Color.BLACK);
        font.draw(batch, confirmMessage, dialogX + 50, dialogY + 150);
        font.getData().setScale(1.5f);
        font.draw(batch, "[Y]es  [N]o", dialogX + 150, dialogY + 50);
        font.getData().setScale(2.0f);
    }
    public void navigateUp() {
        selectedSlot--;
        if (selectedSlot < 0) {
            selectedSlot = saveSlots.size() - 1;
        }
    }
    public void navigateDown() {
        selectedSlot++;
        if (selectedSlot >= saveSlots.size()) {
            selectedSlot = 0;
        }
    }
    public void selectSlot() {
        SaveSlotInfo slot = saveSlots.get(selectedSlot);
        if (screenType == ScreenType.SAVE) {
            if (slot.exists) {
                confirmMessage = "Overwrite '" + slot.slotName + "'?";
                isConfirming = true;
            } else {
                confirmMessage = "Save to '" + slot.slotName + "'?";
                isConfirming = true;
            }
        } else if (screenType == ScreenType.LOAD) {
            if (slot.exists) {
                confirmMessage = "Load '" + slot.slotName + "'?";
                isConfirming = true;
            } else {
                Gdx.app.log("SaveLoadScreen", "Slot is empty!");
            }
        }
    }
    public void confirmYes() {
        if (isConfirming) {
            SaveSlotInfo slot = saveSlots.get(selectedSlot);
            Gdx.app.log("SaveLoadScreen", (screenType == ScreenType.SAVE ? "Saving" : "Loading") + " " + slot.slotName);
            isConfirming = false;
            confirmMessage = "";
        }
    }
    public void confirmNo() {
        isConfirming = false;
        confirmMessage = "";
    }
    public int getSelectedSlot() {
        return selectedSlot + 1;
    }
    public SaveSlotInfo getSelectedSlotInfo() {
        return saveSlots.get(selectedSlot);
    }
    public void dispose() {
        font.dispose();
        titleFont.dispose();
        shapeRenderer.dispose();
        if (px != null) {
            px.dispose();
        }
        for (Texture bg : bgTextures) {
            if (bg != null) {
                bg.dispose();
            }
        }
    }
}
