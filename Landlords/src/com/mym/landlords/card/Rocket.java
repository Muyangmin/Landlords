package com.mym.landlords.card;

import java.util.ArrayList;

public final class Rocket extends CardType implements BombType{
	public Rocket(ArrayList<Card> list){
		if (list==null || list.size()!=2 || list.get(0).getSuit()!= CardSuit.Joker 
			|| list.get(1).getSuit()!= CardSuit.Joker){
			throw new IllegalArgumentException("this type must be 2 cards in joker suit!");
		}
		//保护性复制
		cardList = new ArrayList<>(list);
	}
	
	/**
	 * 该方法将总是返回1。
	 */
	@Override
	public int compareTo(CardType another){
		return 1;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Rocket");
		return builder.toString();
	}
}