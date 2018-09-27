package minigame;

import java.util.Random;

// 敵、敵弾共通。
public class Enemy extends Actor
{
	String picKey;
	Mover move;
	Archer Atalant;
	boolean isEne, isDead = false;
	
	void setHP(int arg)
	{
		HP = arg;
	}
	
	boolean checkOutOfScreen()
	{
		final double oueExt = isEne ? 3 : 1.5;
		
		if (posY < -getHitSize() * oueExt)
		{
			return true;
		}

		if (posX < -getHitSize() * oueExt)
		{
			return true;
		}
		if (posX > 480 + getHitSize() * oueExt)
		{
			return true;
		}
		if (posY > 640 + getHitSize() * oueExt)
		{
			return true;
		}

		return false;
	}
	
	public Enemy(boolean isEnemy, String key, Mover m, Archer a)
	{
		super(m.x, m.y, isEnemy ? GameParameter.layerEnemy : GameParameter.layerEnemy2);
		move = m;
		Atalant = a;
		isEne = isEnemy;
		
		m.inConstructor(this, key);
		
		deadHP = true;
		
		if (isEnemy)
		{
			
		}
		else
		{
			for (int i = 0; i < 6; ++i)
			{
				//ObjFrameMain();
			}
		}
		
		picKey = key;
		
		if (key.equals(""))
		{
			
		}
	}
	
	// 強制的にレイヤーを上書きする
	public Enemy(boolean isEnemy, String key, Mover m, Archer a, int layer)
	{
		super(m.x, m.y, layer);
		move = m;
		Atalant = a;
		isEne = isEnemy;
		
		m.inConstructor(this, key);
		
		deadHP = true;
		
		if (isEnemy)
		{
			
		}
		else
		{
			for (int i = 0; i < 6; ++i)
			{
				//ObjFrameMain();
			}
		}
		
		picKey = key;
		
		if (key.equals(""))
		{
			
		}
	}
	
	void procDead()
	{
		if (isEne)
		{
			// Console.consoleStatic.addMes("destroy enemy.");
			Stage.stage.ene.addScore();
			isDead = true;
			
			//
			Effect.Shape.auto(posX, posY, 8, 3);
		}
		else
		{
			++GameController.stGC.score;
		}
	}
	
	String getKey()
	{
		return picKey;
	}
	
	boolean ObjFrameMain()
	{
		move.Frame(this);
		Atalant.Frame(this);
		
		if (!isEne)
		{
			/*
			// eb系統
			if (picKey.substring(0,2).equals("eb"))
			{
				picKey = "eb" + ((Cnt / 3) % 12 + 1); 
			}
			*/
			// 敵弾時の自機ヒット動作
			if (GameController.stGC.playerCharacter.checkHit(this))
			{
				GameController.stGC.playerCharacter.callHitProc();
				
				return true;
			}
		}
		
		if (checkOutOfScreen())
		{
			return true;
		}
		
		return false;
	}
	
	// 以下のクラスがクラスを保持することはcloneメソッドの云々のために禁止されています。
	// -----------------------------------------------------------------------------------------------------------------
	public static class Mover implements Cloneable
	{
		// 出力先としてつかう想定
		double x, y;
		
