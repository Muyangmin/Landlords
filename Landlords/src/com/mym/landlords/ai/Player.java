package com.mym.landlords.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mym.landlords.card.Card;

/**
 * 代表玩家的实体类。
 * @author Muyangmin
 * @create 2015-3-18
 */
public final class Player {
	private ArrayList<Card> handCards;	//手牌列表
	private boolean isLandlord;			//是否是地主
	private Player priorPlayer; 		//上手玩家
	private Player nextPlayer;			//下手玩家
	
	public ArrayList<Card> getHandCards() {
		return handCards;
	}
	/**
	 * 设置手牌并自动排序。
	 * @param handCards 手牌列表
	 */
	public void setHandCards(ArrayList<Card> handCards) {
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
	 * @param awardCards 底牌列表
	 */
	public void setLandlord(List<Card> awardCards) {
		if (awardCards ==null){
			throw new RuntimeException("awardCards cannot be null.");
		}
		this.isLandlord = true;
		handCards.addAll(awardCards);
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
