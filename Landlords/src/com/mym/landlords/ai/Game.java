package com.mym.landlords.ai;

/**
 * 代表一盘游戏的实体类，用于记录游戏内的各个变量。
 * @author Muyangmin
 * @create 2015-3-18
 */
public final class Game {
	/** 炸弹总数 */
	public int boomCount;	
	/** 基础积分。 */
	public int basicScore;
	
	private static Game instance = new Game();
	
	//隐藏构造方法
	private Game(){}
	
	/**
	 * 开始一局新游戏。注意：前一局的所有记录将丢失。
	 * @return 返回一个初始化后的游戏变量。
	 */
	public static Game newGame(){
		//采用享元模式设计，始终使用同一个对象，降低内存消耗。
		instance.basicScore = 0;
		instance.boomCount =0;
		return instance;
	}
}
