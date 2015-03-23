package com.mym.landlords.card;

/**
 * @author Muyangmin
 * @create 2015-3-23
 */
public abstract class CardType implements Comparable<CardType> {
	
	/**
	 * 用于表示结果是未定义的、不明确的。
	 */
	protected static final int UNDEFINED_COMPARE = -100;
	
	/**
	 * 实现炸弹类型和非炸弹类型的基本比对，该方法认为炸弹始终比另一个大。
	 * @param another 需要进行比较的另一个对象
	 * @return 当且仅当其中一个是炸弹时会返回compareTo方法约定的值，如果两个都是炸弹或都不是炸弹，
	 * 则返回 {@link #UNDEFINED_COMPARE}。
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CardType another) {
		if (this instanceof NonBombType && another instanceof BombType) {
			return -1;
		} else if (this instanceof BombType && another instanceof NonBombType) {
			return 1;
		} else {
			return UNDEFINED_COMPARE;
		}
	}
}
/**包级标记接口， 表示炸弹类型。 */
interface NonBombType { 
}

/**包级标记接口， 表示炸弹类型。 */
interface BombType { // 包级标记接口，炸弹类型
}