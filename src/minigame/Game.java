package minigame;

import java.applet.Applet;

public class Game
{
	static final int TILESIZE = 32;
	static final int NUMTILE_X = 17;
	static final int NUMTILE_Y = 22;

	static GameController gc;
	
	public static GameController getgc()
	{
		return gc;
	}
	
	public static void main(String[] args)
	{
		// Random rnd = new Random(System.currentTimeMillis());
		
		// java2dの有効化(d3dはMacで実行するとどうなるかわからないのでopenglだけでいいかも)
		System.setProperty("sun.java2d.opengl", "true");
		System.setProperty("sun.java2d.d3d", "true");
		new Stage();
		Stage.stage.audio = Applet.newAudioClip(Stage.stage.getClass().getClassLoader().getResource("dat/bgm01.wav"));
		Actor.Settings();
		
		gc = new GameController();
		gc.run();
	}

}
