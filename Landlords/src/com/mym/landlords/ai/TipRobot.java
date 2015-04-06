package com.mym.landlords.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.mym.landlords.card.Bomb;
import com.mym.landlords.card.Card;
import com.mym.landlords.card.CardType;
import com.mym.landlords.card.Pair;
import com.mym.landlords.card.Rocket;
import com.mym.landlords.card.Single;
import com.mym.landlords.card.Straight;
import com.mym.landlords.card.Three;

/**
 * 为人类玩家提供简单的提示功能的类。
 * @author Muyangmin
 * @create 2015-4-2
 */
public final class TipRobot {
	private static AI robot = new AI(null);
	
	/**
	 * 获取出牌提示。
	 * @param followType 要跟的牌
	 * @param handCards 手牌列表
	 * @return 返回出牌提示列表。
	 */
	public static ArrayList<CardType> getTips(CardType followType, List<Card> handCards){
		//如果是第一个出牌
		if (followType==null){
			PlayerCardsInfo info = robot.makeCards(handCards);
			return info.cardTypes;
		}
		//否则只能强制找出所有能组合出的适合当前牌型的牌
		ArrayList<CardType> types = new ArrayList<>();
		ArrayList<Card> cloneList = new ArrayList<>(handCards);
		if (followType instanceof Rocket){
			return types;
		}
		Card[] tempBombCards = new Card[4];
		final boolean followBomb = followType instanceof Bomb;
		final int bombValue = followType.getCardList().get(0).getValue();
		//先找出炸弹
		for (int i=0,lastValue=0; i<cloneList.size();i++){
			int thisValue = cloneList.get(i).getValue();
			//排除比前一个炸弹小的炸弹。
			if (followBomb && thisValue <= bombValue) {
				continue;
			}
			if (thisValue==lastValue){
				continue;
			}
			lastValue = thisValue;
			int realLength=0;
			for (int j=0; j<4 && i+j<cloneList.size(); j++){
				tempBombCards[j] = cloneList.get(i+j);
				realLength++;
			}
			//没有四张牌
			if (realLength<4){
				break;
			}
			if (tempBombCards[0].compareIgnoreSuit(tempBombCards[1])==0
					&& tempBombCards[1].compareIgnoreSuit(tempBombCards[2])==0 
					&& tempBombCards[2].compareIgnoreSuit(tempBombCards[3])==0){
				ArrayList<Card> cards =new ArrayList<>(Arrays.asList(tempBombCards)); 
				types.add(new Bomb(cards));
				cloneList.removeAll(cards);
			}
		}
		//如果是单牌，则找出所有比目标大的就可以了。
		if (followType instanceof Single){
			Card before = followType.getCardList().get(0);
			for (Card card: cloneList){
				if (card.compareIgnoreSuit(before)<=0){
					continue;
				}
				types.add(new Single(card));
			}
		}
		//王炸可以拆开打单牌，但是对于其他情形就只能用作炸弹了
		int[] rocketPattern = new int[]{Card.CARD_VALUE_JOKER_S, Card.CARD_VALUE_JOKER_B};
		ArrayList<Card> rocketCards = robot.takeoutCards(rocketPattern, cloneList);
		if (rocketCards!=null){
			types.add(new Rocket(rocketCards));
			cloneList.removeAll(rocketCards);
		}
		//继续在剩下的卡片中寻找匹配的牌型
		if (followType instanceof Pair){
			//跳过比原牌小的
			Card before = followType.getCardList().get(0);
			for (int i=0, lastValue = 0; i+1<cloneList.size(); i++){
				Card thisCard = handCards.get(i);	
				if (thisCard.compareIgnoreSuit(before)<=0){
					continue;
				}
				if (thisCard.getValue()==lastValue){
					continue;
				}
				Card nextCard = handCards.get(i+1);
				if (thisCard.compareIgnoreSuit(nextCard)==0){
					types.add(new Pair(createArrayList(thisCard, nextCard)));
					//因可能存在三条的情形，为避免对同一点数提示两次，直接跳过已经找到的对子。
					lastValue = thisCard.getValue();
				}
			}
		}
		else if (followType instanceof Three){
			Card before = followType.getCardList().get(0);
			for (int i=0, lastValue = 0; i<cloneList.size()-2; i++){
				Card thisCard = handCards.get(i);
				//跳过比原牌小的
				if (thisCard.compareIgnoreSuit(before)<=0){
					continue;
				}
				if (thisCard.getValue()==lastValue){
					continue;
				}
				Card nextCard = handCards.get(i+1);
				Card thirdCard = handCards.get(i+2);
				if (thisCard.compareIgnoreSuit(nextCard)==0 
						&& nextCard.compareIgnoreSuit(thirdCard)==0){
					types.add(new Three(createArrayList(thisCard, nextCard, thirdCard)));
				}
				//因为已经在之前找出了炸弹，因此这里主要涉及没有组成三个的对子，跳过这些对子的第二张牌。
				lastValue = thisCard.getValue();
			}
		}
		else if (followType instanceof Straight){
			ArrayList<Straight> straights = StraightAnalyst.forceGetStraights(
					(Straight) followType, cloneList);
			if (straights!=null){
				types.addAll(straights);	
			}
		}
		Collections.sort(types, CardType.SORT_COMPARATOR);
		return types;
	}
	
	/**
	 * 创建一个包含指定元素的数组列表并返回。
	 * @param params 要包含的元素。所有元素必须为同一类型，否则返回的列表的行为是不确定的（堆污染）。
	 * @return 返回包含指定元素的列表，不会为null。
	 */
	@SafeVarargs
	public static final <T> ArrayList<T> createArrayList(T... params){
		ArrayList<T> list = new ArrayList<>();
		for (T object: params){
			list.add(object);
		}
		return list;
	}
}
