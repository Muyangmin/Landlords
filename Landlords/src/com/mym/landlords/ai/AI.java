package com.mym.landlords.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import com.mym.landlords.card.Bomb;
import com.mym.landlords.card.Card;
import com.mym.landlords.card.CardType;
import com.mym.landlords.card.Pair;
import com.mym.landlords.card.Rocket;
import com.mym.landlords.card.Single;
import com.mym.landlords.card.Straight;
import com.mym.landlords.card.Three;

/**
 * 处理游戏的AI逻辑。
 * <p>所有的字段和方法访问权限均为包级别访问，即外界不可见。方法会通过Player类进行封装，对外界透明。
 * 此外，所有的方法均不做为静态，主要是为了AI的定制化和多样化考虑，可以引入策略模式使得AI更灵活多变。</p>
 * @author Muyangmin
 * @create 2015-3-21
 */
final class AI {
	
	private static final String LOG_TAG = "AI";
	/**
	 * 保存关联的AI Player对象。
	 */
	private final Player bindPlayer;
	
	//package access
	AI(Player player){
		bindPlayer = player;
	}
	
	/**
	 * 按照牌张大小和牌中炸弹数量分析叫牌的分数。
	 * <p>基本算法如下:王炸记8分，大王4分，小王3分，每个2记2分，每个A记1分，炸弹每个记3分。
	 * 全部加起来后看最后得分，大于8分即可叫3分，大于5分可叫2分，大于3分可叫1分，否则不叫。</p>
	 * @return 返回0~3之间的某个数值。
	 */
	private final int callLandlord(ArrayList<Card> list){
		//force sort 
		Collections.sort(list);
		int evaluateScore = 0;
		int size = list.size();
		//判断王炸和单张大小王
		if ( list.get(size-1).getValue()==Card.CARD_VALUE_JOKER_B 
				&& list.get(size-2).getValue()==Card.CARD_VALUE_JOKER_S ){
			evaluateScore += 8;
		}
		else if ( list.get(size-1).getValue()==Card.CARD_VALUE_JOKER_B ){
			evaluateScore += 4;
		}
		else if ( list.get(size-1).getValue()==Card.CARD_VALUE_JOKER_S ){
			evaluateScore += 3;
		}
		//判断2和A/K的数量
		for (Card card:list){
			switch (card.getValue()){
				case Card.CARD_VALUE_2: evaluateScore += 2; break;
				case Card.CARD_VALUE_A: evaluateScore += 1; break;
				case Card.CARD_VALUE_K: evaluateScore += 1; break;
				default: //do nothing
				break;
			}
		}
		//TODO add bomb count;
		
		Log.d(LOG_TAG, "evaluateScore="+evaluateScore);
		
		if ( evaluateScore > 8){
			return Game.BASIC_SCORE_THREE;
		}
		else if (evaluateScore > 5){
			return Game.BASIC_SCORE_TWO;
		}
		else if (evaluateScore > 3){
			return Game.BASIC_SCORE_ONE;
		}
		else{
			return Game.BASIC_SCORE_NONE;
		}
	}
	
	/**
	 * 返回叫牌信息。
	 * @param cards 手牌列表。
	 * @param minScore 最低分（不包含）。这个分数通常是上一个玩家叫的分数。
	 * @return 返回一个比minScore更大的分数，或者 是{@link Game#BASIC_BASIC_SCORE_NONE}，表示不叫。
	 */
	protected final int callLandlord(ArrayList<Card> cards, int minScore) {
		int alalysis = callLandlord(cards);
		return alalysis > minScore ? alalysis : Game.BASIC_SCORE_NONE;
	}
	
	protected CardType followCards(CardType lastType){
		CardType decideType = null;
		ArrayList<CardType> cardTypes = bindPlayer.cardsInfo.cardTypes;
		if (lastType==null){
			//出第一手牌，从最小的打起
			decideType = (cardTypes.get(0));
			//如果是三条，则考虑是否带牌
			if (decideType instanceof Three){
				optimizeThreeAttachments((Three) decideType, cardTypes);
			}
		}
		else{
			//先判断是否需要跟牌
			if (!needToFollow(lastType)){
				decideType = null;
			}
			else{
				for (CardType type : cardTypes) {
					if (type.canAgainstType(lastType)) {
						decideType = type;
						// 如果是三条， 则检查出牌要带的类型，强制只能带一样的类型。
						if (decideType instanceof Three) {
							if (!forceAttachCards(((Three) lastType),
									((Three) decideType), cardTypes)) {
								// 无牌可带
								decideType = null;
							}
						}
						break;
					}
				}
			}//else block
		}
		return decideType;
	}
	
