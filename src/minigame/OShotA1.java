package minigame;

// とても一般的なショット。shot a1
public class OShotA1 extends Actor
{
	public double x = 0, y = 0;
	
	public OShotA1(double posX, double posY)
	{
		super(posX, posY, GameParameter.layerPlayer2);
	}
	
	public OShotA1(double posX, double posY, int l)
	{
		super(posX, posY, l);
	}
	
	String getKey()
	{
		return "obul";
	}
	
	public boolean shotHitProc()
	{
		if (procAttack(GameParameter.layerEnemy, false, AtkType.Normal, Player.damageA))
		{
			Effect.Shape.auto(posX, posY, 1, 3);
			return true;
		}
		if (procAttack(GameParameter.layerEnemyL, false, AtkType.Normal, Player.damageA))
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
		
		if (checkOutOfScreen())
		{
			return true;
		}
		
		return shotHitProc();
	}
}
