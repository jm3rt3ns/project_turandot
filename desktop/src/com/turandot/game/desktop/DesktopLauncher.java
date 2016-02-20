package com.turandot.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.turandot.game.TurandotGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
	      config.title = "Mantablack";
	      config.width = 500;
	      config.height = 690;
	      new LwjglApplication(new TurandotGame(), config);
	}
}
