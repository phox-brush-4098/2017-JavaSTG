package minigame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.imageio.ImageIO;

// Actorクラスの管理はStageが行うことになりました。
public class Actor
{
	int Cnt = 0;
	int HP = 0;
	boolean deadHP = false;

	// private Image image; // Actorの見た目を保持 picListから引っ張ってくる形に変更。
	protected double posX, posY; // Actorの現在の位置

	// 追加順に描画する機能を実装するためArrayListからLinkedListに変更
	static ArrayList<LinkedList<Actor>> actors; // 全てのActorを保持  描画順序を保持できるようにするため、レイヤー機能を導入。

	// キャンバスオブジェクト
	// 裏画面
	static BufferedImage canvas = new BufferedImage(Stage.PANEL_W, Stage.PANEL_H, BufferedImage.TYPE_INT_RGB);
	// 描画準備ができている画面 (手動ダブルバッファ機能を使う場合実体が必要)
	static BufferedImage cvDraw;// = new BufferedImage(Stage.PANEL_W, Stage.PANEL_H, BufferedImage.TYPE_INT_RGB);

	BufferStrategy bs;

	static int getActorCntByLayer(int layer)
	{
		return actors.get(layer).size();
	}
	
	public static Actor Search(double argX, double argY, int layer)
	{
		Actor res = null;
		double dist = 9999999, tmp;
		
		for (Actor i : actors.get(layer))
		{
			tmp = Math.pow(argX - i.posX, 2) + Math.pow(argY - i.posY, 2);
			if (tmp < dist)
			{
				res = i;
				dist = tmp;
			}
		}
		
		return res;
	}
	
	// 手動ダブルバッファ無効
	public static void SwapCanvas()
	{
		
	}

	public static void Draw(Graphics2D argCv, ImageObserver argObj)
	{
		argCv.drawImage(Actor.canvas, 0, 0, argObj);
	}
	// 画像リスト
	public static HashMap<String, BufferedImage> picList;  // 画像本体
	public static HashMap<String, Double> picDist;         // 画像ごとの判定データ
	
	static void addPic(String fname, double d) throws IOException
	{
		addPic(fname, ".png", d);
	}

	static void addPic(String fname, String ext, double d) throws IOException
	{
		//http://d.hatena.ne.jp/speg03/20090313/1236921793
		String imageFileName = "dat/" + fname + ext;
		URL imageUrl = Stage.stage.getClass().getClassLoader().getResource(imageFileName);
		
		picList.put(fname, ImageIO.read(imageUrl));
		picDist.put(fname, d);
	}
	
	// アプリケーションの初期化時に呼ぶ
	static void Settings()
	{
		actors = new ArrayList<LinkedList<Actor>>();
		for (int i = 0; i < GameParameter.layerNum; ++i)
		{
			actors.add(new LinkedList<Actor>());
		}

		picList = new HashMap<String, BufferedImage>(32, 0.9f);
		picDist = new HashMap<String, Double>(32, 0.9f);

		try
		{
			addPic("player", 1.0);
			addPic("p_bit", 0.0);
			addPic("obul", 13.0);
			addPic("obul2", 18.0);
			addPic("ene1", 20.0);
			addPic("ene2", 22.0);
			addPic("ene3", 22.0);
			addPic("ene4", 32.0);
			addPic("sword1", 0.0);
			addPic("eb1", 4.0);
			addPic("eb2", 8.0);
			addPic("eb3", 8.0);
			addPic("eb4", 4.0);
			//for (int i = 1; i <= 12; ++i)
			//{ addPic("eb"+i, 12.0); }
		}
		catch (IOException e)
		{
			// 自動生成された catch ブロック
			SO.println("exception");
			e.printStackTrace();
		}
	}

	// レイヤー機能を搭載したためコンストラクタにlayerIDパラメーターが追加。layerIDが小さいほど先に(奥に)描画される。
	public Actor(double posX, double posY, int layerID)
	{
		this.posX = posX;
		this.posY = posY;
		Cnt = 0;
		if (layerID > GameParameter.layerNum)
		{
			layerID = GameParameter.layerNum;
		}
		Actor.actors.get(layerID).add(this); // actorsリストに自分自身を登録
	}

