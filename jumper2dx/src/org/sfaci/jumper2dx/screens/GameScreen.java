package org.sfaci.jumper2dx.screens;

import com.badlogic.gdx.Input;
import org.sfaci.jumper2dx.Jumper2DX;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import org.sfaci.jumper2dx.managers.SpriteManager;

/**
 * Pantalla de Juego, donde el usuario juega la partida
 * @author Santiago Faci
 * @version Agosto 2014
 */
public class GameScreen implements Screen {

	final Jumper2DX game;
    public SpriteManager spriteManager;
	
	public GameScreen(Jumper2DX game) {
		this.game = game;

        spriteManager = new SpriteManager(game);
        game.paused = false;
	}
	
	/*
	 * Método que se invoca cuando esta pantalla es
	 * la que se está mostrando
	 * @see com.badlogic.gdx.Screen#show()
	 */
	@Override
	public void show() {
        game.paused = false;
	}
	
	@Override
	public void render(float dt) {
		
		if (!game.paused) {
			// Actualizamos primero (es más eficiente)
			spriteManager.update(dt);
		}
		
		// En cada frame se limpia la pantalla
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteManager.draw();

        handleKeyboard();
	}

    private void handleKeyboard() {

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new InGameMenuScreen(game, this));
        }
    }
	
	/*
	 * Método que se invoca cuando esta pantalla
	 * deja de ser la principal
	 * @see com.badlogic.gdx.Screen#hide()
	 */
	@Override
	public void hide() {
		game.paused = true;
	}
	
	@Override
	public void dispose() {
        spriteManager.dispose();
	}

	@Override
	public void resize(int width, int height) {
		spriteManager.resize(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
        game.paused = false;
	}
}
