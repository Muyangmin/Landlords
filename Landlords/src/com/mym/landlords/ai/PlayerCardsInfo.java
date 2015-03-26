package com.mym.landlords.ai;

import java.util.ArrayList;

import com.mym.landlords.card.CardType;

/**
 * 存放玩家的手牌分析结果。
 * @author Muyangmin
 * @create 2015-3-26
 */
public final class PlayerCardsInfo {
	
	//hide accessibility
	protected PlayerCardsInfo() {}

	protected int singleCount = 0;
	protected int pairCount = 0;
	protected int threeCount = 0;
	protected int bombCount = 0;
	protected int twoAndJokerCount = 0;
	protected boolean hasRocket = false;
	protected int expectedRound = 0;
	protected ArrayList<CardType> cardTypes = new ArrayList<>();

	public int getSingleCount() {
		return singleCount;
	}

	public int getPairCount() {
		return pairCount;
	}

	public int getThreeCount() {
		return threeCount;
	}

	public int getBombCount() {
		return bombCount;
	}

	public int getTwoAndJokerCount() {
		return twoAndJokerCount;
	}

	public boolean isHasRocket() {
		return hasRocket;
	}

	public int getExpectedRound() {
		return expectedRound;
	}

	public ArrayList<CardType> getCardTypes() {
		return cardTypes;
	}
}
