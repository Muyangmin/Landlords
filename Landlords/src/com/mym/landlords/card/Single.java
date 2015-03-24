package com.mym.landlords.card;

/**
 * @author Muyangmin
 * @create 2015-3-23
 */
public final class Single extends CardType implements NonBombType {
	public final Card card;
	
	public Single(Card card) {
		super();
		this.card = card;
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
		return card.compareIgnoreSuit(((Single)another).card);
	}
}
