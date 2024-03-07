package com.codeandcoke.jumper2dx.managers;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import com.codeandcoke.jumper2dx.Jumper2DX;
import com.codeandcoke.jumper2dx.characters.Enemy;
import com.codeandcoke.jumper2dx.characters.Item;
import com.codeandcoke.jumper2dx.characters.Platform;
import com.codeandcoke.jumper2dx.characters.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Controla toda la lógica y renderizado del juego
 * @author Santiago Faci
 * @version Agosto 2014
 */
public class SpriteManager implements ControllerListener{

	public Jumper2DX game;

    private Batch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    public static float CAMERA_OFFSET = 0;
    OrthogonalTiledMapRenderer mapRenderer;
	
	Player player;
    // Música de fondo que suena actualmente
    public Music music;

    public LevelManager levelManager;

    enum PlayerState {
    	IDLE, LEFT, RIGHT, UP, DOWN
	}
	private PlayerState playerState;
	
	public SpriteManager(Jumper2DX game) {
        this.game = game;

        levelManager = new LevelManager();

        font = new BitmapFont(Gdx.files.internal("ui/default.fnt"));

        // Crea una cámara y muestra 30x20 unidades del mundo
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 30, 20);
        camera.update();
        CAMERA_OFFSET = 0;

        // Activa face culling
        Gdx.gl.glCullFace(GL20.GL_CULL_FACE);

        loadCurrentLevel();

