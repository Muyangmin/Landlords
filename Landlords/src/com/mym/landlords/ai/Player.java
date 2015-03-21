package com.mym.landlords.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mym.landlords.ai.Game.Status;
import com.mym.landlords.card.Card;
import com.mym.landlords.card.HandCard;

/**
 * 代表玩家的实体类。
 * 
 * @author Muyangmin
 * @create 2015-3-18
 */
public final class Player {
	private ArrayList<HandCard> handCards; // 手牌列表
	private boolean isLandlord; // 是否是地主
	private boolean isAiPlayer; // 是否是AI玩家
	private Player priorPlayer; // 上手玩家
	private Player nextPlayer; // 下手玩家
	private ArrayList<Card> lastCards; // 出的最后一手牌，用于AI判断和逻辑控制
	private int calledScore = Integer.MIN_VALUE;// 叫的分数, Integer.MIN_VALUE表示未赋值

	/**
	 * 创建一个新的AI玩家实例。
	 */
	public static final Player newAiPlayer() {
		return new Player(true);
	}

	/**
	 * 创建一个非AI玩家实例。
	 */
	public static final Player newHumanPlayer() {
		return new Player(false);
	}

	private Player(boolean isAi) {
		isAiPlayer = isAi;
	}

	public int getCalledScore() {
		return calledScore;
	}

	public ArrayList<HandCard> getHandCards() {
		return handCards;
	}

	public ArrayList<Card> getLastCards() {
		return lastCards;
	}

	public Player getNextPlayer() {
		return nextPlayer;
	}

	public Player getPriorPlayer() {
		return priorPlayer;
	}

	/**
	 * 打出卡牌。
	 */
	protected final void giveCard(List<Card> toRemove) {

	}

	public boolean isAiPlayer() {
		return isAiPlayer;
	}

	public boolean isLandlord() {
		return isLandlord;
	}

	/**
	 * 该方法使玩家进行下一个回合。可能使用到的输入有当前的游戏状态、上一个玩家的操作等。
	 * 
	 * @param currentGame
	 *            当前正在进行的游戏实例。如果为null或处于游戏结束后的某个阶段，则会抛出异常。
	 */
	public synchronized void nextRound(Game currentGame) {
		if (currentGame == null || currentGame.status == Status.ShowingAICards
				|| currentGame.status == Status.Gameover) {
			throw new IllegalArgumentException("invalid game instance.");
		}
		if (currentGame.status == Status.CallingLandlord) {
			if (calledScore != Integer.MIN_VALUE) {// 已经计算过，不再计算
				return;
			}
			int last = priorPlayer.getCalledScore();
			calledScore = AI.callLandlord(handCards,
					last == Integer.MIN_VALUE ? 0 : last);// 如果是第一家，则只需要大于0就可以
			return;
		}
	}

	/**
	 * 设置手牌并自动排序。
	 * 
	 * @param handCards
	 *            手牌列表，不能为null。
	 */
	public void setHandCards(ArrayList<HandCard> handCards) {
		if (handCards == null) {
			throw new RuntimeException("handCards cannot be null.");
		}
		this.handCards = handCards;
		Collections.sort(this.handCards);
	}

	/**
	 * 设置手牌并自动排序。
	 * 
	 * @param cards
	 *            卡牌列表，不能为null。
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
	 * 设置为地主并将底牌加入手中。
	 * 
	 * @param awardCards
	 *            底牌列表，不能为null。
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
	 * 
	 * @param prior
	 *            上家（左方）
	 * @param next
	 *            下家（右方）
	 */
	public void setSeat(Player prior, Player next) {
		this.priorPlayer = prior;
		this.nextPlayer = next;
	}
}
