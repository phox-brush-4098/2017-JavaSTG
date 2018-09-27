package minigame;

public class GameParameter
{
	// 使用レイヤーID
	public static final int layerEffect = 2; // エフェクト
	public static final int layerEnemy = 4;  // 普通の敵
	public static final int layerEnemyL = 5; // 大きい敵 現在は隊列外の追加的扱い
	public static final int layerEnemy2 = 6; // 敵弾
	public static final int layerPlayer = 3;
	public static final int layerPlayer2 = 1; // 自機弾
	public static final int layerPlayer3 = 0; // 自機弾
	public static final int layerNum = 7;

	public GameParameter()
	{
	}
	
	static double Scale(double arg, double param, double paramMax)
	{
		return arg * param / paramMax;
	}
	
	static double ScaleSine(double arg, double param, double paramMax)
	{
		// param = paramMax - param;
		// return arg * (1 - Math.sin(param / paramMax * Math.toRadians(90)));
		
		return arg * Math.sin(param / paramMax * Math.toRadians(90));
	}
	
	static double ScaleSinePow(double arg, double param, double paramMax)
	{
		// param = paramMax - param;
		// return arg * (1 - Math.pow(Math.sin(param / paramMax * Math.toRadians(90)), 2));
		return arg * Math.pow(Math.sin(param / paramMax * Math.toRadians(90)), 2);
	}
}