		Controllers.addListener(this);
		playerState = PlayerState.IDLE;
	}

    /**
     * Carga el nivel actual
     */
    public void loadCurrentLevel() {

        // Crea y carga el mapa
        levelManager.loadCurrentMap();
        mapRenderer = new OrthogonalTiledMapRenderer(levelManager.map);
        batch = mapRenderer.getBatch();

        // Crea el jugador y lo posiciona al inicio de la pantalla
        player = new Player(this);
        // posición inicial del jugador
        player.position.set(2 * levelManager.map.getProperties().get("tilewidth", Integer.class),
                2 * levelManager.map.getProperties().get("tileheight", Integer.class) + 32);

        // Música durante la partida
        music = ResourceManager.getMusic("sounds/" + levelManager.getCurrentLevelName() + ".mp3");
        music.setLooping(true);
        music.play();
	}

    public void passCurrentLevel() {

        levelManager.passCurrentLevel();
        mapRenderer.dispose();
        loadCurrentLevel();
    }
	
	public void update(float dt) {
		
		// Comprobar entrada de usuario (teclado, pantalla, ratón, . . .)
		handleInput();
		
		if (game.paused)
			return;
			
		// Actualizar jugador
		player.update(dt);
		
		// Comprueba colisiones del jugador con elementos móviles del juego
		checkCollisions();

		// Comprueba el estado de los enemigos
		for (Enemy enemy : levelManager.enemies) {
			// Si la cámara no los enfoca no se actualizan
			if (!camera.frustum.pointInFrustum(new Vector3(enemy.position.x, enemy.position.y, 0)))
				continue;
		
			if (enemy.isAlive)
				enemy.update(dt);
			else
				levelManager.enemies.removeValue(enemy, true);
		}

		for (Platform platform : levelManager.platforms)
			platform.update(dt);
		
		for (Item item : levelManager.items)
			if (item.isAlive)
				item.update(dt);
			else
				levelManager.items.removeValue(item, true);
	}

    public void draw() {

        // Fija la cámara para seguir al personaje en el centro de la pantalla y altura fija (eje y)
        camera.position.set(player.position.x + 18 / 2, 125, 0);

        // En los niveles de alturas el jugador puede mover la cámara hacia arriba y abajo
        if (levelManager.highLevel)
            camera.position.set(player.position.x + 18 / 2, 125 + CAMERA_OFFSET, 0);

        camera.zoom = 1 / 2f;
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render(new int[]{0, 1});

        // Inicia renderizado del juego
        batch.begin();
        // Pinta al jugador
        player.render(batch);
        for (Enemy enemy : levelManager.enemies)
            enemy.render(batch);
        for (Item item : levelManager.items)
            item.render(batch);
        // Pinta la información en partida relativa al jugador
        font.getData().setScale(0.7f);
        batch.draw(ResourceManager.getAtlas("items/items.pack").findRegion("coin"), camera.position.x - 60, camera.position.y - 135 - 12);
        font.draw(batch, " X " + levelManager.currentCoins, camera.position.x - 50, camera.position.y - 135);
        batch.draw(ResourceManager.getAtlas("items/items.pack").findRegion("life"), camera.position.x + 40, camera.position.y - 135 - 12);
        font.draw(batch, " X " + levelManager.currentLives, camera.position.x + 50, camera.position.y - 135);
        font.draw(batch, "level " + levelManager.currentLevel, camera.position.x + 100, camera.position.y - 135);
        // Pinta las plataformas móviles del nivel actual
        for (Platform platform : levelManager.platforms)
            platform.render(batch);

        batch.end();
    }
	
	/**
	 * Comprueba las colisiones del jugador con los elementos móviles del juego
	 * Enemigos e items
	 */
	private void checkCollisions() {
		Rectangle playerRect = new Rectangle();
		playerRect.set(player.position.x, player.position.y, Player.WIDTH, Player.HEIGHT);
		
		// Comprueba si el enemigo ha chocado contra algún enemigo
		for (Enemy enemy : levelManager.enemies) {
			Rectangle enemyRect = new Rectangle();
			enemyRect.set(enemy.position.x, enemy.position.y, Enemy.WIDTH, Enemy.HEIGHT);
			
			if (enemyRect.overlaps(playerRect)) {
				
				// Si el jugador está por encima elimina el enemigo
				if (player.position.y > (enemy.position.y + 5)) {
					ResourceManager.getSound("sounds/kick.wav").play();
					levelManager.enemies.removeValue(enemy, true);
					
					// El jugador rebota
					player.jump(false);
				}
				// Si está al mismo nivel o por debajo se pierde una vida
				else {
					player.velocity.x = player.velocity.y = 0;
                    player.die();
				}
			}
		}
		
		// Comprueba si el jugador recoge algún item de la pantalla
		for (Item item : levelManager.items) {
			Rectangle itemRect = new Rectangle();
			itemRect.set(item.position.x, item.position.y, Item.WIDTH, Item.HEIGHT);
			
			if (itemRect.overlaps(playerRect)) {
				ResourceManager.getSound("sounds/1up.wav").play();
				levelManager.items.removeValue(item, true);
				levelManager.currentLives++;
			}
		}
		
		boolean stuck = false;
		// Comprueba colisiones con las plataformas móviles de la pantalla
		for (Platform platform : levelManager.platforms) {
			Rectangle platformRectangle = new Rectangle(platform.position.x, platform.position.y, platform.width, platform.height);
				
			// Si colisiona con una se coloca encima y se "pega" a ella
			if (platformRectangle.overlaps(playerRect)) {
				
				// Si está cayendo y está por encima se coloca en la plataforma
				if ((player.velocity.y < 0) && (player.position.y > platformRectangle.y)) {
					player.position.y = platformRectangle.y + platformRectangle.height;
					player.canJump = true;
					player.isJumping = false;
					Player.stuckPlatform = platform;
					stuck = true;
				}
			}
		}
		// Si ya no está encima de ninguna plataforma, se "despega" de ellas
		if (!stuck)
			Player.stuckPlatform = null;
	}
	
	/**
	 * Controla la entrada de teclado del usuario
	 */
	private void handleInput() {
		
		// Se pulsa la teclad derecha
		if ((Gdx.input.isKeyPressed(Keys.RIGHT)) || (playerState == PlayerState.RIGHT)) {
			player.isRunning = true;
			Player.stuckPlatform = null;
			player.velocity.x = Player.WALKING_SPEED;
			player.state = Player.State.RUNNING_RIGHT;
			
			if ((!player.isJumping))
				player.isRunning = true;
		}
		// Se pulsa la tecla izquierda
		else if ((Gdx.input.isKeyPressed(Keys.LEFT)) || (playerState == PlayerState.LEFT)) {
			player.isRunning = true;
			Player.stuckPlatform = null;
			player.velocity.x = -Player.WALKING_SPEED;
			player.state = Player.State.RUNNING_LEFT;
			
			if ((!player.isJumping))
				player.isRunning = true;
		}
		// No se pulsa ninguna tecla
		else {
			
			if (player.isRunning)
				if (player.state == Player.State.RUNNING_LEFT)
					player.state = Player.State.IDLE_LEFT;
				else
					player.state = Player.State.IDLE_RIGHT;
			
			player.isRunning = false;
			player.velocity.x = 0;
		}
		
		// Se pulsa la tecla CONTROL IZQ (salto)
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {

			player.tryJump();
		}
		
		if ((Gdx.input.isKeyPressed(Keys.UP)) || (playerState == PlayerState.UP)) {
			CAMERA_OFFSET += 40f * Gdx.graphics.getDeltaTime();
		}
		if ((Gdx.input.isKeyPressed(Keys.DOWN)) || (playerState == PlayerState.DOWN)) {
			CAMERA_OFFSET -= 40f * Gdx.graphics.getDeltaTime();
		}
		
		// Controla los límites (por debajo) de la pantalla, cuando cae el personaje
		if (player.position.y < 0) {
			player.die();
		}
		
		// Controla el límite izquierdo de la pantalla
		if (player.position.x <= 0)
			player.position.x = 0;
	}

    public void resize(int width, int height) {

        camera.viewportHeight = height;
        camera.viewportWidth = width;
        camera.update();
    }

    /**
     * Libera los recursos utilizados por el controlador
     * Se invoca cuando se termina una partida y volvemos al
     * menú de juego
     */
    public void dispose() {

        music.stop();
        music.dispose();
        font.dispose();
        batch.dispose();

        levelManager.clearCharactersCurrentLevel();
    }

	@Override
	public void connected(Controller controller) {

	}

	@Override
	public void disconnected(Controller controller) {

	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {

    	switch (buttonCode) {
			case 0:
				// Nothing
				break;
			case 1:
				// Nothing
				break;
			case 2:
				player.tryJump();
				break;
			case 3:
				// Nothing
				break;
			default:
				break;
		}

		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {

        // Player moves around X axis
    	if (axisCode == 0) {
    	    // Player pushes to go right
			if (value == 1.0f) {
				playerState = PlayerState.RIGHT;
			}
			// Player pushes to go left
			else if (value == -1.0f) {
				playerState = PlayerState.LEFT;
			}
			// Player release axis control
			else {
				playerState = PlayerState.IDLE;
			}
		}
		// Player moves around Y axis
		else {
    	    // Player pushes to go up
			if (value == 1.0f) {
				playerState = PlayerState.DOWN;
			}
			// Player pushes to go down
			else if (value == -1.0f) {
				playerState = PlayerState.UP;
			}
			// Player release axis control
			else {
				playerState = PlayerState.IDLE;
			}
		}

		return false;
	}
}