package com.mym.landlords.card;

import java.util.ArrayList;

/**
 * @author Muyangmin
 * @create 2015-3-24
 */
public class Three extends CardType implements NonBombType{
	
	public Three(ArrayList<Card> list) {
		if (list == null || list.size() != 3
				|| (!(list.get(0).isSameValueAs(list.get(1))))
				|| (!(list.get(1).isSameValueAs(list.get(2))))) {
			throw new IllegalArgumentException(
					"this type must be 3 same cards!");
		}
		cardList = list;
	}
	
	/**
	 * 实现三张牌型大小比对。
	 * @return 如果参数是炸弹，则始终返回负数；否则返回按点数的比较结果。
	 * @see com.mym.landlords.card.CardType#compareTo(com.mym.landlords.card.CardType)
	 */
	@Override
	public int compareTo(CardType another){
		int superCompare = super.compareTo(another);
		if (superCompare != UNDEFINED_COMPARE){
			return superCompare;
		}
		if (!(another instanceof Three)) {
			throw new ClassCastException("compare to wrong type:"
					+ another.getClass().getSimpleName());
		}
		return cardList.get(0).compareTo(((Three)another).cardList.get(1));
	}
}