		public Mover clone()
		{
			try
			{
				return (Mover) super.clone();
			}
			catch (CloneNotSupportedException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
		Mover(double baseX, double baseY)
		{
			x = baseX;
			y = baseY;
		}
		
		// こっちをオーバーライド
		void FrameMain(Actor arg)
		{
			
		}
		
		void Frame(Actor arg)
		{
			FrameMain(arg);
			
			arg.posX = x;
			arg.posY = y;
		}
		
		void inConstructor(Actor arg, String key)
		{
			
		}
		
		public boolean enableRota(String key)
		{
			if (key.equals("ene1") || key.equals("ene2") || key.equals("ene3") || key.equals("eb3")
				|| key.equals("eb4"))
			{
				return true;
			}
			
			return false;
		}
	}
	
	public static class MLinear extends Mover
	{
		double sX, sY;
		
		MLinear(double baseX, double baseY, double spdX, double spdY)
		{
			super(baseX, baseY);
			sX = spdX;
			sY = spdY;
		}
		
		void FrameMain(Actor arg)
		{
			x += sX;
			y += sY;
		}
		
		void inConstructor(Actor arg, String key)
		{
			if (!enableRota(key))
			{
				return;
			}
			
			arg.useRot = true;
			arg.rot = Math.atan2(sY, sX);
			
			if (key.equals("ene2") || key.equals("ene3"))
			{
				if (arg.posX < Stage.gameScrX / 2)
				{
					arg.rotAdd = Math.toRadians(5);
				}
				else
				{
					arg.rotAdd = Math.toRadians(-5);
				}
			}
		}
	}
	
	public static class Row extends Mover
	{
		double posX, posY, posXAdd, posYAdd;
		double tX, tY, txAdd = 0, tyAdd = 0;
		double rSpdX, rSpdY, rSpdXAdd = 0, rSpdYAdd = 0;
		int Cnt = 0;
		
		// 状態事の時間 (向かう、停止のふたつ帰還は値なし)
		int first, second;  // secondにはsecond状態+first状態の合計値
		
		// 回るものを減らした
		public boolean enableRota(String key)
		{
			if (key.equals("ene2") || key.equals("ene3"))
			{
				return true;
			}
			
			return false;
		}
		
		void inConstructor(Actor arg, String key)
		{
			/*
			if (!enableRota(key))
			{
				return;
			}
			*/
			arg.useRot = true;
			
			if (key.equals("ene2") || key.equals("ene3"))
			{
				if (arg.posX < Stage.gameScrX / 2)
				{
					arg.rotAdd = Math.toRadians(5);
				}
				else
				{
					arg.rotAdd = Math.toRadians(-5);
				}
			}
			else
			if (key.equals("ene4"))
			{
				arg.useRot = false;
			}
			else
			{
				arg.rot = Math.toRadians(90);
			}
		}
		
		public void minimalRow(int argFT, int argST, double argtX, double argtY, double argspdX, double argspdY)
		{
			tX = argtX;
			tY = argtY;
			rSpdX = argspdX;
			rSpdY = argspdY;
			
			first = argFT;
			second = argFT + argST;
		}
		
		public Row(int argFT, int argST, double pX, double pY, double argtX, double argtY, double argspdX, double argspdY)
		{
			super(pX, pY);
			posX = pX;
			posY = pY;
			minimalRow(argFT, argST, argtX, argtY, argspdX, argspdY);
		}
		
		public Row(int argFT, int argST, double pX, double pY, double argpXAdd, double argpYAdd, double argtX, double argtY, double argtXAdd, double argtYAdd, double argspdX, double argspdY, double argspdXAdd, double argspdYAdd)
		{
			super(pX, pY);
			posX = pX;
			posY = pY;
			minimalRow(argFT, argST, argtX, argtY, argspdX, argspdY);
			posXAdd = argpXAdd;
			posYAdd = argpYAdd;
			txAdd = argtXAdd;
			tyAdd = argtYAdd;
			rSpdXAdd = argspdXAdd;
			rSpdYAdd = argspdYAdd;
		}
		
		void FrameMain(Actor arg)
		{
			if (Cnt <=  first)
			{
				x = posX + GameParameter.ScaleSine(tX - posX, Cnt, first);
				y = posY + GameParameter.ScaleSine(tY - posY, Cnt, first);
			}
			else
			if (Cnt <= second)
			{
				
			}
			else
			{
				x += rSpdX;
				y += rSpdY;
			}
			
			++Cnt;
		}
		
		// クローンをオーバーライドしてコピーされた敵ごとに違う動作を設定する
		public Row clone()
		{
			Row r = (Row) super.clone();
			
			posX += posXAdd;
			posY += posYAdd;
			
			tX += txAdd;
			tY += tyAdd;
			
			rSpdX += rSpdXAdd;
			rSpdY += rSpdYAdd;
			
			return r;
		}
	}
	
	public static class Skyserpent extends Mover
	{
		boolean over;  // 作成時に自分の方が上
		boolean first = true;
		double xs, ys;
		double xs2, ys2;
		int turn;
		
		public Skyserpent(double baseX, double baseY, double xspd, double yspd, double xspd2, double yspd2, int argTurn)
		{
			super(baseX, baseY);
			
			over = GameController.stGC.playerCharacter.posY > baseY;
			xs = xspd;
			ys = yspd;
			xs2 = xspd2;
			ys2 = yspd2;
			turn = argTurn;
		}
		
		void inConstructor(Actor arg, String key)
		{
			if (!enableRota(key))
			{
				return;
			}
			
			arg.useRot = true;
			arg.rot = Math.PI / 2;
			
			if (key.equals("ene2") || key.equals("ene3"))
			{
				if (arg.posX < Stage.gameScrX / 2)
				{
					arg.rotAdd = Math.toRadians(5);
				}
				else
				{
					arg.rotAdd = Math.toRadians(-5);
				}
			}
		}
		
		void FrameMain(Actor arg)
		{
			Enemy e = (Enemy)arg;
			
			if (!first)
			{
				if (turn > 0)
				{
					--turn;
					return;
				}
				
				// 横移動
				x += xs2;
				y += ys2;
				return;
			}
			
			x += xs;
			y += ys;
			
			if (GameController.stGC.playerCharacter.posY > y != over)
			{
				// 転換
				first = false;
				
				if (!(GameController.stGC.playerCharacter.posX > x))
				{
					xs2 *= -1;
				}
				
				e.Atalant.freez = 1;  // 転換時にArcherのFreezを強制的に1にする
			}
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------
	public static class Archer implements Cloneable
	{
		static Archer nullArcher = new Archer();
		public String key = "eb1";
		int cnt = 0;
		int freez = 0;
		
		public Archer clone()
		{
			try
			{
				return (Archer) super.clone();
			}
			catch (CloneNotSupportedException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
		
		public void Frame(Actor make)
		{
			++cnt;
			
			if (freez > 0)
			{
				--freez;
				return;
			}
			
			FrameMain(make);
		}
		
		void FrameMain(Actor make)
		{
			
		}
		
		Enemy CreateShot(Mover m)
		{
			Enemy tmp = new Enemy(false, key, m, Archer.nullArcher);
			
			for (int i = 0; i < 2; ++i)
			{
				tmp.ObjFrameMain();
			}
			
			return tmp;
		}
		
		void wayShoot(int way, double spd, double rangeAng, Actor argAim, Actor argMake)
		{
			double ang = Math.atan2(argAim.posY - argMake.posY, argAim.posX - argMake.posX) - (way == 1 ? 0 : rangeAng / 2.0);
			double loopAng = (way == 1 ? rangeAng : rangeAng / (way - 1));
			
			for (int i = 0; i < way; ++i)
			{
				//Enemy e = 
				CreateShot(new MLinear(argMake.posX, argMake.posY, spd * Math.cos(ang), spd * Math.sin(ang)));
				ang += loopAng;
			}
		}
		
		Random r = new Random(System.currentTimeMillis());
		
		void wayShootRand(int way, double spd, double rangeAng, Actor argAim, Actor argMake)
		{
			double ang = r.nextDouble() * Math.PI * 2;
			double loopAng = (way == 1 ? rangeAng : rangeAng / (way - 1));
			
			for (int i = 0; i < way; ++i)
			{
				//Enemy e = 
				CreateShot(new MLinear(argMake.posX, argMake.posY, spd * Math.cos(ang), spd * Math.sin(ang)));
				ang += loopAng;
			}
		}
	}
	
	public static class Yabusame extends Archer
	{		
		int way, rapid, interval;
		double spd, spdAdd, range;
		
		Yabusame(int argWay, int argInterval, int argRapid, double argSpd, double argSpdAdd, double argRange, String argKey)
		{
			way = argWay;
			rapid = argRapid;
			interval = argInterval;
			spd = argSpd;
			spdAdd = argSpdAdd;
			range = argRange;
			key = argKey;
		}
		
		void FrameMain(Actor make)
		{
			if (cnt % interval == 0)
			{
				for (int i = 0; i < rapid; ++i)
				{
					wayShoot(way, spd + i * spdAdd, range, GameController.stGC.playerCharacter, make);
				}
			}
		}
	}
	
	public static class YabusameOnce extends Yabusame
	{	
		YabusameOnce(int argWay, int argInterval, int argRapid, double argSpd, double argSpdAdd, double argRange, String argKey)
		{
			super(argWay, argInterval, argRapid, argSpd, argSpdAdd, argRange, argKey);
		}

		boolean ret = false;
		
		void FrameMain(Actor make)
		{
			if (ret)
			{
				return;
			}
			
			if (cnt % interval == 0)
			{
				for (int i = 0; i < rapid; ++i)
				{
					wayShoot(way, spd + i * spdAdd, range, GameController.stGC.playerCharacter, make);
				}
			}
			
			ret = true;
		}
	}
	
	public static class YabusameRand extends Yabusame
	{
		YabusameRand(int argWay, int argInterval, int argRapid, double argSpd, double argSpdAdd, double argRange, String argKey)
		{
			super(argWay, argInterval, argRapid, argSpd, argSpdAdd, argRange, argKey);
		}
		
		void FrameMain(Actor make)
		{
			if (cnt % interval == 0)
			{
				for (int i = 0; i < rapid; ++i)
				{
					wayShootRand(way, spd + i * spdAdd, range, GameController.stGC.playerCharacter, make);
				}
			}
		}
	}
	
	public static class YabusameRandOnce extends YabusameRand
	{
		boolean ret = false;
		
		YabusameRandOnce(int argWay, int argInterval, int argRapid, double argSpd, double argSpdAdd, double argRange, String argKey)
		{
			super(argWay, argInterval, argRapid, argSpd, argSpdAdd, argRange, argKey);
		}
		
		void FrameMain(Actor make)
		{
			if (ret)
			{
				return;
			}
			
			if (cnt % interval == 0)
			{
				for (int i = 0; i < rapid; ++i)
				{
					wayShootRand(way, spd + i * spdAdd, range, GameController.stGC.playerCharacter, make);
				}
				ret = true;
			}
		}
	}
	
	public static class Gilgames extends Archer
	{
		public Gilgames()
		{
			super();
			
			key = "eb1";
		}
		
		void FrameMain(Actor make)
		{
			if (cnt % 60 == 0)
			{
				final int num = 16;
				final double spd = 8;
				Archer Yabusame = new Yabusame(3, 45, 1, 6, 0.6, Math.toRadians(360), "eb4");
				Enemy e;
				for (int i = 0; i < num; ++i)
				{
					double x = r.nextDouble() * (Stage.gameScrX - 20);
					double ang =  Math.atan2(GameController.stGC.playerCharacter.posY - 0, GameController.stGC.playerCharacter.posX - x);
					e = new Enemy(true, "ene2", 
							new MLinear(x + 10, 0, spd * Math.cos(ang), spd * Math.sin(ang)),
							Yabusame.clone(), GameParameter.layerEnemyL);
					e.setHP(10);
				}
			}
			
			if (cnt % 30 == 0)
			{
				wayShoot(18, 8, Math.toRadians(360), GameController.stGC.playerCharacter, make);
				wayShoot(18, 8.5, Math.toRadians(360), GameController.stGC.playerCharacter, make);
				wayShoot(18, 9, Math.toRadians(360), GameController.stGC.playerCharacter, make);
			}
		}
	}
}











