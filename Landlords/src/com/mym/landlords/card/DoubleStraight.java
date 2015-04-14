package com.mym.landlords.card;

import java.util.ArrayList;
import java.util.Collections;

import com.mym.util.LangUtils;

/**
 * 双顺（连对）牌型。
 * 
 * @author Muyangmin
 * @create 2015-4-14
 */
public class DoubleStraight extends CardType implements NonBombType {

	private ArrayList<Pair> pairs;	//对子的列表
	private final int startValue;	//最小的对子的数值
	/**
	 * 这个双顺所包含的对子数目。
	 */
	public final int length;

	public DoubleStraight(ArrayList<Card> list) {
		pairs = dividePairs(list);
		if (pairs == null) {
			throw new IllegalArgumentException(
					"cannot make up double straight:" + list.toString());
		}
		//保护性复制
		cardList = new ArrayList<>(list);
		length = pairs.size();
		startValue = cardList.get(0).getValue();
	}

	// 分析连对，如果能组成连对，则返回对子的顺序列表，否则返回null。
	private ArrayList<Pair> dividePairs(ArrayList<Card> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		int listSize = list.size();
		if (listSize<6 || listSize%2==1){
			return null;
		}
		Collections.sort(list, Card.COMPARATOR_WITH_SUIT);
		ArrayList<Pair> pairs = new ArrayList<>();
		for (int i = 0, lastValue=0; i < listSize; i += 2) {
			Card card1 = list.get(i);
			Card card2 = list.get(i + 1);
			if (card1.isSameValueAs(card2) 				//两张一样大
					&& card1.getValue()<Card.CARD_VALUE_2	//2以上不能组成连对
					&& (lastValue==0 || card1.getValue()==lastValue+1)	//与前一个对子是连续的
					) {
				lastValue = card1.getValue();
				pairs.add(new Pair(LangUtils.createList(card1,card2)));
			} else {
				break;
			}
		}
		if (pairs.size() == listSize / 2) {
			return pairs;
		}
		return null;
	}
	
	@Override
	public int compareTo(CardType another) {
		int superCompare = super.compareTo(another);
		if (superCompare != UNDEFINED_COMPARE){
			return superCompare;
		}
		return Integer.valueOf(startValue).compareTo(
				((DoubleStraight) another).startValue);
	}
	
	@Override
	protected boolean isSameConcreteSubclass(CardType another) {
		if (another instanceof DoubleStraight){
			return length == ((DoubleStraight)another).length;
		}
		return false;
	}

	public ArrayList<Pair> getPairs() {
		return pairs;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DoubleStraight [pairs=").append(pairs)
				.append(", startValue=").append(startValue).append(", length=")
				.append(length).append(", cardList=").append(cardList)
				.append("]");
		return builder.toString();
	}

}
