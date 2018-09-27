package minigame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameController implements KeyListener// , Runnable
{
	public Player playerCharacter; // プレイヤーキャラクター用変数
	static GameController stGC;

	public int score;
	
	GameController gi()
	{
		return stGC;
	}

	private boolean key[], keybef[], keypress[];

	public void InitKey()
	{
		for (int i = 0; i < GameKeyCode.dummy.getInt(); ++i)
		{
			key[i] = false;
			keybef[i] = false;
			keypress[i] = false;
		}
	}
	
	public GameController()
	{
		stGC = this;

		key = new boolean[GameKeyCode.dummy.getInt()];
		keybef = new boolean[GameKeyCode.dummy.getInt()];
		keypress = new boolean[GameKeyCode.dummy.getInt()];

		InitKey();

		// new Stage(); // ステージの生成
		Stage.stage.frame.addKeyListener(this); // キーリスナーとしてフレームに追加
		Stage.stage.frame.setVisible(true);
		
		playerCharacter = new Player(0, 0);
		// (new Thread(this)).start(); // ゲーム開始
	}

	public void run()
	{
		Stage.stage.frame.setVisible(true);

		// フレームリミッタ
		long before = System.currentTimeMillis();

		while (!gameIsOver())
		{
			// 一定時間待ち
			try
			{
				long value = before + 15 - System.currentTimeMillis();

				if (value > 0)
				{
					Thread.sleep(value);
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			// フレームのメイン
			KeyProc();
			Stage.stage.frame();
			before = System.currentTimeMillis();
			Stage.stage.repaint();
		}

		System.out.println("game end");
	}

	static public Actor existsAt(int x, int y)
	{
		/*
		 * synchronized (Actor.actors) { for (Actor actor : Actor.actors) { if
		 * ((actor.posX == x) && (actor.posY == y)) { return actor; // もとはreturn
		 * true; } } return null; // もとはreturn false; }
		 */
		return null;
	}

	public void keyTyped(KeyEvent e)
	{
	}

	// 引用 url::http://qiita.com/hkusu/items/0996735553580bfabbdb
	public enum GameKeyCode
	{
		up(0), down(1), left(2), right(3), b1(4), b2(5), b3(6),

		dummy(7), // これが最後尾になるようにすること。int値が要素数になるように。
		;

		private final int id;

		private GameKeyCode(final int id)
		{
			this.id = id;
		}

		public int getInt()
		{
			return this.id;
		}
	}

	public void keyReleased(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_DOWN:
			key[GameKeyCode.down.getInt()] = false;
			break;
		case KeyEvent.VK_UP:
			key[GameKeyCode.up.getInt()] = false;
			break;
		case KeyEvent.VK_LEFT:
			key[GameKeyCode.left.getInt()] = false;
			break;
		case KeyEvent.VK_RIGHT:
			key[GameKeyCode.right.getInt()] = false;
			break;
		case KeyEvent.VK_Z:
			key[GameKeyCode.b1.getInt()] = false;
			break;
		case KeyEvent.VK_X:
			key[GameKeyCode.b2.getInt()] = false;
			break;
		case KeyEvent.VK_C:
			key[GameKeyCode.b3.getInt()] = false;
			break;
		}
	}

	boolean waitStart = true;
	public boolean waitRestart = false;
	
	public void keyPressed(KeyEvent arg0)
	{
		// int x = 0, y = 0;
		switch (arg0.getKeyCode())
		{
		case KeyEvent.VK_DOWN:
			key[GameKeyCode.down.getInt()] = true;
			break;
		case KeyEvent.VK_UP:
			key[GameKeyCode.up.getInt()] = true;
			break;
		case KeyEvent.VK_LEFT:
			key[GameKeyCode.left.getInt()] = true;
			break;
		case KeyEvent.VK_RIGHT:
			key[GameKeyCode.right.getInt()] = true;
			break;
		case KeyEvent.VK_Z:
			key[GameKeyCode.b1.getInt()] = true;
			break;
		case KeyEvent.VK_X:
			key[GameKeyCode.b2.getInt()] = true;
			break;
		case KeyEvent.VK_C:
			key[GameKeyCode.b3.getInt()] = true;
			break;
			
		case KeyEvent.VK_SPACE:
			if (waitStart)
			{
				Stage.stage.toGame();
				waitStart = false;
				break;
			}
			if (waitRestart)
			{
				Stage.stage.resetGame();
				waitRestart = false;
				break;
			}
			break;
		}
		synchronized (this)
		{
			this.notify(); // プレイヤーが移動するたびにゲームの終了条件をチェック
		}
	}

	public void KeyProc()
	{
		for (int i = 0; i < GameKeyCode.dummy.getInt(); ++i)
		{
			keypress[i] = false;
		}

		for (int i = 0; i < GameKeyCode.dummy.getInt(); ++i)
		{
			if (key[i])
			{
				if (!keybef[i])
				{
					keypress[i] = true;
				}

				keybef[i] = true;
			}
			else
			{
				keybef[i] = false;
			}
		}
	}

	public boolean getKeyPress(GameKeyCode arg)
	{
		return keypress[arg.getInt()];
	}

	public boolean getKey(GameKeyCode arg)
	{
		return key[arg.getInt()];
	}

	private boolean gameOver = false;
	
	public void setGameOver()
	{
		gameOver = true;
	}
	
	public boolean gameIsOver()
	{
		return gameOver;
	}
}