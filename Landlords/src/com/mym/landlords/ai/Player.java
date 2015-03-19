package com.mym.landlords.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mym.landlords.card.Card;
import com.mym.landlords.card.HandCard;

/**
 * 代表玩家的实体类。
 * @author Muyangmin
 * @create 2015-3-18
 */
public final class Player {
	private ArrayList<HandCard> handCards;	//手牌列表
	private boolean isLandlord;			//是否是地主
	private Player priorPlayer; 		//上手玩家
	private Player nextPlayer;			//下手玩家
	
	public ArrayList<HandCard> getHandCards() {
		return handCards;
	}
	/**
	 * 设置手牌并自动排序。
	 * @param cards 卡牌列表，不能为null。
	 */
	public void setHandCards(List<Card> cards) {
		if (cards == null) {
			throw new RuntimeException("handCards cannot be null.");
		}
		if (handCards == null) {
			handCards = new ArrayList<>();
		} else {
			handCards.clear();
		}
		for (Card card : cards) {
			handCards.add(new HandCard(card));
		}
		Collections.sort(handCards);
	}
	
	/**
	 * 设置手牌并自动排序。
	 * @param handCards 手牌列表，不能为null。
	 */
	public void setHandCards(ArrayList<HandCard> handCards) {
		if (handCards ==null){
			throw new RuntimeException("handCards cannot be null.");
		}
		this.handCards = handCards;
		Collections.sort(this.handCards);
	}
	
	public boolean isLandlord() {
		return isLandlord;
	}
	/**
	 * 设置为地主并将底牌加入手中。
	 * @param awardCards 底牌列表，不能为null。
	 */
	public void setLandlord(List<Card> awardCards) {
		if (awardCards == null) {
			throw new RuntimeException("awardCards cannot be null.");
		}
		this.isLandlord = true;
		for (Card card : awardCards) {
			handCards.add(new HandCard(card));
		}
		Collections.sort(this.handCards);
	}
	
	/**
	 * 设置座位。
	 * @param prior 上家（左方）
	 * @param next 下家（右方）
	 */
	public void setSeat(Player prior, Player next){
		this.priorPlayer = prior;
		this.nextPlayer = next;
	}
}
