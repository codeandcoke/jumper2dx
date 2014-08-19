package org.sfaci.jumper2dx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.sfaci.jumper2dx.Jumper2DX;

/**
 * Menú principal del juego
 * @author Santiago Faci
 * @version Agosto 2014
 */
public class MainMenuScreen implements Screen {
	
	final Jumper2DX game;
    private Stage stage;
	
	public MainMenuScreen(Jumper2DX game) {
		this.game = game;
	}

    @Override
    public void show() {

        stage = new Stage();

        Table table = new Table(game.getSkin());
        table.setFillParent(true);
        table.center();

        Label title = new Label("JUMPER2DX\nMAIN MENU", game.getSkin());
        title.setFontScale(2.5f);

        TextButton playButton = new TextButton("PLAY GAME", game.getSkin());
        playButton.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                game.setScreen(new GameScreen(game));
            }
        });
        TextButton optionsButton = new TextButton("OPTIONS", game.getSkin());
        optionsButton.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                // FIXME ¿Menú de opciones?

            }
        });
        TextButton exitButton = new TextButton("QUIT GAME", game.getSkin());
        exitButton.addListener(new ClickListener() {
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                System.exit(0);
            }
        });

        Label aboutLabel = new Label("jumper2dx v0.2\n(c) Santiago Faci\nhttp://bitbucket.org/sfaci/jumper2dx", game.getSkin());
        aboutLabel.setFontScale(1f);

        table.row().height(200);
        table.add(title).center().pad(35f);
        table.row().height(75);
        table.add(playButton).center().width(500).pad(5f);
        table.row().height(75);
        table.add(optionsButton).center().width(500).pad(5f);
        table.row().height(75);
        table.add(exitButton).center().width(500).pad(5f);
        table.row().height(75);
        table.add(aboutLabel).center().pad(55f);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		stage.act();
        stage.draw();
	}

	@Override
	public void resize(int width, int height) {
        stage.setViewport(width, height);
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}