	//出牌前检查三条是否能带上单或对
	private void optimizeThreeAttachments(Three three,
			ArrayList<CardType> cardTypes) {
		for (CardType tp : cardTypes) {
			// XXX to be optimized: 2以上不带
			if (tp instanceof Single
					&& tp.getCardList().get(0).getValue() < Card.CARD_VALUE_2) {
				three.setAttachType(tp);
				break;
			} else if (tp instanceof Pair
					&& tp.getCardList().get(0).getValue() < Card.CARD_VALUE_2) {
				three.setAttachType(tp);
				break;
			}
			// 否则不带
		}
	}
	
	//强制带牌，用于跟牌时使用。
	private boolean forceAttachCards(Three before, Three current, 
			ArrayList<CardType> cardTypes){
		if (current.getAttachType()!=null){
			Log.w(LOG_TAG, "this object has already attach cards before.");
			return false;
		}
		CardType followType = before.getAttachType();
		if (followType==null){
			return true;
		}
		Class<? extends CardType> clz = followType.getClass();
		for (CardType tp : cardTypes) {
			if (tp.getClass().equals(clz)
					&& tp.getCardList().get(0).getValue() < Card.CARD_VALUE_2) {
				current.setAttachType(tp);
				return true;
			}
			// 否则不带
		}
		return false;
	}
	
	/**
	 * 
	 * 按照指定的pattern尝试组合出目标牌列表。 
	 * 例如传入参数为[3,3,3]，则将尝试找出三张点数为3的卡牌，并把这些按顺序加入一个列表然后返回。
	 * <p>
	 * 注意： <ul>
	 * <li>该方法的实现假定list是升序排列的。</li> </ul>
	 * </p>
	 * 
	 * @param targetPattern 目标数值模式。
	 * @param list 当前剩余卡牌列表。
	 * @return 如果能找出这样的卡牌，则返回该列表；否则返回 null。
	 */
	protected ArrayList<Card> takeoutCards(int[] targetPattern, ArrayList<Card> list){
		if (targetPattern==null || list==null){
			Log.w(LOG_TAG, "takecards return null due to null param.");
			return null;
		}
		int patternLength = targetPattern.length;
		int cardLength = list.size();
		if (patternLength > cardLength){
			Log.d(LOG_TAG, "takecards return null due to no enough length.");
			return null;
		}
		ArrayList<Card> targetList = new ArrayList<>();
		//创建临时复制数组，然后从中迭代遍历，如果匹配则加入目标列表，并从临时复制数组中删除迭代的元素。
		//最后检查结果，如果目标列表长度合适则返回，否则返回null。
		ArrayList<Card> internalTempList = new ArrayList<>(list);
		Iterator<Card> iterator = internalTempList.iterator();
		int matchIndex = 0;
		while (iterator.hasNext()){
			Card card = iterator.next();
			if (card.getValue()== targetPattern[matchIndex]){
				targetList.add(card);
				iterator.remove();
				matchIndex++;
				//如果已经到了要出牌的张数，则终止循环
				if (matchIndex == targetPattern.length){
					break;
				}
			}
		}
		//手动清除列表内容，方便GC
		internalTempList.clear();
		if (targetList.size() != targetPattern.length){
			targetList.clear();
			Log.v(LOG_TAG, "takecards: pattern="+Arrays.toString(targetPattern)+ ", not found");
			return null;
		}
		Log.d(LOG_TAG, "takecards: pattern="+Arrays.toString(targetPattern)+ ", res="+targetList.toString());
		return targetList;
	}
	
