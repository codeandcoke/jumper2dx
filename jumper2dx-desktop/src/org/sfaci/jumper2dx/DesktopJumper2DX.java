package org.sfaci.jumper2dx;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopJumper2DX {

	public static void main(String[] args) {
		LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
		configuration.title = "Jumper2DX";
		configuration.width = 1024;
		configuration.height = 600;
				
		new LwjglApplication(new Jumper2DX(), configuration);
	}
}

