package org.sfaci.jumper2dx.managers;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import org.sfaci.jumper2dx.characters.Enemy;
import org.sfaci.jumper2dx.characters.Item;
import org.sfaci.jumper2dx.characters.Platform;
import org.sfaci.jumper2dx.characters.Platform.Direction;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

/**
 * Gestor de niveles del juego
 * @author Santiago Faci
 * @version Agosto 2014
 */
public class LevelManager {

	// Info del LevelManager
	public static final String LEVEL_DIR = "levels";
	public static final String LEVEL_PREFIX = "level";
	public static final String LEVEL_EXTENSION = ".tmx";

	// NPC del nivel actual
	public static Array<Enemy> enemies = new Array<Enemy>();
	public static Array<Item> items = new Array<Item>();
	public static Array<Platform> platforms = new Array<Platform>();
	
	// Mapa del nivel actual
	public TiledMap map;
	
	// Parámetros de nivel
	public int currentLevel;
	public int currentLives;
	public int totalCoins;
	public int currentCoins;

    // Indica si la pantalla actual es más alta que la cámara
	public boolean highLevel;

    public LevelManager() {

        currentLevel = 1;
        currentLives = 3;
        currentCoins = 0;
        totalCoins = 0;
        highLevel = true;
    }
	
	public void passCurrentLevel() {
		currentLevel++;
	}
	
	public String getCurrentLevelName() {
		return LEVEL_PREFIX + currentLevel;
	}
	
	public String getCurrentLevelPath() {
		return LEVEL_DIR + "/" + getCurrentLevelName() + LEVEL_EXTENSION;
	}
	
	/**
	 * Carga el mapa de la pantalla actual
	 */
	public void loadCurrentMap() {

        TiledMapManager.setLevelManager(this);

		map = new TmxMapLoader().load(getCurrentLevelPath());
		TiledMapManager.collisionLayer = (TiledMapTileLayer) map.getLayers().get("terrain");
		TiledMapManager.objectLayer = map.getLayers().get("objects");
		
		loadAnimateTiles();
		loadEnemies();
		loadPlatforms();
	}
	
	/**
	 * Carga los tiles animados
	 */
	private void loadAnimateTiles() {
		
		// Anima los tiles animados
		TiledMapManager.animateTiles(TiledMapManager.COIN, 4);
		TiledMapManager.animateTiles(TiledMapManager.PLANT, 3);
		TiledMapManager.animateTiles(TiledMapManager.PLANT2, 3);
		TiledMapManager.animateTiles(TiledMapManager.BOX, 4);
		TiledMapManager.animateTiles(TiledMapManager.WATER_UP, 4);
		TiledMapManager.animateTiles(TiledMapManager.WATER_DOWN, 4);
	}
	
	/**
	 * Carga los enemigos del nivel actual
	 */
	private void loadEnemies() {
		
		Enemy enemy = null;
		
		// Carga los objetos móviles del nivel actual
		for (MapObject object : map.getLayers().get("objects").getObjects()) {
			
			if (object instanceof RectangleMapObject) {
				RectangleMapObject rectangleObject = (RectangleMapObject) object;
				if (rectangleObject.getProperties().containsKey(TiledMapManager.ENEMY)) {
					Rectangle rect = rectangleObject.getRectangle();
					
					enemy = new Enemy();
					enemy.position.set(rect.x, rect.y);
					LevelManager.enemies.add(enemy);
				}
			}
		}
	}
	
	/**
	 * Sitúa un enemigo en la pantalla
	 * @param x Posición x
	 * @param y Posición y
	 */
	public void addEnemy(float x, float y) {
		
		Enemy enemy = new Enemy();
		enemy.position.set(x * map.getProperties().get("tilewidth", Integer.class), y * map.getProperties().get("tileheight",
            Integer.class));
		enemies.add(enemy);
	}
	
	/**
	 * Hace aparecer un nuevo Item en la pantalla
	 * @param x Posición x
	 * @param y Posición y
	 */
	public void raiseItem(final int x, final int y) {
		
		Timer.schedule(new Task(){
		    @Override
		    public void run() {
		    	Item item = new Item();
				item.position.set(x, y);
				LevelManager.items.add(item);
		    }
		}, 1);
	}
	
	/**
	 * Elimina una moneda de la pantalla
	 * @param x Posición x de la moneda
	 * @param y Posición y de la moneda
	 */
	public void removeCoin(int x, int y) {
		
		TiledMapManager.collisionLayer.setCell(x, y, TiledMapManager.collisionLayer.getCell(0, 5));
		currentCoins++;
	}
	
	/**
	 * Carga las plataformas móviles de la pantalla actual
	 */
	public void loadPlatforms() {
		
		Platform platform = null;
		
		// Carga los objetos móviles del nivel actual
		for (MapObject object : map.getLayers().get("objects").getObjects()) {
			
			if (object instanceof RectangleMapObject) {
				RectangleMapObject rectangleObject = (RectangleMapObject) object;
				if (rectangleObject.getProperties().containsKey(TiledMapManager.MOBILE)) {
					Rectangle rect = rectangleObject.getRectangle();
					
					Direction direction = null;
					if (Boolean.valueOf((String) rectangleObject.getProperties().get("right_direction")))
						direction = Direction.RIGHT;
					else
						direction = Direction.LEFT;
					
					platform = new Platform(rect.x, rect.y, TiledMapManager.PLATFORM_WIDTH, TiledMapManager.PLATFORM_HEIGHT, 
										    Integer.valueOf((String) rectangleObject.getProperties().get("offset")), direction);
					LevelManager.platforms.add(platform);
				}
			}
		}
	}
	
	/**
	 * Elimina los personajes del el nivel actual
	 */
	public void clearCharactersCurrentLevel() {
		LevelManager.enemies.clear();
		LevelManager.items.clear();
		LevelManager.platforms.clear();
	}
	
	/**
	 * Finaliza y limpia el nivel actual
	 */
	public void finishCurrentLevel() {

        try {
			Thread.sleep(2000);
		} catch (InterruptedException ie) {}

        totalCoins += currentCoins;
        currentCoins = 0;

		clearCharactersCurrentLevel();
		// FIXME Anotarlo y leerlo en el mapa
		if (currentLevel == 3)
			highLevel = true;
	}

    /**
     * Reinicia la pantalla actual
     * (Normalmente para jugarla otra vez)
     */
    public void restartCurrentLevel() {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {}

        currentCoins = 0;
        currentLives--;
        clearCharactersCurrentLevel();
    }
}
