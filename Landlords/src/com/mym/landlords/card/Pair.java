package com.mym.landlords.card;

import java.util.ArrayList;

/**
 * @author Muyangmin
 * @create 2015-3-23
 */
public final class Pair extends CardType implements NonBombType {
	public final ArrayList<Card> cardList;

	public Pair(ArrayList<Card> list) {
		if (list == null || list.size() != 2
				|| (list.get(0).compareTo(list.get(1)) != 0)) {
			throw new IllegalArgumentException("A pair must be 2 same cards!");
		}
		cardList = list;
	}

	@Override
	public int compareTo(CardType another) {
		int superCompare = super.compareTo(another);
		if (superCompare != UNDEFINED_COMPARE) {
			return superCompare;
		}
		if (!(another instanceof Single)) {
			throw new ClassCastException("compare to wrong object.");
		}
		return cardList.get(0).compareTo(((Pair) another).cardList.get(1));
	}
}
