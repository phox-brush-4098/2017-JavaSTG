package minigame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

// 参考
// https://ja.wikipedia.org/wiki/%E3%83%A9%E3%82%A4%E3%83%95%E3%82%B2%E3%83%BC%E3%83%A0
// http://aidiary.hatenablog.com/entry/20080914/1281748797
public class Conway
{
	ArrayList<ArrayList<Boolean>> buff;

	static final int buffNum = 2;  // 2以上。バッファを描画しないときはどうせ多くても使わないので2にして
	static final boolean isDrawBuff = false;//true;
	static final boolean isUseMutation = true;
	public int MutationParam = 180;       // 変異フレームでの変異率 1/これ
	public int ExceMutationParam = 120;   // 過疎条件を満たしていないとき、フレームが突然変異可能状態になる確率の設定1/これ
	static final int enebleMutLiveRate = 24;    // 変異増殖閾過疎値
	
	// 
	int useBuff = 0;
	final int wid, hei, extRegion;

	static final int liveBeg = 2, liveEnd = 3, grow = 3;

	// 表示サイズプロパティ、コンストラクタで受け取った表示サイズをライフゲームフィールドサイズに変換するときとかに使ったり
	static final int extend = 5;    // マスサイズ
	static final int drawSize = 4;  // マスのうちで描画されるサイズ

	Random r = new Random(System.currentTimeMillis());
	
	int befCellLive;
	
	public Conway(int Wid, int Hei, int Ext)
	{
		Wid /= extend;
		Hei /= extend;
		
		buff = new ArrayList<ArrayList<Boolean>>();

		for (int i = 0; i < buffNum; ++i)
		{
			buff.add(new ArrayList<Boolean>(Wid * Hei));

			if (i == 0)
			{
				for (int i2 = 0; i2 < Wid * Hei; ++i2)
				{
					buff.get(i).add(r.nextInt() % 12 == 0 ? true : false);
					// System.out.println(buff.get(i).get(i2));
				}
			}
			else
			{
				buff.get(i).addAll(buff.get(0));
			}
		}

		wid = Wid;
		hei = Hei;
		extRegion = Ext / extend;
	}

	public void draw()
	{
		draw(Actor.canvas);
	}

	// 生存チェック
	boolean checkCell(int cell, int max, ArrayList<Boolean> arg)
	{
		// このif中で上端と下端をつなげられたと思うんだけどできてなさそうだったらreturn 0に戻せ
		if (cell < 0 || cell >= max)
		{
			cell = (cell + max) % max;

			// return false;
		}

		return arg.get(cell);
	}

	//
	public void create(int posX, int posY)
	{
		posX /= extend;
		posY /= extend;
		
		if (posX < 0 || posX > wid || posY < 0 || posY > hei)
		{
			return;
		}
		
		buff.get(useBuff).set((posX + extRegion) + (posY + extRegion) * wid, true);
	}

	void loop(ArrayList<Boolean> bef, ArrayList<Boolean> now)
	{
		for (int i = 0, n = wid * hei; i < n; ++i)
		{
			int Cnt = 0;
			boolean frameEnMutation = befCellLive < n / enebleMutLiveRate && isUseMutation;
			
			// 密度低下が起こっていなくても変異発生させる確率
			if (r.nextInt() % ExceMutationParam == 0)
			{
				frameEnMutation = true;
			}

			Cnt += checkCell(i - wid - 1, n, bef) ? 1 : 0;
			Cnt += checkCell(i - wid, n, bef) ? 1 : 0;
			Cnt += checkCell(i - wid + 1, n, bef) ? 1 : 0;
			Cnt += checkCell(i - 1, n, bef) ? 1 : 0;
			Cnt += checkCell(i + 1, n, bef) ? 1 : 0;
			Cnt += checkCell(i + wid - 1, n, bef) ? 1 : 0;
			Cnt += checkCell(i + wid, n, bef) ? 1 : 0;
			Cnt += checkCell(i + wid + 1, n, bef) ? 1 : 0;

			if (bef.get(i))
			{
				if (Cnt >= liveBeg && Cnt <= liveEnd)
				{
					now.set(i, true);
				}
				else
				{
					now.set(i, false);
				}
			}
			else
			{
				if (Cnt == grow)
				{
					now.set(i, true);
				}
				else
				{
					now.set(i, false);
					
					// 特殊発生
					if (frameEnMutation && Cnt > 0)
					{
						if (r.nextInt() % MutationParam == 0)
						{
							now.set(i, true);
						}
					}
				}
			}
		}
	}
	
	public void run()
	{
		//for (int i2 = 0; i2 < 2; ++i2)
		//if (!drawOnly)
		{
			int befBuff = useBuff;
			useBuff = (useBuff + 1) % buffNum;

			ArrayList<Boolean> bef, now;
			// System.out.println(buff.get(0).size());
			bef = buff.get(befBuff);
			now = buff.get(useBuff);

			loop(bef, now);
		}
	}
	
	public void draw(BufferedImage arg)
	{
		Graphics2D g = arg.createGraphics();

		// draw
		if (isDrawBuff)
		{
			for (int i = 0; i < buffNum; ++i)
			{
				DrawMain(i, g);
			}
		}
		else
		{
			DrawMain(useBuff, g);
		}
	}
	
	public int cR = 48, cG = 134, cB = 96;
	
	int add = 0;
	int addCnt = 1;
	
	public void addView(int arg)
	{
		// add = (add + hei - arg * extend) % hei;
		add = (add + hei - arg) % hei;
	}
	
	void DrawMain(int i, Graphics2D g)
	{
		ArrayList<Boolean> print = isDrawBuff ?  buff.get((i + useBuff + 1) % buffNum) : buff.get(i);
		g.setColor(isDrawBuff ? new Color((int)(cR * (double)(i+1) / buffNum), (int)(cG * (double)(i+1) / buffNum), (int)(cB * (double)(i+1) / buffNum)) : new Color(cR, cG, cB));

		befCellLive = 0;
		//
		for (int i2 = 0; i2 < print.size() - wid * extRegion * 2; ++i2)
		{
			if (print.get((i2 + add * wid) % print.size()))
			{
				// 目がちかちかする
				// g.setColor(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
				int x = i2 % wid - extRegion, y = i2 / wid ;

				++befCellLive;
				
				if (!(x < 0 || x > Stage.gameScrX || y < 0 || y > Stage.gameScrY))
				{
					// g.fillRect(x, y, 1, 1);
					g.fillRect(x * extend, y * extend, drawSize, drawSize);
				}
			}
		}
		/*
		for (int i2 = wid * extRegion, n = print.size() - wid * extRegion; i2 < n; ++i2)
		{
			// System.out.println(print.get(i2));
			if (print.get(i2))
			{
				int x = i2 % wid - extRegion, y = i2 / wid - extRegion;

				++befCellLive;
				
				if (!(x < 0 || x > Stage.gameScrX || y < 0 || y > Stage.gameScrY))
				{
					// g.fillRect(x, y, 1, 1);
					g.fillRect(x * extend, y * extend, drawSize, drawSize);
				}
			}
		}
		*/
	}
}
