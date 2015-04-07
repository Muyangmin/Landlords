package com.mym.landlords.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

import com.mym.landlords.ai.Game.Status;
import com.mym.landlords.card.Card;
import com.mym.landlords.card.CardType;

/**
 * 代表玩家的实体类。
 * 
 * @author Muyangmin
 * @create 2015-3-18
 */
public final class Player {
	private String playerName;			//玩家名称
	private ArrayList<Card> handCards; 	// 手牌列表
	private boolean isLandlord;			// 是否是地主
	private boolean isAiPlayer; 		// 是否是AI玩家
	private Player priorPlayer; 		// 上手玩家
	private Player nextPlayer; 			// 下手玩家
	private CardType lastCards;			//出的最后一手牌，用于AI判断和逻辑控制
	private int calledScore = Integer.MIN_VALUE;// 叫的分数, Integer.MIN_VALUE表示未赋值
	
	/* 以下部分为 AI需要用到的属性 */
	private AI aiRobot;					//机器AI
	protected PlayerCardsInfo cardsInfo;	//卡牌分析结果
	/** 是否为地主的上家。 */
	protected boolean isPriorOfLandlord = false;
	/** 是否为地主的下家。 */
	protected boolean isNextOfLandlord = false;

	/**
	 * 创建一个新的AI玩家实例。
	 * @param name 玩家名称。
	 */
	public static final Player newAiPlayer(String name) {
		Player player = new Player(true, name == null ? "" : name);
		player.aiRobot = new AI(player);
		return player;
	}

	/**
	 * 创建一个非AI玩家实例。
	 * @param name 玩家名称
	 */
	public static final Player newHumanPlayer(String name) {
		return new Player(false, name);
	}

	private Player(boolean isAi, String name) {
		isAiPlayer = isAi;
		playerName = name;
	}

	public int getCalledScore() {
		return calledScore;
	}

	public ArrayList<Card> getHandCards() {
		return handCards;
	}

	public CardType getLastCards() {
		return lastCards;
	}
	
	/**
	 * 打出卡牌。参数为null表示不出，但也将清空上一手牌的记录。
	 * @param type 要打出的当前一手牌。
	 */
	public final synchronized void giveOutCards(CardType type){
		lastCards = type;
		if (type!=null){
			Log.d(playerName, "giveoutcard:"+type);
			handCards.removeAll(lastCards.getCardList());
			//移除相关牌型
			if (isAiPlayer){
				cardsInfo.cardTypes.remove(type);
			}
		}
	}

	public Player getNextPlayer() {
		return nextPlayer;
	}

	public Player getPriorPlayer() {
		return priorPlayer;
	}
	
	public boolean isAiPlayer() {
		return isAiPlayer;
	}

	public boolean isLandlord() {
		return isLandlord;
	}
	
	private final void checkAiPlayer(){
		if (!isAiPlayer){
			throw new RuntimeException("This method should not be invoked from a non-ai player.");
		}
	}
	
	/**
	 * 执行叫地主操作。最终得出的分数要使用 {@link #getCalledScore()}方法获得。
	 * @param minScore 地主最低分数（通常是本局前两位喊出来的）。
	 */
	public synchronized final void callLandlord(int minScore){
		checkAiPlayer();
		calledScore = aiRobot.callLandlord(handCards, minScore >= 0 ? minScore : 0);
	}
	
	private final PlayerCardsInfo makeCards(){
		checkAiPlayer();
		PlayerCardsInfo playerInfo =  aiRobot.makeCards(handCards);
		Log.d(playerName, "Final playerInfo:"+playerInfo);
		return playerInfo;
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
	}
	
	/**
	 * 执行出牌或跟牌策略。
	 * @param lastType 场上最后打出的卡牌牌型对象。当参数为null时表示自由出牌。
	 * @return 返回一个合适的对象，该对象一定是逻辑合法（即大于参数牌型）的；或者为null。
	 */
	public CardType followCards(CardType lastType){
		checkAiPlayer();
		return aiRobot.followCards(lastType);
	}
	

	/**
	 * 设置手牌并自动排序。
	 * @param handCards 手牌列表，不能为null。
	 */
	public void setHandCards(List<Card> handCards) {
		if (handCards == null) {
			throw new RuntimeException("handCards cannot be null.");
		}
		this.handCards = new ArrayList<>(handCards.size());
		this.handCards.addAll(handCards);
		Collections.sort(this.handCards);
		if (isAiPlayer){
			this.cardsInfo = makeCards();
		}
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
		getPriorPlayer().isPriorOfLandlord = true;
		getNextPlayer().isNextOfLandlord = true;
		
		//实现底牌加入手中时的选中状态
		for (Card card : this.handCards){
			card.setPicked(false);
		}
		for (Card card : awardCards){
			card.setPicked(true);
			this.handCards.add(card);
		}
		Collections.sort(this.handCards);
		
		//对于 AI，重新组合手牌
		if (isAiPlayer){
			this.cardsInfo.recycle();
			this.cardsInfo = makeCards();
		}

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

	public void setCalledScore(int calledScore) {
		this.calledScore = calledScore;
	}
	public String getPlayerName() {
		return playerName;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Player [").append(playerName)
				.append(isAiPlayer ? ", isAiPlayer." : "").append("]");
		return builder.toString();
	}
	
}
