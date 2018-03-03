package com.codeandcoke.jumper2dx.managers;

import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.codeandcoke.jumper2dx.characters.Enemy;
import com.codeandcoke.jumper2dx.characters.Item;
import com.codeandcoke.jumper2dx.characters.Platform;
import com.codeandcoke.jumper2dx.characters.Platform.Direction;

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
	public Array<Enemy> enemies;
	public Array<Item> items;
	public Array<Platform> platforms;
	
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

        enemies = new Array<Enemy>();
        items = new Array<Item>();
        platforms = new Array<Platform>();
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
            TiledMapTileMapObject tileObject = (TiledMapTileMapObject) object;
            if (object.getProperties().containsKey(TiledMapManager.ENEMY)) {
                enemy = new Enemy();
                enemy.position.set(tileObject.getX(), tileObject.getY());
                enemies.add(enemy);
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
				items.add(item);
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
			
            TiledMapTileMapObject tileObject = (TiledMapTileMapObject) object;
            if (tileObject.getProperties().containsKey(TiledMapManager.MOBILE)) {

                Direction direction = null;
                if (Boolean.valueOf((String) tileObject.getProperties().get("right_direction")))
                    direction = Direction.RIGHT;
                else
                    direction = Direction.LEFT;

                platform = new Platform(tileObject.getX(), tileObject.getY(),
                        TiledMapManager.PLATFORM_WIDTH, TiledMapManager.PLATFORM_HEIGHT,
                        Integer.valueOf((String) tileObject.getProperties().get("offset")), direction);
                platforms.add(platform);
            }
		}
	}
	
	/**
	 * Elimina los personajes del el nivel actual
	 */
	public void clearCharactersCurrentLevel() {
		enemies.clear();
		items.clear();
		platforms.clear();
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
