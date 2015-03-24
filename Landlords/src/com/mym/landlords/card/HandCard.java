package com.mym.landlords.card;
//
///**
// * 代表玩家手中的手牌。
// * <p>
// * 从客观现实而言，纸牌和手牌在材料上并无区别；但从程序流程来讲，卡牌本身是无属性的，加入手牌后会有选中状态等属性。
// * 并且每局游戏完成后，卡牌并不会重新印刷，而是利用原来的卡牌，但是手牌的这些属性是每次必须重新初始化的。
// * </p>
// * <p>
// * 从面向对象设计方法而言，Card类作为单纯的物体实体类，不应该掺杂业务和逻辑方法。而HandCard类就不是如此。
// * </p>
// * 
// * @author Muyangmin
// * @create 2015-3-19
// */
//public final class HandCard implements Comparable<HandCard> {
//	private Card card;
//	private boolean isPicked; // 该卡牌是否被选中
//
//	public HandCard(Card card) {
//		this.card = card;
//		isPicked = false;
//	}
//
//	public Card getCard() {
//		return card;
//	}
//
//	public boolean isPicked() {
//		return isPicked;
//	}
//
//	public void setPicked(boolean isPicked) {
//		this.isPicked = isPicked;
//	}
//
//	@Override
//	public int compareTo(HandCard another) {
//		return card.compareTo(another.card);
//	}
//
//	@Override
//	public String toString() {
//		StringBuilder builder = new StringBuilder();
//		builder.append("HandCard [").append(card.getSuit()).append(" ")
//				.append(card.getValue()).append(isPicked ? ",picked" : "")
//				.append("]");
//		return builder.toString();
//	}
//}
