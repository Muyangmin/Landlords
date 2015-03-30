package com.mym.landlords.card;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * 牌型的抽象父类。每个该类及其子类的实例都代表一手符合牌型规则的牌。
 * <p>
 * <h1>获取卡牌列表</h1>
 * 为程序统一起见，将所有的牌型都视为一个卡牌的列表，这个列表存储在 {@link #cardList} 字段中，客户端代码通过
 * {@link #getCardList()}方式获得。这其中只有单牌是例外，因此只需将单牌处理为仅有一个元素的列表即可。
 * </p>
 * <p>
 * <h1>牌型的排序</h1>
 * CardType类自身实现了 {@link Comparable}接口，{@link #SORT_COMPARATOR}实现了 {@link Comparator}接口。<br/>
 * 当需要对两手牌进行对比（例如出牌阶段下家和上家手牌大小的判断）时，使用 {@link CardType#compareTo(CardType)}方法；
 * 需要对不同手牌进行排序时（例如，出牌时总是希望先从包含最小卡牌的牌型开始），使用 {@link #SORT_COMPARATOR}进行排序。 
 * </p>
 * @author Muyangmin
 * @create 2015-3-23
 */
public abstract class CardType implements Comparable<CardType> {
	
	/**
	 * 用于实现所有牌型之间的排序。
	 * @see CardType#getMinCard()
	 */
	public static final Comparator<CardType> SORT_COMPARATOR = new Comparator<CardType>() {
		
		@Override
		public int compare(CardType lhs, CardType rhs) {
			Card left = lhs.getMinCard();
			Card right = rhs.getMinCard();
			if (left == null || right==null){
				throw new RuntimeException("subclass must implement getMinCard() method properly."
						+ "leftclass=" + lhs.getClass().getSimpleName() 
						+ "rightclass=" + right.getClass().getSimpleName());
			}
			//由于大小王的点数本身比其他牌都要大，因此免去对王炸的判断，仅令炸弹牌大于非炸弹牌即可。
			if (lhs instanceof Bomb && (!(rhs instanceof Bomb))){
				return 1;
			}
			else if( (rhs instanceof Bomb) && (!(lhs instanceof Bomb)) ){
				return -1;
			}
			//按照牌的最小点数排序
			return left.compareIgnoreSuit(right);
		}
	};

	/**
	 * 用于表示结果是未定义的、不明确的。
	 */
	protected static final int UNDEFINED_COMPARE = -100;
	
	/**
	 * 该种牌型的卡牌列表。子类必须在构造函数中为之赋值。
	 */
	protected ArrayList<Card> cardList = null; 
	
	/**
	 * 获取该牌型中数值最小的卡牌，供 {@link #SORT_COMPARATOR} 排序时使用。
	 */
	protected final Card getMinCard(){
		//assume list is sorted before this call
//		Collections.sort(cardList);
		//using getter method instead of field to check null pointer
		return getCardList().get(0);
	};
	
	
	/**
	 * 实现牌型对象之间的对比。由于斗地主游戏不允许下家的点数和上家的点数相同，因此忽略花色。
	 * 用于比较的两个对象要么至少一个是炸弹或王炸，要么必须是相同类型，否则将会抛出异常。
	 * @param another
	 *            需要进行比较的另一个对象
	 * @return 实现炸弹类型和非炸弹类型的基本比对，该方法认为炸弹始终比另一个大。
	 * 		当且仅当其中一个是炸弹时会返回compareTo方法约定的值，
	 *      如果两个都是炸弹或都不是炸弹， 则返回 {@link #UNDEFINED_COMPARE}。
	 * @throws ClassCastException 如果两种牌型无法判断大小又无法转换时将抛出该异常。
	 */
	@Override
	public int compareTo(CardType another) {
		//this super implementation just verify bomb type & class.
		//concrete value compare must be specified in subclass implementation.
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
	
	/**
	 * 判断当前牌能否打前一手牌。
	 * @param before 前一手牌
	 * @return 如果能打出则返回true，否则返回false。
	 */
	public final boolean canAgainstType(CardType before){
		if (this instanceof Rocket){
			return true;
		}
		if (this instanceof BombType && before instanceof NonBombType){
			return true;
		}
		return getClass().equals(before.getClass());
	}
}
/**包级标记接口， 表示炸弹类型。 */
interface NonBombType {
	
}

/**包级标记接口， 表示炸弹类型。 */
interface BombType {
	
}