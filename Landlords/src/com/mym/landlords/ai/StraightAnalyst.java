package com.mym.landlords.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.mym.landlords.card.Card;

//package access
final class StraightAnalyst {
	protected static List<StraightNumbers> getAllStraights(
			ArrayList<Card> cardList) {
		//transfer int array
		ArrayList<Integer> leftCardValues = new ArrayList<>();
		for (Card card: cardList){
			leftCardValues.add(card.getValue());
		}
		List<StraightNumbers> shortList = getShortestStraightList(leftCardValues);
		extendStraight(shortList, leftCardValues);
		concatPossibleStraights(shortList);
		return shortList;
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

	// 选取最小的五连，参数必须非递减有序。
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

	// 扩展所有五连，以使能拼接的都拼接上。
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
		ArrayList<Integer> toRemove = new ArrayList<Integer>(); //待删除元素下标（即连接后被合并的数据）
		
		Iterator<StraightNumbers> strIterator = strList.iterator();//遍历迭代器
		final int listLength = strList.size();
		while (strIterator.hasNext()){
			ArrayList<Integer> list = strIterator.next().numbers;
			if (list.size()<=0){
				continue;
			}
			Integer max = list.get(list.size()-1);
			for (int i=0; i<listLength; i++){
				StraightNumbers bigger = strList.get(i);
				int biggerMax = bigger.numbers.get(bigger.numbers.size()-1);
				if (biggerMax - max == 1) {
					list.addAll(bigger.numbers);
					//更新最大值
					max = list.get(list.size()-1);
					Collections.sort(list);
					bigger.numbers.clear();
					toRemove.add(i);
				}
			}//当前顺子可连接的已测试完
		}
		for (Integer id : toRemove){
			toRemove.remove	(id);	// remove the object
		}
		return ;
	}
}