	// グラフィックスgに現在actorsリストに登録されている全てのActorを描き出すためのメソッド
	// static保持のキャンバスに描画済み内容が存在するので受け取ったGraphicsに転送するだけ
	static public void paintActors(Graphics g, ImageObserver io)
	{
		// 後で使う
		// Graphics2D g2 = (Graphics2D)(g);
	}

	// この値で回転する
	double rot = 0;
	double rotAdd = 0;
	boolean useRot = false;

	void DrawFromKey(String arg)
	{
		DrawFromKey(arg, (int) posX, (int) posY);
	}

	// 関数内で毎度作っても意味ないので外で
	static AffineTransform at = new AffineTransform();
	
	void DrawFromKey(String arg, int x, int y)
	{
		BufferedImage img = picList.get(arg);

		if (useRot)
		{
			rot += rotAdd;
			at.setToRotation(rot, img.getWidth() / 2, img.getHeight() / 2);

			AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			BufferedImage bufferedImage;
			try
			{
				bufferedImage = op.filter(img, null);
				canvas.createGraphics().drawImage(bufferedImage, x - img.getWidth() / 2, y - img.getHeight() / 2,
						Stage.stage);
			}
			catch (Exception e)
			{
			}

			return;
		}
		canvas.createGraphics().drawImage(img, x - img.getWidth() / 2, y - img.getHeight() / 2, Stage.stage);
	}

	// 判定サイズ
	String getKey()
	{
		return "player";
	}

	boolean checkOutOfScreen()
	{
		final double oueExt = 1;
		
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

	// 中心からの距離
	double getHitSize()
	{
		return picDist.get(getKey());
	}

	boolean checkHit(Actor arg)
	{
		if (deadHP && HP < 0)
		{
			return false;
		}
		// SO.println(getHitSize());
		if (Math.pow(posX - arg.posX, 2) + Math.pow(posY - arg.posY, 2) < Math.pow(arg.getHitSize() + getHitSize(), 2))
		{
			return true;
		}
		
		return false;
	}
	
	public enum AtkType
	{
		Normal,
	}
	
	boolean toHit(Actor arg, AtkType type, int damage)
	{
		if (checkHit(arg))
		{
			HP -= damage;
			
			return true;
		}
		
		return false;
	}
	
	boolean procAttack(int layer, boolean through, AtkType at, int damage)
	{
		boolean result = false;
		LinkedList<Actor> list = actors.get(layer);
		
		for (Actor a : list)
		{
			if (a.toHit(this, at, damage))
			{
				if (!through)
				{
					return true;
				}
				result = true;
			}
		}
		
		return result;
	}
	
	void procDead()
	{
		
	}
	
	void ObjFrameDraw()
	{
		DrawFromKey(getKey());
	}

	// 継承クラスごとのフレーム動作。1を返すと削除要求
	boolean ObjFrameMain()
	{
		return false;
	}

	// 各実体のFrame動作
	boolean ObjFrame()
	{
		if (HP < 0 && deadHP)
		{
			procDead();
			
			return true;
		}
		
		boolean res = ObjFrameMain();
		ObjFrameDraw();
		
		++Cnt;
		return res;
	}
	
	// すべてのActorの動作をする。
	public static void Frame()
	{
		// SO.println("frame");
		for (LinkedList<Actor> actorArray : Actor.actors)
		{
			// SO.println(actorArray.size());
			Iterator<Actor> itr = actorArray.iterator();
			while (itr.hasNext())
			{
				if (itr.next().ObjFrame())
				{
					itr.remove();
				}
			}
		}
	}

	// 衝突判定が必要ないため無効化状態
	public static void enemyClear()
	{
		actors.get(GameParameter.layerEnemy).clear();
		actors.get(GameParameter.layerEnemy2).clear();
		actors.get(GameParameter.layerEnemyL).clear();
	}
}