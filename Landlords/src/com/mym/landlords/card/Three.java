package com.mym.landlords.card;

import java.util.ArrayList;

/**
 * 因为斗地主中三条可以带一个或一对，因此需要稍加判断。但为了程序的统一性，三条和其带的牌被统一放在
 * 从父类继承的 {@link CardType#cardList}字段中，新增一个 {@link #getAttachType()}方法用于判定带的牌型，
 * 但并不应该做其他用途。
 * 例如，有一个 QAAA 对象（三个A带一个Q），则在出牌的时候 {@link #getCardList()}会返回四张牌，
 * 而 {@link #getAttachType()} 会返回一个Single对象，其值是 Q. 
 * @author Muyangmin
 * @create 2015-3-24
 */
public final class Three extends CardType implements NonBombType{
	
	private CardType attachType = null;
	
	public Three(ArrayList<Card> list) {
		try {
			divideListAndCreate(list);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("wrong cards:" + list.toString());
		}
	}
	
	/**
	 * 尝试识别三条和它所带的卡牌类型。
	 * @param list
	 * @throws IllegalArgumentException
	 */
	private void divideListAndCreate(ArrayList<Card> list) throws IllegalArgumentException{
		if (list.size()<3 || list.size()>5){
			throw new IllegalArgumentException();
		}
		int cardValue=0, attachValue=0;
		ArrayList<Card> tempCardList = new ArrayList<>();
		ArrayList<Card> tempAttachList= new ArrayList<>();
		for (Card card: list){
			int currentValue = card.getValue();
			if (cardValue==0 || currentValue==cardValue){
				cardValue = currentValue;
				tempCardList.add(card);
			}
			else if (attachValue==0 || currentValue==attachValue) {
				attachValue=currentValue;
				tempAttachList.add(card);
			}
			//出现了第三种值
			else {
				throw new IllegalArgumentException();
			}
		}
		//检查元素的个数
		int tmpListSize = tempCardList.size();
		int tmpAttachSize = tempAttachList.size();
		if ( (tmpListSize==3 && (tmpAttachSize<3)) ){
			//do nothing
		}
		else if (tmpAttachSize==3 && (tmpListSize<3)){
			tempAttachList = tempCardList;
			tmpAttachSize = tmpListSize;
		}
		else{
			throw new IllegalArgumentException();
		}
		cardList = list;
		if (tmpListSize == 1) {
			this.attachType = new Single(tempCardList);
		} else if (tmpAttachSize == 2) {
			this.attachType = new Pair(tempCardList);
		}
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Three ").append(cardList);
		return builder.toString();
	}

	/**
	 * 获得所带的牌型，可能为null，Single或 Pair三种类型。
	 */
	public CardType getAttachType() {
		return attachType;
	}

	public void setAttachType(CardType attachType) {
		this.attachType = attachType;
	}
	
}