package com.mym.landlords.card;

import java.util.ArrayList;

/**
 * 顺子牌型。
 * @author Muyangmin
 * @create 2015-3-27
 */
public class Straight extends CardType implements NonBombType{
	
	private int startValue;
	public final int length;
	
	public Straight(ArrayList<Card> list){
		if (list==null || (!canMakeupStraight(list))){
			throw new IllegalArgumentException(
					"A straight must be made up of more than 5 continuous value.");
		}
		this.cardList = list;
		this.startValue = list.get(0).getValue();
		this.length = list.size();
	}
	private final boolean canMakeupStraight(ArrayList<Card> list){
		int listSize = list.size();
		if (listSize < 5){
			return false;
		}
		boolean res = true;
		int lastValue = list.get(0).getValue();
		for (int i=1; i<listSize ; i++){	//从下标1开始，第一个元素无需比较
			if (list.get(i).getValue() != lastValue+1){
				res = false;
				break;
			}
		}
		return res;
	}
	@Override
	public int compareTo(CardType another){
		int superCompare = super.compareTo(another);
		if (superCompare != UNDEFINED_COMPARE){
			return superCompare;
		}
		return Integer.valueOf(startValue).compareTo(
				((Straight) another).startValue);
	}
	
}