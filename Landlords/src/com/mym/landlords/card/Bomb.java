package com.mym.landlords.card;

import java.util.ArrayList;

public final class Bomb extends CardType implements BombType{
	
	public Bomb(ArrayList<Card> list){
		if (list==null || list.size()!=4 || (!list.get(0).isSameValueAs(list.get(1)))
			|| (!list.get(1).isSameValueAs(list.get(2)))
			|| (!list.get(2).isSameValueAs(list.get(3)))){
			throw new IllegalArgumentException("this type must be 4 same cards!");
		}
		cardList = list;
	}
	
	@Override
	public int compareTo(CardType another){
		int superCompare = super.compareTo(another);
		if (superCompare != UNDEFINED_COMPARE){
			return superCompare;
		}
		if (another instanceof Rocket){
			return -1;
		}
		if ( !(another instanceof Bomb) ){
			throw new ClassCastException("compare to wrong type:" + another.getClass().getSimpleName());
		}
		return cardList.get(0).compareTo(((Bomb)another).cardList.get(0));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Bomb ").append(cardList);
		return builder.toString();
	}
}