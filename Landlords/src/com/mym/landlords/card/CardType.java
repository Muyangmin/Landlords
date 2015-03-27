package com.mym.landlords.card;

import java.util.ArrayList;

/**
 * 牌型的抽象父类。
 * <p>
 * 为程序统一起见，将所有的牌型都视为一个卡牌的列表，这个列表存储在 {@link #cardList} 字段中，客户端代码通过
 * {@link #getCardList()}方式获得。这其中只有单牌是例外，因此只需将单牌处理为仅有一个元素的列表即可。
 * </p>
 * 
 * @author Muyangmin
 * @create 2015-3-23
 */
public abstract class CardType implements Comparable<CardType> {
	
	/**
	 * 用于表示结果是未定义的、不明确的。
	 */
	protected static final int UNDEFINED_COMPARE = -100;
	
	/**
	 * 该种牌型的卡牌列表。子类必须在构造函数中为之赋值。
	 */
	protected ArrayList<Card> cardList = null; 
	
	/**
	 * 实现牌型对象之间的对比。由于斗地主游戏不允许下家的点数和上家的点数相同，因此忽略花色。
	 * 用于比较的两个对象要么至少一个是炸弹，要么必须是相同类型，否则将会抛出异常。
	 * @param another
	 *            需要进行比较的另一个对象
	 * @return 实现炸弹类型和非炸弹类型的基本比对，该方法认为炸弹始终比另一个大。
	 * 		当且仅当其中一个是炸弹时会返回compareTo方法约定的值，
	 *      如果两个都是炸弹或都不是炸弹， 则返回 {@link #UNDEFINED_COMPARE}。
	 * @throws ClassCastException 如果两种牌型无法判断大小又无法转换时将抛出该异常。
	 */
	@Override
	public int compareTo(CardType another) {
		if (this instanceof NonBombType && another instanceof BombType) {
			return -1;
		} else if (this instanceof BombType && another instanceof NonBombType) {
			return 1;
		} else {
			//after bomb type check:
			Class<?> thisClz = getClass();
			Class<?> anotherClz = another.getClass();
			if (!thisClz.equals(anotherClz)) {
				throw new ClassCastException(
						"compareTo wrong type, this=" + thisClz.getSimpleName()
								+ ", another=" + anotherClz.getSimpleName());
			}
			return UNDEFINED_COMPARE;
		}
	}

	/**
	 * 获得这一手牌的卡牌列表。
	 * @return 卡牌列表，不会为null（如果子类没有通过 {@link #cardList}
	 *         初始化列表值，则会抛出NullPointerException）。
	 */
	public final ArrayList<Card> getCardList() {
		if (cardList==null){
			throw new NullPointerException("subclass"
					+ getClass().getSimpleName()
					+ " must init cardList in constructor!");
		}
		return cardList;
	}
}
/**包级标记接口， 表示炸弹类型。 */
interface NonBombType {
	
}

/**包级标记接口， 表示炸弹类型。 */
interface BombType {
	
}