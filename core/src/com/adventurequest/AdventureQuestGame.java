package com.adventurequest;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.adventurequest.controller.GameController;
import com.adventurequest.view.Renderer;
import com.adventurequest.view.MenuScreen;
import com.adventurequest.view.SaveLoadScreen;
public class AdventureQuestGame extends ApplicationAdapter {
    private enum GameState { MENU, LOAD, SAVE, PLAYING }
    private GameState currentState;
    private GameController gameController;
    private Renderer renderer;
    private SpriteBatch batch;
    private MenuScreen menuScreen;
    private SaveLoadScreen loadScreen;
    private SaveLoadScreen saveScreen;
    @Override
    public void create() {
        currentState = GameState.MENU;
        batch = new SpriteBatch();
        gameController = new GameController();
        renderer = new Renderer(batch);
        gameController.setRenderer(renderer);
        boolean hasSaveFile = new java.io.File("saves/slot_1.dat").exists();
        menuScreen = new MenuScreen(hasSaveFile);
        loadScreen = new SaveLoadScreen(SaveLoadScreen.ScreenType.LOAD);
        saveScreen = new SaveLoadScreen(SaveLoadScreen.ScreenType.SAVE);
        Gdx.app.log("AdventureQuest", "🎮 Game created - showing menu");
    }
    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        handleMenuInput();
        if (currentState == GameState.MENU) {
            menuScreen.render(batch);
        } else if (currentState == GameState.LOAD) {
            loadScreen.render(batch);
        } else if (currentState == GameState.SAVE) {
            saveScreen.render(batch);
        } else if (currentState == GameState.PLAYING) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            gameController.update(deltaTime);
            renderer.render(gameController.getGameState());
        }
    }
    private void handleMenuInput() {
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.W) ||
            com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.UP)) {
            if (currentState == GameState.MENU) {
                menuScreen.navigateUp();
            } else if (currentState == GameState.LOAD) {
                loadScreen.navigateUp();
            } else if (currentState == GameState.SAVE) {
                saveScreen.navigateUp();
            }
        }
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S) ||
            com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            if (currentState == GameState.MENU) {
                menuScreen.navigateDown();
            } else if (currentState == GameState.LOAD) {
                loadScreen.navigateDown();
            } else if (currentState == GameState.SAVE) {
                saveScreen.navigateDown();
            }
        }
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER) ||
            com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            if (currentState == GameState.MENU) {
                menuScreen.selectOption();
                handleMenuSelection();
            } else if (currentState == GameState.LOAD || currentState == GameState.SAVE) {
                if (currentState == GameState.LOAD) {
                    loadScreen.selectSlot();
                } else {
                    saveScreen.selectSlot();
                }
            }
        }
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.Y)) {
            if (currentState == GameState.LOAD) {
                loadScreen.confirmYes();
                currentState = GameState.PLAYING;
            } else if (currentState == GameState.SAVE) {
                saveScreen.confirmYes();
                gameController.saveGame();
                currentState = GameState.PLAYING;
            }
        }
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.N)) {
            if (currentState == GameState.LOAD) {
                loadScreen.confirmNo();
            } else if (currentState == GameState.SAVE) {
                saveScreen.confirmNo();
            }
        }
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            if (currentState == GameState.LOAD || currentState == GameState.SAVE) {
                currentState = GameState.MENU;
                boolean hasSaveFile = new java.io.File("saves/slot_1.dat").exists();
                menuScreen = new MenuScreen(hasSaveFile);
            }
        }
    }
    private void handleMenuSelection() {
        MenuScreen.MenuState state = menuScreen.getCurrentState();
        switch (state) {
            case NEW_GAME:
                gameController.initialize();
                currentState = GameState.PLAYING;
                Gdx.app.log("AdventureQuest", "🎮 Starting new game");
                break;
            case LOAD_GAME:
                currentState = GameState.LOAD;
                Gdx.app.log("AdventureQuest", "📂 Load game screen");
                break;
            default:
                break;
        }
    }
    /**
     * Called when the window is resized
     */
    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }
    @Override
    public void dispose() {
        batch.dispose();
        renderer.dispose();
        gameController.dispose();
        menuScreen.dispose();
        loadScreen.dispose();
        saveScreen.dispose();
        Gdx.app.log("AdventureQuest", "Game disposed");
    }
}
