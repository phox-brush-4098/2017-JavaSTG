package minigame;

// B1ショットの飛んでいく方
public class OShotB1Main extends OShotA1
{
	// 追加引数は移動値。強制。
	public OShotB1Main(double posX, double posY, double mX, double mY)
	{
		super(posX, posY, GameParameter.layerPlayer3);
		
		x = mX;
		y = mY;
	}

	String getKey()
	{
		return "obul2";
	}
	
	public boolean shotHitProc()
	{
		if (procAttack(GameParameter.layerEnemy, false, AtkType.Normal, Player.damageB))
		{
			Effect.Shape.auto(posX, posY, 1, 3);
			return true;
		}
		if (procAttack(GameParameter.layerEnemyL, false, AtkType.Normal, Player.damageB))
		{
			Effect.Shape.auto(posX, posY, 1, 3);
			return true;
		}
		return false;
	}
	
	boolean ObjFrameMain()
	{
		posX += x;
		posY += y;
				
		if (checkOutOfScreen() && Cnt > 12)
		{
			return true;
		}
		
		return shotHitProc();
	}
}
