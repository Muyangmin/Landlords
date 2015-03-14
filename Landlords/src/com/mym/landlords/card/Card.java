package com.mym.landlords.card;

import java.io.Serializable;

/**
 * 单张卡牌实体类。
 * @author Muyangmin
 * @create 2015-3-14
 */
public final class Card implements Serializable, Comparable<Card>{
	//-------------- 卡牌数值大小定义 BEGIN --------
	//NOTE:这里将所有卡牌按单打的大小依次+1，便于在构造器中检查参数合法性。
	public static final int CARD_VALUE_3 = 3;
	public static final int CARD_VALUE_4 = 4;
	public static final int CARD_VALUE_5 = 5;
	public static final int CARD_VALUE_6 = 6;
	public static final int CARD_VALUE_7 = 7;
	public static final int CARD_VALUE_8 = 8;
	public static final int CARD_VALUE_9 = 9;
	public static final int CARD_VALUE_10 = 10;
	public static final int CARD_VALUE_J = 11;
	public static final int CARD_VALUE_Q = 12;
	public static final int CARD_VALUE_K = 13;
	public static final int CARD_VALUE_A = 14;
	public static final int CARD_VALUE_2 = 15;
	/** 小王 */
	public static final int CARD_VALUE_JOKER_S = 16;
	/** 大王 */
	public static final int CARD_VALUE_JOKER_B = 17;
	//-------------- 卡牌数值大小定义 END --------
	
	private static final long serialVersionUID = 1L;
	
	private CardType type;		//卡牌花色
	private int value;			//卡牌数值。即上面的大小值之一。
	private String valueStr;		//卡牌数值的字符描述，主要用于提示或日志输出，使用频繁，故在构造函数中初始化。

	/**
	 * 创建一张新的卡牌。
	 * @param type 卡牌的花色。
	 * @param value 卡牌的面值，必须为 {@value #CARD_VALUE_3} ~ {@value #CARD_VALUE_JOKER_B}之间的值。
	 */
	public Card(CardType type, int value) {
		if (type == null || value < CARD_VALUE_3 || value > CARD_VALUE_JOKER_B) {
			throw new IllegalArgumentException("invalid card!type="+type+", value="+value);
		}
		this.type = type;
		this.value = value;
		this.valueStr = getValueLiteral(value);
	}

	@Override
	public int compareTo(Card another) {
		return Integer.valueOf(value).compareTo(another.value);
	}
	
	private final String getValueLiteral(int value){
		String str;
		switch (value) {
		case CARD_VALUE_3:
		case CARD_VALUE_4:
		case CARD_VALUE_5:
		case CARD_VALUE_6:
		case CARD_VALUE_7:
		case CARD_VALUE_8:
		case CARD_VALUE_9:
		case CARD_VALUE_10:
			str =String.valueOf(value);
			break;
		case CARD_VALUE_2:
			str = "2";
			break;
		case CARD_VALUE_J:
			str = "J";
			break;
		case CARD_VALUE_Q:
			str = "Q";
			break;
		case CARD_VALUE_K:
			str = "K";
			break;
		case CARD_VALUE_A:
			str = "A";
			break;
		case CARD_VALUE_JOKER_S:
			str = "S";
			break;
		case CARD_VALUE_JOKER_B:
			str = "B";
			break;
		default:
			throw new RuntimeException("Invalid default branch exec, cardvalue="+value);
		}
		return str;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		//仅输出类似 "SPADE A"之类文本。
		builder.append("Card [").append(type).append(" ").append(valueStr).append("]");
		return builder.toString();
	}
}
