package com.adventurequest.controller;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.adventurequest.model.GameState;
import com.adventurequest.model.entities.Player;
import com.adventurequest.model.entities.Player.Direction;
/**
 * InputHandler - Handles player input
 *
 * Maps keyboard input to game actions
 */
public class InputHandler {
    /**
     * Update input handling
     */
    public void update(GameState gameState, float deltaTime) {
        if (gameState == null || gameState.getPlayer() == null) {
            return;
        }
        Player player = gameState.getPlayer();
        // Don't handle movement input if dialogue is active
        if (gameState.isDialogueActive()) {
            return;
        }
        // Handle movement input
        Direction direction = Direction.NONE;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            direction = Direction.UP;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            direction = Direction.DOWN;
        } else if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            direction = Direction.LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            direction = Direction.RIGHT;
        }
        // Move the player
        if (direction != Direction.NONE) {
            player.moveInDirection(direction, deltaTime);
        }
        // Handle jump key (SPACE or J)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            player.jump();
        }
        // Handle interaction key (E or F)
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            // Interaction will be handled by InteractionManager
            Gdx.app.log("InputHandler", "Interaction key pressed");
        }
    }
}
