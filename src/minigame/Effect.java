package minigame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.Random;

public class Effect extends Actor
{
	public Effect(double posX, double posY)
	{
		super(posX, posY, GameParameter.layerEffect);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	void ObjFrameDraw()
	{
		
	}
	
	public static class Shape extends Effect
	{
		static Random r = new Random(System.currentTimeMillis());
		
		Polygon poly;
		double sX, sY, sExt = 0.98, spd, size = 7, sizeExt = 1.10, sizeExtAdd = -0.01;
		double ang, angAdd = 0;
		
		int polyNum;
		
		int []x;
		int []y;
		
		// 後でまとめる
		static public void auto3(double x, double y, int num, int poly)
		{
			for (int i = 0; i < num; ++i)
			{
				double ang = r.nextDouble() * Math.PI * 2;
				double spd = r.nextDouble() * 8;
				Shape s = new Shape(x + Math.cos(ang) * spd, y + Math.sin(ang) * spd, poly);
				
				ang = r.nextDouble() * Math.PI * 2;
				spd = r.nextDouble() * 4 + 4;
				
				s.angAdd = r.nextDouble() * Math.PI * 2 / 60.0;
				s.setSpd(spd * Math.cos(ang), spd * Math.sin(ang));
				s.sExt = 0.9875;
				s.sizeExt = 1.05;
			}
		}
		
		static public void auto2(double x, double y, int num, int poly)
		{
			for (int i = 0; i < num; ++i)
			{
				double ang = r.nextDouble() * Math.PI * 2;
				double spd = r.nextDouble() * 8;
				Shape s = new Shape(x + Math.cos(ang) * spd, y + Math.sin(ang) * spd, poly);
				
				ang = r.nextDouble() * Math.PI * 2;
				spd = r.nextDouble() * 16 + 3;
				
				s.angAdd = r.nextDouble() * Math.PI * 2 / 60.0;
				s.setSpd(spd * Math.cos(ang), spd * Math.sin(ang));
				s.sExt = 0.9875;
				s.sizeExt = 1.15;
				s.sizeExtAdd = -0.008;
			}
		}
		
		static public void auto(double x, double y, int num, int poly)
		{
			for (int i = 0; i < num; ++i)
			{
				double ang = r.nextDouble() * Math.PI * 2;
				double spd = r.nextDouble() * 8;
				Shape s = new Shape(x + Math.cos(ang) * spd, y + Math.sin(ang) * spd, poly);
				
				ang = r.nextDouble() * Math.PI * 2;
				spd = r.nextDouble() * 6 + 3;
				
				s.angAdd = r.nextDouble() * Math.PI * 2 / 60.0;
				s.setSpd(spd * Math.cos(ang), spd * Math.sin(ang));
			}
		}
		
		public Shape(double argPosX, double argPosY, int num)
		{
			super(argPosX, argPosY);
			
			x = new int[num];
			y = new int[num];
			
			polyNum = num;
		}
		
		public void setSpd(double x, double y)
		{
			sX = x;
			sY = y;
		}
		
		double getHitSize()
		{
			return 32;
		}
		
		boolean ObjFrameMain()
		{
			sX *= sExt;
			sY *= sExt;
			posX += sX;
			posY += sY;
			// SO.println(posX + "," + posY);
			size *= sizeExt;
			sizeExt += sizeExtAdd;
			ang =  (ang + angAdd + 2 * Math.PI) % 2 * Math.PI;
			//SO.println("--");
			for (int i = 0; i < polyNum; ++i)
			{
				double tmpAng = ang + (Math.PI * 2 / (double)polyNum * (double)i);
				//SO.println(tmpAng);
				x[i] = (int) (posX + size * Math.cos(tmpAng));
				y[i] = (int) (posY + size * Math.sin(tmpAng));
				//SO.println(size);
				// SO.println(sX + "," + sY);
				//SO.println(x[i] + "," + y[i]);
			}
			
			return checkOutOfScreen() || size <= 1;
		}
		
		static Stroke shapeStroke = new BasicStroke(1.5f);
		static Color strokeColor = new Color(190,190,190);
		
		void ObjFrameDraw()
		{
			Graphics2D g = canvas.createGraphics();
			g.setStroke(shapeStroke);
			g.setColor(strokeColor);
			g.drawPolygon(x, y, polyNum);
		}
	}
}












