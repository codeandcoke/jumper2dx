package com.codeandcoke.jumper2dx.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.codeandcoke.jumper2dx.Jumper2DX;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
		configuration.title = "Jumper2DX";
		configuration.width = 1024;
		configuration.height = 600;
		configuration.fullscreen = false;


		new LwjglApplication(new Jumper2DX(), configuration);
	}
}
