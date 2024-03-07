package com.codeandcoke.jumper2dx.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.codeandcoke.jumper2dx.Jumper2DX;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Jumper2DX");
		config.setWindowSizeLimits(1024, 768, 1024, 768);


		new Lwjgl3Application(new Jumper2DX(), config);
	}
}
