package minigame;

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Random;

import minigame.GameController.GameKeyCode;

public class Player extends Actor
{
	int cntDisHit = 0;  // 無敵
	
	public Player(double posX, double posY)
	{
		super(posX, posY, GameParameter.layerPlayer);

		s1bef = 0;
		// TODO 自動生成されたコンストラクター・スタブ

		Init();
	}

	final int operaFreezeTime = 60; 
	int plCanOperation = operaFreezeTime;   // 0:操作可能
	
	void SortieProc()
	{
		plCanOperation = operaFreezeTime;
		cntDisHit = operaFreezeTime * 3;
	}
	
	final double defX = Stage.PANEL_W / 2;
	final double defY = 1000;
	final double sortieY = 480;
	
	void Init()
	{
		posX = defX;
		posY = defY;
		changePA(plAtkType.none);
		
		HP = 3;
		
		SortieProc();
	}

	int s1bef = 0;

	static Random r = new Random(System.currentTimeMillis());

	enum plAtkType
	{
		none, aShot1, bShot1, cSword,
		// ab1A,
		// ab1B,
	}

	plAtkType pa = plAtkType.none;
	int paCnt = 0;

	// コマンド別のパラメータがある場合ここ
	double cAng;

	//
	enum cmdCState
	{
		none, a, b,
	};

	cmdCState ccs;

	void changePA(plAtkType argNext)
	{
		pa = argNext;
		paCnt = -1;

		// 状態事に特殊な処理がある場合ここに書く
		switch (argNext)
		{
		case cSword:
			cAng = Math.toRadians(-90);
			break;
		default:
		}
	}

	// 指定キーが押されていないときにnoneに
	void noPressToNoneState(GameKeyCode arg)
	{
		if (!Game.getgc().getKey(arg))
		{
			changePA(plAtkType.none);

			//
			if (Game.getgc().getKey(GameKeyCode.b1))
			{
				changePA(plAtkType.aShot1);
			}
			if (Game.getgc().getKey(GameKeyCode.b2))
			{
				changePA(plAtkType.bShot1);
			}
		}
	}

	void noPressToNoneState(GameKeyCode arg, GameKeyCode arg2)
	{
		if (!Game.getgc().getKey(arg) && !Game.getgc().getKey(arg2))
		{
			changePA(plAtkType.none);
		}
	}

	void pressToChangeState(GameKeyCode arg, plAtkType next)
	{
		if (Game.getgc().getKeyPress(arg))
		{
			changePA(next);
		}
	}

	void pressToChangeState(GameKeyCode arg, GameKeyCode arg2, plAtkType next)
	{
		if (Game.getgc().getKeyPress(arg))
		{
			changePA(next);
		}
	}

	// ボタンが押された時の遷移先
	plAtkType aCmd = plAtkType.aShot1, bCmd = plAtkType.bShot1, cCmd = plAtkType.cSword;

	static final int damageA = 4;
	static final int damageB = 2;
	static final int damageC = 2;
	
	void frameAttack()
	{
		if (pa != plAtkType.cSword)
		{
			pressToChangeState(GameKeyCode.b1, aCmd);
			pressToChangeState(GameKeyCode.b2, bCmd);
		}
		pressToChangeState(GameKeyCode.b3, cCmd);

		switch (pa)
		{
		case none:
			break;

		// a: 12 * 3  - 4(136)
		// b: 10 * 4  - 2(80)
		// c: 60      - 2(120)
			
		case aShot1:
			final int as1Interval = 5;
			if ((paCnt % as1Interval) == 0)
			{
				final int n = 2;

				for (int i = 0; i <= n; ++i)
				{
					final double angInterval = 10;

					OShotA1 tmp = new OShotA1((int) posX, (int) posY - 8);
					double ang = -90 + angInterval * (-n / 2.0 + i);
					tmp.x = Math.cos(Math.toRadians(ang)) * 10;
					tmp.y = Math.sin(Math.toRadians(ang)) * 15;
				}
			}

			noPressToNoneState(GameKeyCode.b1);
			break;
		case bShot1:
			final int bs1Interval = 6;
			if ((paCnt % bs1Interval) == 0)
			{
				//new OShotB1((int) posX, (int) posY);
				new OShotB1((int) posX, (int) posY);
				new OShotB1((int) posX, (int) posY);
			}

			noPressToNoneState(GameKeyCode.b2);
			break;

		case cSword:
			DrawSword(1, (int) posX, (int) posY);
			new OShotC(posX, posY, cAng);
			noPressToNoneState(GameKeyCode.b3);
			break;

		default:

			break;
		}

		++paCnt;
	}

	void DrawSword(int sID, int x, int y)
	{
		BufferedImage img = picList.get("sword1");

		at.setToRotation(cAng, img.getWidth() / 2, img.getHeight() / 2);

		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		BufferedImage bufferedImage;
		try
		{
			final double r = 16;
			bufferedImage = op.filter(img, null);
			canvas.createGraphics().drawImage(bufferedImage, 
					(int)(x - img.getWidth() / 2 + r * Math.cos(cAng)), 
					(int)(y - img.getHeight() / 2 + r * Math.sin(cAng)),
					Stage.stage);
		}
		catch (Exception e)
		{
		}
	}

	cmdCState ccsOptA = cmdCState.b, ccsOptB = cmdCState.a, ccsOptC = cmdCState.none;

