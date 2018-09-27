package minigame;

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import minigame.Enemy.Archer;
import minigame.Enemy.Mover;

public class Stage extends JPanel
{
	JFrame frame;
	static final int PANEL_OFFSET = 16;
	static final int gameScrX = 480;
	static final int gameScrY = 640;
	static final int PANEL_W = gameScrX + PANEL_OFFSET;
	static final int PANEL_H = gameScrY + PANEL_OFFSET;

	public static Stage stage;
	public static Console console = new Console();

	static final int ConwayExtend = 16;

	int ext1 = 2, ext2 = 3, ext3 = 4;

	static final int timeDefault = 70 * 60;
	static final int bonusDefault = 15;

	int time = timeDefault;
	int bonus = bonusDefault;
	int forceCnt = 0;

	public Conway conway = new Conway(gameScrX + ConwayExtend * ext1, gameScrY + ConwayExtend * ext1,
			ConwayExtend / 2 * ext1);
	public Conway conway2 = new Conway(gameScrX + ConwayExtend * ext2, gameScrY + ConwayExtend * ext2,
			ConwayExtend / 2 * ext2);
	public Conway conway3 = new Conway(gameScrX + ConwayExtend * ext3, gameScrY + ConwayExtend * ext3,
			ConwayExtend / 2 * ext3);
	public Conway conway4 = new Conway(gameScrX + ConwayExtend * ext1, gameScrY + ConwayExtend * ext1,
			ConwayExtend / 2 * ext1);

	public AudioClip audio;

	// 解除はGameControllerのSpaceキーイベント
	boolean isGame = false;

	// TODO
	void resetGame()
	{
		isGame = true;
		time = timeDefault;
		bonus = bonusDefault;
		forceCnt = 0;
		GameController.stGC.playerCharacter.Init();
		ene = new ConvoyGenerator();
		resetDaPhase(0);
		phase0();
		GameController.stGC.score = 0;
		
		Actor.enemyClear();
		
		System.gc();
	}

	public void toGame()
	{
		pFCnt = 0;
		audio.loop(); // 音楽
		resetGame();
	}

	static final int stageBeginInterval = 100; // ステージ開始の待機時間

