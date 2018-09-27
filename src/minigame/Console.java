package minigame;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Console
{
	// 最後に生成された実体が保持される
	public static Console consoleStatic;
	
	public Console()
	{
		c = new ArrayList<Element>(cMax);
		for (int i = 0; i < cMax; ++i)
		{
			c.add(new Element("empty"));
		}
		
		consoleStatic = this;
	}

	ArrayList<Element> c;
	
	final int cMax = 8;
	int ptr = 0;
	final boolean onlyDebug = false;
	
	public void addMes(String arg)
	{
		if (onlyDebug)
		{ return; }
		
		c.get(ptr).set(arg);
		ptr = (ptr + 1) % cMax;
	}
	
	public void addMesDebug(String arg)
	{
		c.get(ptr).set(arg);
		ptr = (ptr + 1) % cMax;
	}
	
	public void draw(Graphics2D g)
	{
		g.setFont(new Font("Yu Gothic", Font.BOLD, 15));
		
		int addY = 0;
		
		for (int i = 0; i < cMax; ++i)
		{
			Element tmp = c.get((i + ptr) % cMax);
			if (tmp.Life > 0)
			{
				--tmp.Life;
				g.drawString(tmp.Mes, 8, 85 + 14 * addY);
				++addY;
			}
		}
	}
	
	public static class Element
	{
		static final int defLife = 80;
		
		Element(String argS)
		{
			Mes = argS;
			Life = 0;
		}
		
		void set(String mes)
		{
			Mes = mes;
			Life = defLife;
		}
		
		public String Mes;
		public int Life;
	}
}