	void rotSword(double radTargetAng)
	{
		final double subAng = radTargetAng - cAng;
		final double angAbs = (subAng + Math.toRadians(360)) % Math.toRadians(360);
		double maxAng = Math.toRadians(12);

		if (angAbs > maxAng)
		{
			cAng += maxAng * (angAbs < Math.toRadians(180) ? 1 : -1);
		}
		else
		{
			cAng = radTargetAng;
		}
	}

	public void geneEffect()
	{
		Effect.Shape.auto2(posX, posY, 64, 3);
	}
	
	// 被弾処理呼び出し
	public void callHitProc()
	{
		//SO.println(cntDisHit);
		// 被弾無効状態
		if (cntDisHit != 0)
		{
			return;
		}
		
		// 被弾本処理
		--HP;
		SortieProc();
		// pa = plAtkType.none;
		Stage.console.addMes("Hit!!!");
		Stage.console.addMes("flyugel[2310]:system trying restore...");
		
		geneEffect();
		
		if (HP < 0)
		{
			plCanOperation = 200;
		}
	}
	
	boolean ObjFrameMain()
	{
		if (HP < 0)
		{
			posX = defX; posY = -200;
			
			--plCanOperation;
			
			if (plCanOperation == 5)
			{
				Stage.console.addMes("flyugel[2310]:exception(recover failed).");
			}
			
			if (plCanOperation <= 0)
			{
				
				// GameController.stGC.setGameOver();
			}
			
			return false;
		}
		
		if (plCanOperation > 0)
		{
			--plCanOperation;
			
			if (plCanOperation == 5)
			{
				Stage.console.addMes("flyugel[2310]:system ready.");
			}
			
			posX = defX;
			posY = sortieY + GameParameter.ScaleSine(defY - sortieY, plCanOperation, operaFreezeTime);
			return false;
		}
		
		if (cntDisHit > 0)
		{
			--cntDisHit;
		}
		
		final int speed = 7 - ((Game.getgc().getKey(GameKeyCode.up) || Game.getgc().getKey(GameKeyCode.down))
				&& (Game.getgc().getKey(GameKeyCode.left) || Game.getgc().getKey(GameKeyCode.right)) ? 1 : 0);

		// none:無 a:先行 b:追尾 (関数外の変数でオプションとして外部で変更できるが移動時は前記に従え)
		if (!(Game.getgc().getKey(GameKeyCode.b1) && Game.getgc().getKey(GameKeyCode.b2)))
		{
			ccs = ccsOptC;
		}
		if (Game.getgc().getKey(GameKeyCode.b1) && !Game.getgc().getKey(GameKeyCode.b2))
		{
			ccs = ccsOptA;
		}
		if (Game.getgc().getKey(GameKeyCode.b2) && !Game.getgc().getKey(GameKeyCode.b1))
		{
			ccs = ccsOptB;
		}
		if (Game.getgc().getKeyPress(GameKeyCode.b1))
		{
			ccs = ccsOptA;
		}
		if (Game.getgc().getKeyPress(GameKeyCode.b2))
		{
			ccs = ccsOptB;
		}

		// 剣回しはここ
		if (ccs != ccsOptB)    // 回したくないコマンド
		{
			int sign = ccs == ccsOptC ? 0:180;    // 追尾させたいコマンド
			
			if (Game.getgc().getKey(GameKeyCode.up))
			{
				if (Game.getgc().getKey(GameKeyCode.left))
				{
					rotSword(Math.toRadians(45 + sign));
				}
				else
				if (Game.getgc().getKey(GameKeyCode.right))
				{
					rotSword(Math.toRadians(135 + sign));
				}
				else
				{
					rotSword(Math.toRadians(90 + sign));
				}
			}
			else if (Game.getgc().getKey(GameKeyCode.down))
			{
				if (Game.getgc().getKey(GameKeyCode.left))
				{
					rotSword(Math.toRadians(-45 + sign));
				}
				else
				if (Game.getgc().getKey(GameKeyCode.right))
				{
					rotSword(Math.toRadians(-135 + sign));
				}
				else
				{
					rotSword(Math.toRadians(-90 + sign));
				}
			}
			else
			{
				if (Game.getgc().getKey(GameKeyCode.left))
				{
					rotSword(Math.toRadians(0 + sign));
				}
				else if (Game.getgc().getKey(GameKeyCode.right))
				{
					rotSword(Math.toRadians(180 + sign));
				}
			}
		}
		// 移動
		if (Game.getgc().getKey(GameKeyCode.up))
		{
			posY -= speed;

			if (posY < 32)
			{
				posY = 32;
			}
		}
		if (Game.getgc().getKey(GameKeyCode.down))
		{
			posY += speed;

			if (posY > Stage.gameScrY - getHitSize())
			{
				posY = Stage.gameScrY - getHitSize();
			}
		}
		if (Game.getgc().getKey(GameKeyCode.left))
		{
			posX -= speed;

			if (posX < getHitSize())
			{
				posX = getHitSize();
			}
		}
		if (Game.getgc().getKey(GameKeyCode.right))
		{
			posX += speed;

			if (posX > Stage.gameScrX - getHitSize())
			{
				posX = Stage.gameScrX - getHitSize();
			}
		}
		frameAttack();
		return false;
	}
}
