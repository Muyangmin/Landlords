package com.mym.landlords.ai;

import java.util.ArrayList;

import com.mym.landlords.card.Card;

/**
 * 处理游戏的AI逻辑。
 * <p>所有的字段和方法访问权限均为包级别访问，即外界不可见。方法会通过Player类进行封装，对外界透明。
 * 此外，所有的方法均不做为静态，主要是为了AI的定制化和多样化考虑，可以引入策略模式使得AI更灵活多变。</p>
 * @author Muyangmin
 * @create 2015-3-21
 */
final class AI {
	//package access
	AI(){}
	
	private final int callLandlord(ArrayList<Card> cards){
		return Game.BASIC_SCORE_THREE;
	}
	
	/**
	 * 返回叫牌信息。
	 * @param cards 手牌列表。
	 * @param minScore 最低分（不包含）。这个分数通常是上一个玩家叫的分数。
	 * @return 返回一个比minScore更大的分数，或者 是{@link Game#BASIC_SCORE_NONE}，表示不叫。
	 */
	protected final int callLandlord(ArrayList<Card> cards, int minScore) {
		int alalysis = callLandlord(cards);
		return alalysis > minScore ? alalysis : Game.BASIC_SCORE_NONE;
	}
}
