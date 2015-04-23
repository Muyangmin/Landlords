package com.mym.landlords.card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * @author Muyangmin
 * @create 2015-4-16
 */
public class Airplane extends CardType implements NonBombType {

	private ArrayList<Three> bodyThrees;
	
	public Airplane(ArrayList<Card> list) {
		try {
			bodyThrees = getThreeLists(list);
			cardList = list;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("cannot makeup airplane: "+e.getMessage());
		}
	}
	
	private ArrayList<Three> getThreeLists(ArrayList<Card> list) throws IllegalArgumentException{
		if (list==null || list.isEmpty()){
			throw new IllegalArgumentException("param is null.");
		}
		//记录每张卡的点数对应的卡牌张数
		HashMap<Integer, Integer> cardCountMap = new HashMap<>();
		//与 cardCountMap 同步更新的一个映射，保存相同点数的卡牌，以备后用。
		HashMap<Integer, ArrayList<Card>> cardMap = new HashMap<>();
		int three=0, two=0, one=0;	//记录卡组数目
		for (Card card: list){
			int value = card.getValue();
			Integer already = cardCountMap.get(value);
			if (already==null){
				already = 0;
			}
			already++;
			if (already > 3){//超过四张一样的牌，不允许出牌。
				throw new IllegalArgumentException("too many same cards.");
			}
			//更新卡牌映射
			ArrayList<Card> sameCards = cardMap.get(value);
			if (sameCards==null){
				sameCards = new ArrayList<>();
			}
			sameCards.add(card);
			cardMap.put(value, sameCards);
			//更新计数
			cardCountMap.put(value, already);
		}
		for (ArrayList<Card> cards: cardMap.values()){
			switch (cards.size()){
				case 3: three++;break;
				case 2: two++;	break;
				case 1: one++;	break;
			}
		}
		if ( (one!=0 && two!=0) ){
			throw new IllegalArgumentException("mixed attachment, one="+one+",two="+two);
		}
		if ( (one!=0 && three!=one) || (two!=0 && three != two) ){
			throw new IllegalArgumentException("body and attachment not match."
					+ "one=" + one + ",two=" + two + ",three=" + three);
		}
		//组装三条并带牌
		Iterator<Integer> iterator = cardCountMap.keySet().iterator();
		ArrayList<ArrayList<Card>> bodyLists = new ArrayList<>();
		ArrayList<ArrayList<Card>> attachLists = new ArrayList<>();
		while (iterator.hasNext()){
			int value = iterator.next();
			if (cardCountMap.get(value)==3){	//是三条
				bodyLists.add(cardMap.get(value));
			}
			else{
				attachLists.add(cardMap.get(value));
			}
		}
		//bodyLists & attachLists must be same size.
		int size = bodyLists.size();
		//补充不带牌的情况
		if (attachLists.size()<size){
			int sub = size-attachLists.size();
			for (int i=0; i<sub; i++){
				attachLists.add(null);
			}
		}
		ArrayList<Three> tempThrees = new ArrayList<>();
		for (int i=0; i<size; i++ ){
			tempThrees.add(new Three(bodyLists.get(i), attachLists.get(i)));
 		}
		return tempThrees;
	}

	public ArrayList<Three> getBodyThrees() {
		return bodyThrees;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Airplane ").append(cardList);
		return builder.toString();
	}
}