	public Stage()
	{
		setDoubleBuffered(true);

		frame = new JFrame("tamazusa2017 summer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(360, 80, 0, 0);
		frame.pack();
		Insets is = frame.getInsets();
		frame.setSize(gameScrX + is.left, gameScrY + is.top);
		frame.setResizable(false);
		frame.getContentPane().add(this);
		Stage.stage = this;

		resetDaPhase(0);

		conway.cR = 64;
		conway.cG = 32;
		conway.cB = 48;

		conway2.cR = 32;
		conway2.cG = 48;
		conway2.cB = 64;

		conway4.cR = 48;
		conway4.cG = 64;
		conway4.cB = 32;

		conway3.cR = 48;
		conway3.cG = 48;
		conway3.cB = 48;

		conway4.ExceMutationParam = 120;
		conway.ExceMutationParam = 70;
		conway2.ExceMutationParam = 40;
		conway3.ExceMutationParam = 20;

		conway4.MutationParam = 80;
		conway.MutationParam = 120;
		conway2.MutationParam = 60;
		conway3.MutationParam = 30;

		/*
		for (int i = 0; i < 3; ++i)
		{
			conway.run();
			conway2.run();
			conway3.run();
			conway4.run();
		}
		*/
	}

	public void paintComponent(Graphics g)
	{
		Graphics2D argg = (Graphics2D) (g);
		//
		super.paintComponent(argg);
		Actor.Draw(argg, this);

		Toolkit.getDefaultToolkit().sync();
	}

	// // 未使用
	// class runConway implements Runnable
	// {
	// Conway c;
	//
	// public void run()
	// {
	// c.run();
	// }
	// }

	void drawUI()
	{
		Graphics2D g = Actor.canvas.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setFont(new Font("Yu Gothic", Font.BOLD, 16));
		g.setColor(Color.WHITE);

		//
		Actor pl = GameController.stGC.playerCharacter;

		g.drawImage(Actor.picList.get("player"), 8, -8, this);
		g.drawString("player: " + (pl.HP >= 0 ? pl.HP : "gameover"), 8, 60);

		//
		g.setFont(new Font("Yu Gothic", Font.PLAIN, 24));
		String Score = "score:" + String.format("%010d" + "0", GameController.stGC.score);
		g.drawString(Score, 270, 24);

		g.setFont(new Font("Yu Gothic", Font.PLAIN, 30));
		String Time = String.format("%02.02fs", time / 60.0);
		g.drawString(Time, 365, 58);

		if (!isGame)
		{
			g.setFont(new Font("Yu Gothic", Font.PLAIN, 24));
			g.drawString("press Space...", 160, PANEL_H / 2);
		}

		if (pl.HP < 0)
		{
			g.setFont(new Font("Yu Gothic", Font.PLAIN, 24));
			if (time > 0)
			{
				g.drawString("-game over-", 160, PANEL_H / 2 - 32);
				g.drawString("press Space...", 160, PANEL_H / 2);
			}
			else
			{
				g.drawString("-time over-", 160, PANEL_H / 2 - 32);
				g.drawString("press Space...", 160, PANEL_H / 2);
			}
		}

		console.draw(g);
	}

	static final int conwayMod = 5;

	void drawBack()
	{
		Graphics2D tg = Actor.canvas.createGraphics();

		tg.setColor(Color.BLACK);
		tg.clearRect(0, 0, Stage.PANEL_W, Stage.PANEL_H);

		/*
		conway4.draw();
		conway3.draw();
		conway2.draw();
		conway.draw();
		 */
		
		if (!isGame && pFCnt / conwayMod == 1)
		{
			pFCnt = 0;
		}

		/*
		if (pFCnt % 4 == 0)
		{
			conway4.addView(1);
		}
		if (pFCnt % 3 == 0)
		{
			conway3.addView(1);
		}
		if (pFCnt % 2 == 0)
		{
			conway2.addView(1);
		}
		if (pFCnt % 1 == 0)
		{
			conway.addView(1);
		}
		*/
	}

	int phase = 0;
	int pWaveCnt; // 周回したWaveカウンタ
	int pConvCnt; // 隊列カウンタ
	int pConvNum; // 隊列パターン数
	int pFCnt; // このクラス全体で使うフレームカウンタ

	void resetDaPhase(int next)
	{
		phase = next;
		pWaveCnt = pConvCnt = pConvNum = 0;
		pFCnt = 0;

		//
		switch (phase)
		{
		case 0:
			phase0();
		}

		phaseSettings(phase);
	}

	// phase ごとの設定、resetDaPhaseから呼ばれる
	void phaseSettings(int p)
	{
		switch (p)
		{
		case 0:
			pConvNum = 2;
			break;
		}
	}

	boolean checkConvoyDestroy()
	{
		for (Enemy e : ene.ene)
		{
			if (!e.isDead)
			{
				return false;
			}
		}
		return true;
	}

	// 敵作ったり
	void frameMain()
	{
		/*
		if (pFCnt % conwayMod == 0)
		{
			conway4.run();
			conway3.run();
			conway2.run();
			conway.run();
		}
		*/
		//
		if (!isGame)
		{
			return;
		}
		if (!(pFCnt > stageBeginInterval))
		{
			return;
		}
		if (ene.endProc())
		{
			// phase終了条件処理
			// 未実装というかどうするかも決めていない

			// 隊列をすべて破壊したかどうか、その時の動作
			if (checkConvoyDestroy())
			{
				// TODO
				GameController.stGC.score += ene.Score * (10 + pWaveCnt) * ene.loopMax;
				console.addMes(String.format("bonus:%d [%d] = %d", ene.Score * ene.loopMax * 100, pWaveCnt * 10,
						ene.Score * ene.loopMax * (100 + pWaveCnt * 10)));
				++forceCnt;
				// if (forceCnt % 2 == 0 && bonus < 60)
				if (bonus < 60)
				{
					bonus = (bonus + 1) % 60;
				}
				time += bonus;
				console.addMes(String.format("extend time:%.02fs", bonus / 60.0));
			}

			ene.ene.clear();

			// 通常処理(phaseが終了していない場合)
			pConvCnt = (pConvCnt + 1) % pConvNum;

			// phaseが最初からになるとき
			if (pConvCnt == 0 && GameController.stGC.playerCharacter.HP >= 0)
			{
				int bonus = 3 * 60 + this.bonus;
				++pWaveCnt;
				time += bonus;
				console.addMes(String.format("phase completion bonus:%.02fs", bonus / 60.0));
			}

			switch (phase)
			{
			case 0:
				phase0();
			}
		}

		ene.func();
	}

	public void frame()
	{
		switch (pFCnt)
		{
		case 12:
			console.addMes("---start flyugel[2310]---");
			break;
		case 28:
			console.addMes("f[2310]:sys.setup(\"minimal\") exit status(0x0000)");
			break;
		case 35:
			console.addMes("f[2310]:sys.setup(\"full\") exit status(0x0000)");
			break;
		case 42:
			console.addMes("connecting Server...");
			break;
		case 47:
			console.addMes("Observation system initialized.");
			break;
		case 65:
			console.addMes("connected Server. Delay 0.1-8.4s. average 3.7s.");
			break;
		}
		if (isGame && time > 0 && GameController.stGC.playerCharacter.HP >= 0)
		{
			if (pFCnt > stageBeginInterval)
			{
				--time;
			}

			if (time == 0)
			{
				GameController.stGC.playerCharacter.HP = -1;
				GameController.stGC.playerCharacter.geneEffect();
				GameController.stGC.waitRestart = true;
				console.addMes("time over...");
			}
		}
		
		if (GameController.stGC.playerCharacter.HP < 0)
		{
			if (!GameController.stGC.waitStart)
			{
				GameController.stGC.waitRestart = true;
			}
		}
		
		++pFCnt;
		drawBack();

		if (time >= 0)
		{
			frameMain();

			if (isGame)
			{
				Actor.Frame();
			}
		}

		drawUI();

		Actor.canvas.flush();
		Actor.SwapCanvas();
	}

	// ステージ系ツール
	class ConvoyGenerator
	{
		// 生成パラメーター
		private int Cnt = 0/* framecnt */, loopCnt = 0/* done generate */,
				loopMax = 1/* generate num */;
		private int loopFrame = 10;/* generate wait time */
		private int layer = GameParameter.layerEnemy;
		private Mover m = new Mover(0, 0);
		private Archer a = new Archer();
		private String picKey = "ene1";

		int HP = 1, Score = 1;

		//
		ArrayList<Enemy> ene = new ArrayList<Enemy>(12);

		void setHP(int argHP)
		{
			HP = argHP;
		}

		void setScore(int argScore)
		{
			Score = argScore;
		}

		// 設定系
		void setEnemyLayer()
		{
			layer = GameParameter.layerEnemy;
		}

		void setLargeEnemyLayer()
		{
			layer = GameParameter.layerEnemyL;
		}

		void setloop(int argLoopMax, int argLoopFrame)
		{
			Cnt = 0;
			loopCnt = 0;
			loopMax = argLoopMax;
			loopFrame = argLoopFrame;
		}

		void setPicture(String arg)
		{
			picKey = arg;
		}

		void setMover(Mover argM)
		{
			m = argM;
		}

		void setArcher(Archer argA)
		{
			a = argA;
		}

		// この関数を呼ぶとカウント。
		void func()
		{
			if (loopCnt >= loopMax)
			{
				return;
			}

			++Cnt;

			if ((Cnt % loopFrame) == 0)
			{
				Enemy e = new Enemy(true, picKey, m.clone(), a.clone());
				e.HP = HP;

				ene.add(e);
				++loopCnt;
			}
		}

		boolean endProc()
		{
			if (loopCnt >= loopMax && Actor.getActorCntByLayer(layer) == 0)
			{
				return true;
			}

			return false;
		}

		// TODO
		void addScore()
		{

			GameController.stGC.score += Score * (10 + pWaveCnt);
			console.addMes(String.format("%d [%d] = %d", Score * 100, pWaveCnt * 10, Score * (100 + pWaveCnt * 10)));
		}
	}

	// 隊列更新関数
	// ---------------------------------------------------------------------------
	ConvoyGenerator ene = new ConvoyGenerator();

	int getPhaseWaveNum()
	{
		switch (phase)
		{
		case 0:
			return 0;
		}
		return 1;
	}

	void phase0()
	{
		Archer ta = null;

		pConvNum = 17;

		switch (pConvCnt)
		{
		case 0:
			ene.setloop(8, 10);
			ene.setScore(2);
			ene.setPicture("ene2");
			ene.setHP(5);
			ene.setMover(new Enemy.MLinear(0, 0, 5, 4));
			ene.setArcher(Archer.nullArcher);
			break;

		case 1:
			ene.setloop(8, 10);
			ene.setMover(new Enemy.MLinear(gameScrX, 0, -5, 4));
			break;

		case 2:
			ene.setloop(8, 10);
			ene.setScore(3);
			ene.setPicture("ene3");
			ene.setMover(new Enemy.MLinear(0, 160, 5, -2));
			ene.setArcher(new Enemy.Yabusame(1, 60, 3, 7, 0.6, Math.toRadians(40), "eb2"));
			break;

		case 3:
			ene.setloop(8, 10);
			ene.setMover(new Enemy.MLinear(gameScrX, 160, -5, -2));
			break;

		case 4:
			ene.setloop(6, 8);
			ene.setScore(3);
			ene.setHP(10);
			ene.setPicture("ene2");
			ene.setMover(new Enemy.MLinear(60, 0, 0, 5));
			ene.setArcher(Archer.nullArcher);
			break;

		case 5:
			ene.setloop(6, 8);
			ene.setMover(new Enemy.MLinear(gameScrX - 60, 0, 0, 5));
			break;

		case 6:
			ene.setHP(30);
			ene.setScore(4);
			ene.setloop(6, 10);
			ene.setPicture("ene1");
			ene.setMover(new Enemy.Row(30, 120, 60, 0, 66, 0, 60, 250, 66, 0, 0, -5, 0, 0));
			ene.setArcher(Archer.nullArcher);
			break;

		case 7:
			ene.setloop(6, 10);
			ene.setMover(new Enemy.Row(30, 120, gameScrX - 60, 0, -66, 0, gameScrX - 60, 250, -66, 0, 0, -5, 0, 0));
			break;

		case 8:
			ene.setHP(7);
			ene.setloop(8, 8);
			ene.setScore(3);
			ene.setPicture("ene3");
			ene.setMover(new Enemy.MLinear(60, gameScrY, 0, -7));
			ene.setArcher(new Enemy.YabusameRand(18, 30, 1, 6, 2, Math.toRadians(360), "eb3"));
			break;

		case 9:
			ene.setloop(8, 8);
			ene.setMover(new Enemy.MLinear(gameScrX - 60, gameScrY, 0, -7));
			break;

		case 10:
			ene.setloop(6, 16);
			ene.setPicture("ene4");
			ene.setHP(40);
			ene.setMover(new Enemy.Row(45, 80, gameScrX - 60, gameScrY, -70, 0, 60, 250, 70, 0, 0, -5, 0, 0));
			ene.setArcher(ta = new Enemy.YabusameRandOnce(15, 1, 1, 6, 0.75, Math.toRadians(360), "eb1"));
			ene.setScore(5);
			ta.freez = 45;
			break;

		case 11:
			ene.setloop(6, 16);
			ene.setMover(new Enemy.Row(45, 80, 60, gameScrY, 70, 0, gameScrX - 60, 250, -70, 0, 0, -5, 0, 0));
			break;

		case 12:
			ene.setloop(1, 1);
			ene.setMover(new Enemy.Row(45, 360, gameScrX / 2, gameScrY, gameScrX / 2, 200, 0, -2.5));
			ene.setArcher(ta = new Enemy.Gilgames());
			ta.freez = 15;
			ene.setHP(100);
			ene.setScore(2);
			break;

		case 13:
			ene.setHP(7);
			ene.setloop(10, 10);
			ene.setScore(3);
			ene.setPicture("ene1");
			ene.setMover(new Enemy.MLinear(0, gameScrY - 90, 2.7, -8.4));
			ene.setArcher(ta = new Enemy.YabusameRand(8, 35, 1, 6, 2, Math.toRadians(360), "eb2"));
			ta.freez = 22;
			break;

		case 14:
			ene.setloop(8, 8);
			ene.setMover(new Enemy.MLinear(gameScrX - 0, gameScrY - 90, -2.7, -8));
			break;

		case 15:
			ene.setHP(10);
			ene.setloop(10, 12);
			ene.setMover(new Enemy.Skyserpent(gameScrX - 150, 0, 1.5, 8, 7, 0, 12));
			ene.setArcher(ta = new Enemy.YabusameOnce(16, 1, 5, 6, 0.5, Math.toRadians(360), "eb1"));
			ta.freez = 9999;
			ene.setPicture("ene2");
			ene.setScore(4);
			break;

		case 16:
			ene.setloop(10, 12);
			ene.setMover(new Enemy.Skyserpent(150, 0, -1.5, 8, 7, 0, 12));
			break;
		}
	}
}
