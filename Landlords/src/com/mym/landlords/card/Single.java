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

	@Override
	public int compareTo(CardType another){
		int superCompare = super.compareTo(another);
		if (superCompare!=UNDEFINED_COMPARE){
			return superCompare;
		}
		if (!(another instanceof Single)){
			throw new ClassCastException("compare to wrong object.");
		}
		return card.compareTo(((Single)another).card);
	}
}
