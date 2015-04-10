package com.mym.landlords.card;

import java.util.ArrayList;

/**
 * @author Muyangmin
 * @create 2015-3-23
 */
public final class Single extends CardType implements NonBombType {
	
	public Single(Card card) {
		super();
		cardList = new ArrayList<>(1);
		cardList.add(card);
	}
	public Single(ArrayList<Card> cards) {
		super();
		if (cards==null || cards.size()>1){
			throw new IllegalArgumentException("a single type cannot contain more than 1 card.");
		}
		cardList = cards;
	}

	/**
	 * 实现单张牌牌型大小比对。
	 * @return 如果参数是炸弹，则始终返回负数；否则返回按点数的比较结果。
	 * @see com.mym.landlords.card.CardType#compareTo(com.mym.landlords.card.CardType)
	 */
	@Override
	public int compareTo(CardType another){
		int superCompare = super.compareTo(another);
		if (superCompare!=UNDEFINED_COMPARE){
			return superCompare;
		}
		if (!(another instanceof Single)){
			throw new ClassCastException("compare to wrong object.");
		}
		return cardList.get(0).compareTo(
				((Single) another).cardList.get(0));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Single ").append(cardList);
		return builder.toString();
	}
}