	/**
	 * 将手牌按照一般原则进行组合。
	 * @param list 手牌列表
	 * @return 返回封装后的 PlayerCardsInfo 对象，其中的 cardTypes字段保证不为null且已进行过排序。
	 */
	protected PlayerCardsInfo makeCards(final List<Card> list){
		if (list==null || list.size()==0){
			return null;
		}
		//复制一个列表以便内部操作，避免直接操纵玩家手牌。
		PlayerCardsInfo playerInfo = new PlayerCardsInfo();
		ArrayList<Card> cloneList= new ArrayList<>(list);
		Collections.sort(cloneList);
		//判断王炸是否存在。
		ArrayList<Card> rocket = takeoutCards(new int[] {
				Card.CARD_VALUE_JOKER_S, Card.CARD_VALUE_JOKER_B }, cloneList);
		if (rocket!=null){
			playerInfo.cardTypes.add(new Rocket(rocket));
			cloneList.removeAll(rocket);
		}
		//找出所有的炸弹。通常情况下，炸弹也是不会进行拆分处理的。
		//注意：这里使用lastNotFoundValue跳过相同点数的数值，避免无谓的循环。
		for (int i = 0, lastNotFoundValue=0; i < cloneList.size(); i++) {
			int cardValue = cloneList.get(i).getValue();
			if (lastNotFoundValue==cardValue){
				continue ;
			}
			int[] bombPattern = new int[] { cardValue, cardValue, cardValue,
					cardValue };
			ArrayList<Card> bombs = takeoutCards(bombPattern, cloneList);
			if (bombs != null) {
				playerInfo.cardTypes.add(new Bomb(bombs));
				cloneList.removeAll(bombs);
			}
			else{
				lastNotFoundValue = cardValue;
			}
		}
		//找出所有的顺子
		List<StraightNumbers> numbers = StraightAnalyst.getAllStraights(cloneList);
		ArrayList<Straight> straights = new ArrayList<>();
		for (StraightNumbers num : numbers){
			ArrayList<Card> tempList = takeoutCards(num.asIntegerArray(), cloneList);
			if (tempList != null){
				Straight tempStr = new Straight(tempList);
				straights.add(tempStr);
				cloneList.removeAll(tempList);
			}
		}
		playerInfo.cardTypes.addAll(straights);
		
		//找出所有的三条
		for (int i = 0, lastNotFoundValue=0; i < cloneList.size(); i++) {
			int cardValue = cloneList.get(i).getValue();
			if (lastNotFoundValue==cardValue){
				continue ;
			}
			int[] threePattern = new int[] { cardValue, cardValue, cardValue};
			ArrayList<Card> threes = takeoutCards(threePattern, cloneList);
			if (threes != null) {
				playerInfo.cardTypes.add(new Three(threes));
				cloneList.removeAll(threes);
			}
			else{
				lastNotFoundValue = cardValue;
			}
		}
		//找出所有的对子
		//对子无需使用lastNotFound来优化
		for (int i = 0; i < cloneList.size(); i++) {
			int cardValue = cloneList.get(i).getValue();
			int[] threePattern = new int[] { cardValue, cardValue};
			ArrayList<Card> pairs = takeoutCards(threePattern, cloneList);
			if (pairs != null) {
				playerInfo.cardTypes.add(new Pair(pairs));
				cloneList.removeAll(pairs);
			}
		}
		//剩下的都是单牌
		Iterator<Card> singleIterator = cloneList.iterator();
		while (singleIterator.hasNext()){
			playerInfo.cardTypes.add(new Single(singleIterator.next()));
			singleIterator.remove();
		}
		statPlayerCardsInfo(playerInfo);
		Collections.sort(playerInfo.cardTypes, CardType.SORT_COMPARATOR);
		return playerInfo;
	}
	
	//判断是否需要跟牌
	private boolean needToFollow(CardType cardsToFollow){
		//如果自己是地主，则无需考虑，有牌必打
		if (bindPlayer.isLandlord()){
			return true;
		}
		//如果自己是地主的上家, 默认出牌
		if (bindPlayer.isPriorOfLandlord){
			CardType partnerCards = bindPlayer.getPriorPlayer().getLastCards();
			//搭档没有打,则跟牌
			if (partnerCards==null){
				return true;
			}
			//如果搭档跟了牌且牌比较大，则不跟牌
			if ((cardsToFollow instanceof Single 
					&& partnerCards.getCardList().get(0).getValue() >= Card.CARD_VALUE_A)
					|| (cardsToFollow instanceof Pair 
					&& partnerCards.getCardList().get(0).getValue() >= Card.CARD_VALUE_K)) {
				return false;
			}
			//如果跟的牌是炸弹或顺子，不跟牌
			if (partnerCards instanceof Bomb
					|| partnerCards instanceof Straight
					|| partnerCards instanceof Rocket) {
				return false;
			}
			return true;
		}
		//如果自己是地主的下家
		if (bindPlayer.isNextOfLandlord){
			CardType landCards = bindPlayer.getPriorPlayer().getLastCards();
			//如果地主没有出牌，则不出
			if (landCards==null){
				return false;
			}
			//如果是炸弹或顺子，有牌必跟（不需要判断王炸）
			if (landCards instanceof Bomb || landCards instanceof Straight){
				return true;
			}
			//搭档上次没有跟牌，则打出
			if (bindPlayer.getNextPlayer().getLastCards()==null){
				return true;
			}
			//如果有独立的无需拆牌就能打的，则打出
			ArrayList<CardType> cardTypes = bindPlayer.cardsInfo.cardTypes;
			for (CardType type: cardTypes){
				if (type.getClass().equals(cardsToFollow)){
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	private void statPlayerCardsInfo(PlayerCardsInfo info){
		//force init
		info.hasRocket = false;
		info.bombCount = 0;
		info.pairCount = 0;
		info.singleCount = 0;
		info.threeCount = 0;
		info.straightCount=0;
		
		info.expectedRound = info.cardTypes.size();
		
		for (CardType type: info.cardTypes){
			if (type instanceof Rocket){
				info.hasRocket = true;
			}
			else if (type instanceof Bomb){
				info.bombCount++;
			}
			else if (type instanceof Straight){
				info.straightCount++;
			}
			else if (type instanceof Three){
				info.threeCount++;
			}
			else if (type instanceof Single){
				info.singleCount++;
			}
			else if (type instanceof Pair){
				info.pairCount++;
			}
		}
	}
	
}
