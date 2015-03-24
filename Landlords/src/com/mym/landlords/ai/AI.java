package com.mym.landlords.ai;

import java.util.ArrayList;

import com.mym.landlords.card.Card;

/**
 * 处理游戏的AI逻辑。
 * @author Muyangmin
 * @create 2015-3-21
 */
final class AI {
	
	private AI(){}
	
	private static final int callLandlord(ArrayList<Card> cards){
		return Game.BASIC_SCORE_THREE;
	}
	
	/**
	 * 返回叫牌信息。
	 * @param cards 手牌列表。
	 * @param minScore 最低分（不包含）。这个分数通常是上一个玩家叫的分数。
	 * @return 返回一个比minScore更大的分数，或者 是{@link Game#BASIC_SCORE_NONE}，表示不叫。
	 */
	protected static final int callLandlord(ArrayList<Card> cards, int minScore) {
		int alalysis = callLandlord(cards);
		return alalysis > minScore ? alalysis : Game.BASIC_SCORE_NONE;
	}
}
