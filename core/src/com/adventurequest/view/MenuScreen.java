package com.adventurequest.view;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
public class MenuScreen {
    public enum MenuState {
        MAIN_MENU,
        NEW_GAME,
        LOAD_GAME,
        PLAYING
    }
    private MenuState currentState;
    private BitmapFont font;
    private BitmapFont titleFont;
    private int selectedOption;
    private String[] menuOptions;
    private boolean hasSaveFile;
    private Texture[] bgTextures;
    private int currentBgIndex;
    private final Color tint = new Color(0, 0, 0, 0.25f);
    private Texture px;
    private final GlyphLayout gl = new GlyphLayout();
    public MenuScreen(boolean hasSaveFile) {
        this.currentState = MenuState.MAIN_MENU;
        this.font = new BitmapFont();
        this.titleFont = new BitmapFont();
        this.selectedOption = 0;
        this.hasSaveFile = hasSaveFile;
        this.menuOptions = hasSaveFile ?
            new String[]{"New Game", "Load Game", "Exit"} :
            new String[]{"New Game", "Exit"};
        font.getData().setScale(2.0f);
        titleFont.getData().setScale(4.0f);
        px = makePixel();
        loadBackgrounds();
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
                Gdx.app.log("MenuScreen", "✅ Loaded bg/" + i + ".jpg");
            } catch (Exception e) {
                Gdx.app.log("MenuScreen", "⚠️  Failed to load bg/" + i + ".jpg");
                bgTextures[i] = null;
            }
        }
        currentBgIndex = (int)(Math.random() * 5);
    }
    public void render(SpriteBatch batch) {
        if (currentState == MenuState.PLAYING) {
            return;
        }
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
    String title = "ADVENTURE QUEST";
    gl.setText(titleFont, title);
    titleFont.draw(batch, title, (screenWidth - gl.width) / 2f, screenHeight - 100);

    font.setColor(Color.LIGHT_GRAY);
    String subtitle = "A Professional 2D RPG Game";
    gl.setText(font, subtitle);
    font.draw(batch, subtitle, (screenWidth - gl.width) / 2f, screenHeight - 180);
        float startY = screenHeight / 2f + 90;
        for (int i = 0; i < menuOptions.length; i++) {
            float y = startY - i * 80;
            if (i == selectedOption) {
                batch.setColor(0, 0, 0, 0.35f);
                batch.draw(px, screenWidth / 2f - 210, y - 40, 420, 55);
                batch.setColor(1, 1, 1, 1);

                font.setColor(Color.YELLOW);
                String s = "> " + menuOptions[i] + " <";
                gl.setText(font, s);
                font.draw(batch, s, (screenWidth - gl.width) / 2f, y);
            } else {
                font.setColor(Color.WHITE);
                String s = menuOptions[i];
                gl.setText(font, s);
                font.draw(batch, s, (screenWidth - gl.width) / 2f, y);
            }
        }
        font.setColor(Color.GRAY);
        font.getData().setScale(1.5f);
        String hint = "[W/S] Navigate | [ENTER] Select | [ESC] Back";
        gl.setText(font, hint);
        font.draw(batch, hint, (screenWidth - gl.width) / 2f, 80);
        font.getData().setScale(2.0f);
        batch.end();
    }
    public void navigateUp() {
        selectedOption--;
        if (selectedOption < 0) {
            selectedOption = menuOptions.length - 1;
        }
    }
    public void navigateDown() {
        selectedOption++;
        if (selectedOption >= menuOptions.length) {
            selectedOption = 0;
        }
    }
    public void selectOption() {
        if (hasSaveFile) {
            switch (selectedOption) {
                case 0:
                    currentState = MenuState.NEW_GAME;
                    Gdx.app.log("MenuScreen", "Starting new game...");
                    break;
                case 1:
                    currentState = MenuState.LOAD_GAME;
                    Gdx.app.log("MenuScreen", "Loading saved game...");
                    break;
                case 2:
                    Gdx.app.exit();
                    break;
            }
        } else {
            switch (selectedOption) {
                case 0:
                    currentState = MenuState.NEW_GAME;
                    Gdx.app.log("MenuScreen", "Starting new game...");
                    break;
                case 1:
                    Gdx.app.exit();
                    break;
            }
        }
    }
    public MenuState getCurrentState() {
        return currentState;
    }
    public void setState(MenuState state) {
        this.currentState = state;
    }
    public boolean isPlaying() {
        return currentState == MenuState.PLAYING || currentState == MenuState.NEW_GAME;
    }
    public void dispose() {
        font.dispose();
        titleFont.dispose();
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
