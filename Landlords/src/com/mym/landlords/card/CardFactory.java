package com.mym.landlords.card;

import java.util.ArrayList;

/**
 * 卡牌工厂类，负责卡牌的印制\(^o^)/。
 * @author Muyangmin
 * @create 2015-3-14
 */
public final class CardFactory {
	
	/**
	 * 获得一副新牌。
	 * @return 返回的卡牌按花色排列，大小王在最后。
	 */
	public static ArrayList<Card> newCardPack(){
		ArrayList<Card> cards = new ArrayList<>();
		for (int i=Card.CARD_VALUE_3; i<=Card.CARD_VALUE_2; i++){
			cards.add(new Card(CardType.Spade, i));
		}
		for (int i=Card.CARD_VALUE_3; i<=Card.CARD_VALUE_2; i++){
			cards.add(new Card(CardType.Heart, i));
		}
		for (int i=Card.CARD_VALUE_3; i<=Card.CARD_VALUE_2; i++){
			cards.add(new Card(CardType.Club, i));
		}
		for (int i=Card.CARD_VALUE_3; i<=Card.CARD_VALUE_2; i++){
			cards.add(new Card(CardType.Diamond, i));
		}
		cards.add(new Card(CardType.Joker, Card.CARD_VALUE_JOKER_S));
		cards.add(new Card(CardType.Joker, Card.CARD_VALUE_JOKER_B));
		return cards;
	}
}
