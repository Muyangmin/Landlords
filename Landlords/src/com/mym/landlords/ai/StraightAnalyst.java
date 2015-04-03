package com.mym.landlords.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import com.mym.landlords.card.Card;
import com.mym.landlords.card.Straight;

//package access
final class StraightAnalyst {
	
	private static final String LOG_TAG = "StraightAnalyst";
	
	/**
	 * 找出所有能组成顺子的数值组合。
	 * @param cardList 卡牌列表。
	 * @return 顺子数值列表
	 */
	protected static List<StraightNumbers> getAllStraights(
			ArrayList<Card> cardList) {
//		Log.d(LOG_TAG, "original list:"+cardList.toString());
		//transfer int array
		ArrayList<Integer> leftCardValues = new ArrayList<>();
		for (Card card: cardList){
			//过滤2和王
			if (card.getValue() < Card.CARD_VALUE_2){
				leftCardValues.add(card.getValue());	
			}
		}
//		Log.d(LOG_TAG, "leftCardValue:" + leftCardValues.toString());
		List<StraightNumbers> shortList = getShortestStraightList(leftCardValues);
//		Log.d(LOG_TAG, "shortest:"+shortList.toString());
		extendStraight(shortList, leftCardValues);
//		Log.d(LOG_TAG, "after extends:"+shortList.toString());
		concatPossibleStraights(shortList);
		Log.d(LOG_TAG, "final concat:"+shortList.toString());
		return shortList;
	}
	
	/**
	 * 强行凑出对子。
	 * @param follow 要跟牌的对子
	 * @param cards 如果无牌可出，则返回null;否则返回一个所有可能点数情况的列表，这些对子的点数彼此可能有所重叠。
	 * @return
	 */
	protected static ArrayList<Straight> forceGetStraights(Straight follow, ArrayList<Card> cards){
		if (follow==null || cards.size()< follow.length){
			return null;
		}
		ArrayList<Straight> forceStraights = new ArrayList<>();
		Card lastCard = null;
		int size = cards.size();
		ArrayList<Card> tempCards = new ArrayList<>(); 
		for (int i=0; i<size-follow.length; i++){
			lastCard=null;
			tempCards.clear();
			//一定比
			for (int j=0; i+j < size ; j++){
				Card thisCard = cards.get(i+j);
				if (thisCard.getValue()>=Card.CARD_VALUE_2){
					break;
				}
				if (lastCard==null){
					tempCards.add(thisCard);
					continue;
				}
				int increment = thisCard.getValue()-lastCard.getValue();
				if (increment==0){
					continue;
				}
				else if (increment==1){
					tempCards.add(thisCard);
					lastCard = thisCard;
					if (tempCards.size()==follow.length){
						break;
					}
				}
				//大于1不会构成顺子。
				else {
					break;
				}
			}
			if (tempCards.size()==follow.length){
				forceStraights.add(new Straight(tempCards));
			}
		}
		return forceStraights;
	}

	/**
	 *  获取所有的最短顺子，即所有的五连。
	 * @param cardValues 剩余卡牌点数。
	 * @return 返回匹配成功的所有五连顺子列表。
	 */
	private static List<StraightNumbers> getShortestStraightList(
			ArrayList<Integer> cardValues) {
		List<StraightNumbers> strList = new ArrayList<>();
		StraightNumbers straight;
		while ((straight = getSmallestShortList(cardValues)) != null) {
			cardValues.removeAll(straight.numbers);
			strList.add(straight);
		}
		return strList;
	}

	/**
	 *  选取最小的五连，参数必须非递减有序。
	 * @param values 剩余牌值
	 * @return 一旦选取到5张组成的顺子则立即返回这5张牌组成的列表，否则返回null。
	 */
	private static StraightNumbers getSmallestShortList(ArrayList<Integer> values) {
		ArrayList<Integer> integers = new ArrayList<>();
		for (Integer current : values) {
			if (integers.size() == 0) {
				integers.add(current);
				continue;
			}
			int increment = current - integers.get(integers.size() - 1);
			// 如果相等，不做理会
			if (increment == 0) {
				continue;
			}
			// 如果增量恰好为1，则加入列表
			else if (increment == 1) {
				integers.add(current);
				if (integers.size() == 5) {
					break;
				}
			}
			// 如果增量大于1则已经无法组成顺子，清空暂存列表
			else {
				integers.clear();
				continue;
			}
		}// end of loop for
		return integers.size() >= 5 ? new StraightNumbers(integers) : null;
	}

	// 扩展所有五连，以使能拼接的单牌都拼接上。
	private static void extendStraight(List<StraightNumbers> strList,
			ArrayList<Integer> leftCards) {
		Iterator<StraightNumbers> strIter = strList.iterator();
		while (strIter.hasNext()) {
			StraightNumbers straight = strIter.next();
			if (leftCards.size()<=0){
				break ;
			}
			Iterator<Integer> numIter = leftCards.iterator();
			while (numIter.hasNext()){
				Integer number = numIter.next();
				ArrayList<Integer> list = straight.numbers;
				if (list.get(0)-number==1){
					list.add(0, number);
					numIter.remove();
				}
				else if (number - list.get(list.size()-1)==1){
					list.add(number);
					numIter.remove();
				}
			}
		} // end of while loop
	}
	
	// 连接扩展顺子，消除可以无缝连接的顺子。
	private static void concatPossibleStraights(List<StraightNumbers> strList){
		//using HashMap instead of ArrayList to improve performance
		HashMap<Integer, Boolean> concatedMap = new HashMap<>(strList.size());
		
		Iterator<StraightNumbers> strIterator = strList.iterator();//遍历迭代器
		final int listLength = strList.size();
		while (strIterator.hasNext()){
			ArrayList<Integer> list = strIterator.next().numbers;
			if (list.size()<=0){
				continue;
			}
			//内层循环因为要随机访问所以不使用iterator迭代
			Integer max = list.get(list.size()-1);
			//因为顺子已经按照最小值有序排列，因此检测顺子的连接时只需要比较每一个的最小值和当前的最大值
			for (int i=0; i<listLength; i++){
				//如果已经是被连接过的顺子，则跳过
				Boolean concated = concatedMap.get(i);
				//因为是HashMap，取值出来的数据会是null而不是false
				if (concated!=null){
					continue;
				}
				StraightNumbers bigger = strList.get(i);
				int biggerMax = bigger.numbers.get(0);
				if (biggerMax - max == 1) {
					list.addAll(bigger.numbers);
					max = list.get(list.size()-1);
					Collections.sort(list);
					concatedMap.put(i, true);//标记已被连接，避免多个顺子重复连接
				}
			}//当前顺子可连接的已测试完
		}
		//将待删除列表的数据反向排序，确保删除的时候从最大的下标开始倒序删除  ← ← ← ← 。
		ArrayList<Integer> toRemoveList = new ArrayList<>(concatedMap.keySet());
		Collections.sort(toRemoveList, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1); //reverse compare
			}
		});
		//注意：这里的遍历必须是int类型而不是 Integer类型，因为是记录的下标而不是元素自身的引用。
		for (int key : toRemoveList){
			strList.remove(key);
		}
		return ;
	}
}