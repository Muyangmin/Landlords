package com.mym.landlords.card;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * 牌型的抽象父类。每个该类及其子类的实例都代表一手符合牌型规则的牌。所有的牌型都必须实现 {@link BombType}和
 * {@link NonBombType}其中的一个且仅能实现一个。
 * <p>
 * <h1>获取卡牌列表</h1>
 * 为程序统一起见，将所有的牌型都视为一个卡牌的列表，这个列表存储在 {@link #cardList} 字段中，客户端代码通过
 * {@link #getCardList()}方式获得。这其中只有单牌是例外，因此只需将单牌处理为仅有一个元素的列表即可。
 * </p>
 * <p>
 * <h1>从卡牌列表创建牌型</h1>
 * 在与用户交互的过程中，程序可能需要把用户的卡牌列表组合成牌型对象。{@link #createObjectFromCards(ArrayList)}方法
 * 可以完成这项工作。但是，<b>子类必须有一个构造器仅带有ArrayList&lt;Card&gt;参数</b>。
 * </p>
 * <p>
 * <h1>牌型的排序</h1>
 * CardType类自身实现了 {@link Comparable}接口，{@link #SORT_COMPARATOR}实现了
 * {@link Comparator}接口。<br/>
 * 当需要对两手牌进行对比（例如出牌阶段下家和上家手牌大小的判断）时，使用 {@link CardType#compareTo(CardType)}方法；
 * 需要对不同手牌进行排序时（例如，出牌时总是希望先从包含最小卡牌的牌型开始），使用 {@link #SORT_COMPARATOR}进行排序。
 * </p>
 * 
 * @author Muyangmin
 * @create 2015-3-23
 */
public abstract class CardType implements Comparable<CardType> {
	
	public CardType() {
		if (!(this instanceof BombType || this instanceof NonBombType)) {
			throw new RuntimeException(
					"Subclasses of CardType must implement either BombType or NonBombType!");
		}
		if (this instanceof BombType && this instanceof NonBombType) {
			throw new RuntimeException(
					"Subclasses of CardType must implement either BombType or NonBombType, not both!");
		}
	}
	
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
			return left.compareTo(right);
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
	public final boolean canAgainstType(CardType before) {
		if (before==null){
			return true;
		}
		if (this instanceof Rocket) {
			return true;
		}
		if (this instanceof BombType && before instanceof NonBombType) {
			return true;
		}
		//否则需要类型相同且大于参数值。
		return getClass().equals(before.getClass())
				&& isSameConcreteSubclass(before)
				&& (this.compareTo(before) > 0);
	}
	
	/**
	 * 用于标记当前的两个对象是否是完全一致的子类型，（例如，长度为5的顺子和长度为9的顺子并不完全一样）。
	 * @return 如果两个对象的子类型一样（即逻辑上可以在同一轮中由不同玩家打出），则返回true。
	 */
	protected boolean isSameConcreteSubclass(CardType another){
		return true;
	}
	
	/**
	 * 简单的工厂方法，用于把特定的卡牌列表转换为一个合适的类型。
	 * @param cards 卡牌列表
	 * @return 如果能把指定的卡牌转换为一个特定的类型，则返回这个类型的对象，否则返回null。
	 */
	public static final CardType createObjectFromCards(ArrayList<Card> cards){
		CardType instance = null;
		int cardCount;
		if (cards==null || ((cardCount=cards.size())==0) ){
			return null;
		}
		if (cardCount==1){
			//仅有一种情况，因此直接尝试创建对象并返回。为符合单一出口原则，并不在这里直接return
			instance=createObject(cards, Single.class);
		}
		else if (cardCount==2){
			//只有对子和王炸两种情况
			if ((instance=createObject(cards, Pair.class))==null){
				instance = createObject(cards, Rocket.class);
			}
		}
		else if (cardCount==3) {
			instance = createObject(cards, Three.class);
		}
		else if (cardCount==4) {
			instance = createObject(cards, Bomb.class);
			//三带一
			if (instance==null){
				instance = createObject(cards, Three.class);
			}
		}
		else if (cardCount==5){
			instance = createObject(cards, Straight.class);
			//三带对
			if (instance==null){
				instance = createObject(cards, Three.class);
			}
		}
		else if (cardCount>=5){
			instance = createObject(cards, Straight.class);
		}
		return instance;
	}

	//创建子类对象。该方法假定子类均有一个仅带有一个ArrayList参数的构造器。
	private static final CardType createObject(ArrayList<Card> cards,
			Class<? extends CardType> clz) {
		try {
			CardType instance = clz.getConstructor(ArrayList.class).newInstance(cards);
			return instance;
		}catch (InvocationTargetException e) {
			//ignore
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}
}