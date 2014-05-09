package org.sfaci.jumper2dx;

import org.sfaci.jumper2dx.managers.GameController;
import org.sfaci.jumper2dx.managers.GameRenderer;

import com.badlogic.gdx.Game;

/**
 * Clase principal del juego
 * 
 * @author Santiago Faci
 * @version 1.0
 *
 */
public class Jumper2DX extends Game {

	public GameController gameController;
	public GameRenderer gameRenderer;
	
	public boolean paused;
	
	public enum GameState {
		START, RESUME;
	}
	public GameState gameState; 
	
	/*
	 * M�todo invocado en el momento de crearse la aplicaci�n
	 * @see com.badlogic.gdx.ApplicationListener#create()
	 */
	@Override
	public void create() {
		
		gameController = new GameController(this);
		gameRenderer = new GameRenderer(gameController);
		
		paused = false;
		gameState = GameState.START;
		
		setScreen(new MainMenuScreen(this));
	}

	/*
	 * M�todo que se invoca cada vez que hay que renderizar
	 * Es el m�todo donde se actualiza tambi�n la l�gica del juego
	 * @see com.badlogic.gdx.ApplicationListener#pause()
	 */
	@Override
	public void render() {
		super.render();
	}
	
	@Override
	public void resize(int width, int height) {
		gameRenderer.resize(width, height);
	}
	
	/*
	 * M�todo invocado cuando se destruye la aplicaci�n
	 * Siempre va precedido de una llamada a 'pause()'
	 * @see com.badlogic.gdx.ApplicationListener#dispose()
	 */
	@Override
	public void dispose() {
		
		gameRenderer.dispose();
	}
	
	/*
	 * Proporciona compatibilidad con Android
	 * El juego puede ser pasado a segundo plano y deber�a ser pausado
	 * 
	 */
	@Override
	public void pause() {
		paused = true;
	}
	
	/*
	 * Proporciona compatibilidad con Android
	 * El juego puede ser pasado a primer plano despu�s de haber sido pausado
	 * 
	 */
	@Override
	public void resume()  {
		paused = false;
	}
}
