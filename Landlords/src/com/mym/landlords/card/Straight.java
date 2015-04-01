package com.mym.landlords.card;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 顺子牌型。
 * @author Muyangmin
 * @create 2015-3-27
 */
public class Straight extends CardType implements NonBombType{
	
	private int startValue;
	public final int length;
	
	public Straight(ArrayList<Card> list){
		if (list == null || (!canMakeupStraight(list))) {
			throw new IllegalArgumentException(
					"A straight must be made up of more than 5 continuous value."
							+ list.toString());
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
		Iterator<Card> iterator = list.iterator();
		//设定起始数值。由于list的长度在前面已经做过检验，因此这里的第一次 next操作一定是安全的。 
		int lastValue = iterator.next().getValue(); 
		while (iterator.hasNext()){
			int currentValue = iterator.next().getValue();
			if (currentValue != lastValue+1){
				res = false;
				break;
			}
			lastValue = currentValue;//更新迭代比较变量
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
	
	@Override
	protected boolean isSameConcreteSubclass(CardType another) {
		if (another instanceof Straight){
			return length == ((Straight)another).length;
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Straight [startValue=").append(startValue)
				.append(", length=").append(length).append(", cardList=")
				.append(cardList).append("]");
		return builder.toString();
	}
	
}