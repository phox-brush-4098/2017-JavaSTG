package minigame;

import java.util.Random;

// 一旦拡散した後喪とも近い敵を狙う球を発射する、拡散するほう。
public class OShotB1 extends Actor
{
	static final int procTime = 6;  // 拡散にかかる時間

	public double begX, begY, xParam, yParam;
	
	static Random r = new Random(System.currentTimeMillis());
	
	public OShotB1(double posX, double posY)
	{
		super(posX, posY, GameParameter.layerPlayer2);
		
		final double distAdd = 80, distMin = 80;
		double dist, ang;
		dist = r.nextDouble() * distAdd + distMin;
		ang = r.nextDouble() * 2 * Math.PI;

		begX = posX;
		begY = posY;
		xParam = dist * Math.cos(ang);
		yParam = dist * Math.sin(ang);
	}
	
	String getKey()
	{
		return "p_bit";
	}

	boolean ObjFrameMain()
	{
		if (Cnt <= procTime)
		{
			posX = begX + GameParameter.ScaleSine(xParam, (double)Cnt, (double)procTime);
			posY = begY + GameParameter.ScaleSine(yParam, (double)Cnt, (double)procTime);
		}
		else
		{
			double tmpX = GameController.stGC.playerCharacter.posX;
			double tmpY = GameController.stGC.playerCharacter.posY;
			
			posX = tmpX + GameParameter.ScaleSine(xParam - tmpX, (double)procTime * 2 - Cnt, (double)procTime);
			posY = tmpY + GameParameter.ScaleSine(yParam - tmpY, (double)procTime * 2 - Cnt, (double)procTime);
		}
		
		if (Cnt == procTime)
		{
			// 子弾生成処理
			Actor tmp = Search(posX, posY, GameParameter.layerEnemy);
			
			final double childSpd = 23;
			final double childSpdAdd = 3;
			
			for (int i = 0; i < 2; ++i)
			{
				double cs = childSpd + childSpdAdd * i;
				
				if (tmp == null)
				{
					// new OShotB1Main(posX, posY, 0.0, -cs);
				}
				else
				{
					double ang = Math.atan2(tmp.posY - posY, tmp.posX - posX);
					OShotB1Main t = new OShotB1Main(posX, posY, cs * Math.cos(ang), cs * Math.sin(ang));
					t.useRot = true;
					t.rot = ang - Math.toRadians(90);
				}
			}
			
			// 移動パラメーターの意味が変わる
			xParam = posX;
			yParam = posY;
		}
		else
		if (Cnt == procTime * 2)
		{
			return true;
		}
		
		return false;
	}
}
