package minigame;

import java.util.LinkedList;

public class OShotC extends Actor
{
	double ang;

	public OShotC(double posX, double posY, double argAng)
	{
		super(posX, posY, GameParameter.layerPlayer2);
		useRot = true;
		ang = argAng;
	}

	String getKey()
	{
		return "sword1";
	}

	double getHitSize()
	{
		return 14;
	}
	
	boolean procAttack(int layer, boolean through, AtkType at, int damage)
	{
		boolean result = false;
		LinkedList<Actor> list = actors.get(layer);

		double circSize = 11;
		double addX = Math.cos(ang) * circSize;
		double addY = Math.sin(ang) * circSize;
		
		final double tmpX = posX, tmpY = posY;
		
		for (Actor a : list)
		{
			for (int i = 0; i < 12; ++i)
			{
				if (a.toHit(this, at, damage))
				{
					if (!through)
					{
						return true;
					}
					Effect.Shape.auto(posX, posY, 1, 3);
					result = true;
					break;
				}
				posX += addX;
				posY += addY;
			}
			posX = tmpX;
			posY = tmpY;
		}

		return result;
	}
	
	// こぴぺ
	boolean procAttackEB(int layer, AtkType at)
	{
		boolean result = false;
		LinkedList<Actor> list = actors.get(layer);
		// SO.println(list.size());
		double circSize = 12;
		double addX = Math.cos(ang) * circSize;
		double addY = Math.sin(ang) * circSize;
		
		final double tmpX = posX, tmpY = posY;
		
		final int skip = 4;
		
		for (Actor a : list)
		{
			posX += addX * skip;
			posY += addY * skip;
			
			for (int i = skip; i < 12; ++i)
			{
				//SO.println(a);
				if (a.toHit(this, at, 1))
				{
					Effect.Shape.auto(posX, posY, 1, 3);
					result = true;
					break;
				}
				posX += addX;
				posY += addY;
			}
			posX = tmpX;
			posY = tmpY;
		}

		return result;
	}

	public boolean shotHitProc()
	{
		procAttackEB(GameParameter.layerEnemy2, AtkType.Normal);
		if (procAttack(GameParameter.layerEnemy, true, AtkType.Normal, Player.damageC))
		{
			return true;
		}
		if (procAttack(GameParameter.layerEnemyL, true, AtkType.Normal, Player.damageC))
		{
			return true;
		}
		return false;
	}

	boolean ObjFrame()
	{
		shotHitProc();
		return true;
	}
}
