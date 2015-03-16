package com.mym.landlords.res;

import com.mym.landlords.R;
import com.mym.landlords.card.Card;
import com.mym.landlords.card.CardType;

import android.content.Context;

/**
 * 负责加载并存储从Assets文件中加载到内存中的资源。
 * <p>
 * <h1>加载数据</h1>
 * 使用 {@link #loadAssets(Context, LoadingProgressListener)}方法进行资源文件的加载，加载进度会通过参数中的接口进行传递。
 * 注意：在调用 {@link #getInstance()}获得该类的实例之前必须确保 load方法已被调用过，否则将抛出异常。
 * </p>
 * <p>
 * <h1>数据安全</h1>
 * 因加载的资源较多，且多使用数组或列表的形式存放，因此对这些数据的安全性不做任何强制性保证，
 * 但在代码编写过程中需要注意避免为该类的任何域赋值。
 * </p>
 * @author Muyangmin
 * @create 2015-3-16
 */
public final class Assets {
	
	private static Assets instance;
	
	/** 黑桃卡牌，按卡牌从小到大排序。  */
	public LiveBitmap[] spades = new LiveBitmap[13];;
	/** 红桃卡牌，按卡牌从小到大排序。  */
	public LiveBitmap[] hearts = new LiveBitmap[13];;
	/** 梅花卡牌，按卡牌从小到大排序。  */
	public LiveBitmap[] clubs = new LiveBitmap[13];;
	/** 方片卡牌，按卡牌从小到大排序。  */
	public LiveBitmap[] diamonds = new LiveBitmap[13];;
	
	/** 小型黑桃卡牌（仅有左上角的数字和花色），按卡牌从小到大排序。  */
	public LiveBitmap[] smallSpades = new LiveBitmap[13];;
	/** 小型红桃卡牌（仅有左上角的数字和花色），按卡牌从小到大排序。  */
	public LiveBitmap[] smallHearts = new LiveBitmap[13];;
	/** 小型梅花卡牌（仅有左上角的数字和花色），按卡牌从小到大排序。  */
	public LiveBitmap[] smallClubs = new LiveBitmap[13];;
	/** 小型方片卡牌（仅有左上角的数字和花色），按卡牌从小到大排序。  */
	public LiveBitmap[] smallDiamonds = new LiveBitmap[13];;
	
	/** 小王卡牌。 */
	public LiveBitmap jokerS;
	/** 大王卡牌。 */
	public LiveBitmap jokerB;
	
	/** 卡牌背面图。 */
	public LiveBitmap cardbg;
	
	/** 叫地主音效：不叫。 */
	public int landloadPass;
	/** 叫地主音效：1分。 */
	public int landloadP1;
	/** 叫地主音效：2分。 */
	public int landloadP2;
	/** 叫地主音效：3分。 */
	public int landloadP3;
	
	/** 牌型音效：单。 */
	public int cardSingle;
	/** 牌型音效：对。 */
	public int cardTwo;
	/** 牌型音效：小王。 */
	public int cardJokerS;
	/** 牌型音效：大王。 */
	public int cardJokerB;
	/** 牌型音效：三不带。 */
	public int cardThree;
	/** 牌型音效：三带一。 */
	public int cardThree1;
	/** 牌型音效：三带对。 */
	public int cardThree2;
	/** 牌型音效：飞机。 */
	public int cardAir;
	/** 牌型音效：顺子。 */
	public int cardShun1;
	/** 牌型音效：双顺（连对）。 */
	public int cardShun2;
	/** 牌型音效：炸弹。 */
	public int cardBoom;
	/** 牌型音效： 王炸（有些地方称为火箭）。 */
	public int cardRocket;
	
	/** 牌局音效：过。 */
	public int playPass;
	/** 牌局音效：跟牌压牌。 */
	public int[] playBigger;
	/** 牌局音效：炸弹爆炸。 */
	public int playBoom;
	/** 牌局音效：胜。 */
	public int playWin;
	/** 牌局音效：负。 */
	public int playLose;
	
	/**
	 * 获取该类的唯一实例。
	 * @throws RuntimeException 如果之前未调用过 {@link #loadAssets(Context, LoadingProgressListener)}方法。
	 */
	public static final Assets getInstance(){
		if (instance==null){
			throw new RuntimeException("you must call load() method before getInstance()!");
		}
		return instance;
	}
	
	/**
	 * 加载Assets资源，该方法应该仅被调用一次。
	 * @param context 上下文信息，不能为null。
	 * @param listener 进度监听器，不能为null。
	 * @throws RuntimeException 如果之前已经调用过该方法则抛出此异常。
	 */
	public static synchronized void loadAssets(Context context, LoadingProgressListener listener){
		if (instance!=null){
			throw new RuntimeException("Assets resources have been already loaded.");
		}
		if (context==null || listener==null){
			throw new NullPointerException("params cannot be null.");
		}
		instance = new Assets();
		instance.load(context, listener);
	}
	
	/**
	 * 执行实际的加载操作，由于公有方法中做了参数检查，这里不再检查。
	 */
	private final void load(Context context, LoadingProgressListener listener){
		loadBitmap(context, listener);
		loadSound(context, listener);
		listener.onLoadCompleted();
	}
	
	private final void loadSound(Context context, LoadingProgressListener listener){
		GlobalSoundPool sp = GlobalSoundPool.getInstance(context);
		String loadingSound = context.getString(R.string.str_loading_task_sound);
		final int totalSoundCount = 23;	//数出来的
		int completed = 0;	//复位
		landloadP1 = sp.loadSound(context, "landlord_p1.mp3");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		landloadP2 = sp.loadSound(context, "landlord_p2.mp3");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		landloadP3 = sp.loadSound(context, "landlord_p3.mp3");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		landloadPass = sp.loadSound(context, "landlord_pass.mp3");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		cardAir = sp.loadSound(context, "feiji.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		cardBoom = sp.loadSound(context, "zhadan.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		cardJokerS = sp.loadSound(context, "xiaowang.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		cardJokerB = sp.loadSound(context, "dawang.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		cardRocket = sp.loadSound(context, "wangzha.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		cardShun1 = sp.loadSound(context, "shunzi.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		cardShun2 = sp.loadSound(context, "liandui.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		cardSingle = sp.loadSound(context, "givecard.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		cardThree = sp.loadSound(context, "givecard.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		cardThree1 = sp.loadSound(context, "sandaiyi.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		cardThree2 = sp.loadSound(context, "sandaiyidui.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		cardTwo = sp.loadSound(context, "givecard.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		
		playBigger = new int[3];
		playBigger[0] = sp.loadSound(context, "dani1.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		playBigger[1] = sp.loadSound(context, "dani2.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		playBigger[2] = sp.loadSound(context, "dani3.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		playPass = sp.loadSound(context, "buyao.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		playBoom = sp.loadSound(context, "boom.wav");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		playLose = sp.loadSound(context, "lose.mp3");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		playWin = sp.loadSound(context, "win.mp3");
		notifyProgressChanged(++completed, totalSoundCount, listener, loadingSound);
		if (completed!=totalSoundCount){
			throw new RuntimeException("There is a bug. completed "+completed+" while total is "+totalSoundCount);
		}
	}
	
	private final void loadBitmap(Context context, LoadingProgressListener listener){
		//54 = 一副牌，52=一副牌的较小版本，再加上卡牌背面图片
				final int totalBitmap = 54 + 52 + 1;
				
				String loadingBitmap = context.getString(R.string.str_loading_task_bitmap);
				int completed = 0;
				// 加载黑桃图片
				for (int i = 0, value = Card.CARD_VALUE_3; i < 13; i++, value++) {
					spades[i] = LiveBitmap.loadBitmap(context,
							getCardAssetsName(CardType.Spade, value, false));
					smallSpades[i] = LiveBitmap.loadBitmap(context,
							getCardAssetsName(CardType.Spade, value, true));
					completed += 2;
					notifyProgressChanged(completed, totalBitmap, listener,
							loadingBitmap);
				}
				// 加载红桃图片
				for (int i = 0, value = Card.CARD_VALUE_3; i < 13; i++, value++) {
					hearts[i] = LiveBitmap.loadBitmap(context,
							getCardAssetsName(CardType.Heart, value, false));
					smallSpades[i] = LiveBitmap.loadBitmap(context,
							getCardAssetsName(CardType.Spade, value, true));
					completed += 2;
					notifyProgressChanged(completed, totalBitmap, listener,
							loadingBitmap);
				}
				// 加载梅花图片
				for (int i = 0, value = Card.CARD_VALUE_3; i < 13; i++, value++) {
					clubs[i] = LiveBitmap.loadBitmap(context,
							getCardAssetsName(CardType.Club, value, false));
					smallSpades[i] = LiveBitmap.loadBitmap(context,
							getCardAssetsName(CardType.Spade, value, true));
					completed += 2;
					notifyProgressChanged(completed, totalBitmap, listener,
							loadingBitmap);
				}
				// 加载方片图片
				for (int i = 0, value = Card.CARD_VALUE_3; i < 13; i++, value++) {
					diamonds[i] = LiveBitmap.loadBitmap(context,
							getCardAssetsName(CardType.Diamond, value, false));
					smallSpades[i] = LiveBitmap.loadBitmap(context,
							getCardAssetsName(CardType.Spade, value, true));
					completed += 2;
					notifyProgressChanged(completed, totalBitmap, listener,
							loadingBitmap);
				}
				jokerS = LiveBitmap.loadBitmap(context, 
						getCardAssetsName(CardType.Joker, Card.CARD_VALUE_JOKER_S, false));
				jokerB = LiveBitmap.loadBitmap(context, 
						getCardAssetsName(CardType.Joker, Card.CARD_VALUE_JOKER_B, false));
				completed += 2;
				notifyProgressChanged(completed, totalBitmap, listener,
						loadingBitmap);
				cardbg = LiveBitmap.loadBitmap(context, "cardbg.png");
				notifyProgressChanged(++completed, totalBitmap, listener,
						loadingBitmap);
	}
	
	//通知加载的进度
	private final void notifyProgressChanged(int completed, int total,
			LoadingProgressListener listener, String currentTask) {
		int progress = (int) (completed/(double)total*100);
		listener.onProgressChanged(progress, currentTask);
	}
	
	/**
	 * 拼接卡牌文件名。
	 * @param type 卡牌花色。
	 * @param value 卡牌面值。
	 * @param isSmaller 是整张还是仅左上角。
	 * @return 返回拼接好的文件名。
	 */
	private String getCardAssetsName(CardType type, int value, boolean isSmaller){
		StringBuilder sb = new StringBuilder();
		char typePrefix;
		switch (type) {
			case Spade	: typePrefix = 's'; break;
			case Heart	: typePrefix = 'h'; break;
			case Club 	: typePrefix = 'c'; break;
			case Diamond: typePrefix = 'd'; break;
			case Joker	:
				if (value == Card.CARD_VALUE_JOKER_S){
					return "j1.png";
				}
				else if(value==Card.CARD_VALUE_JOKER_B){
					return "j2.png";
				}
				else{
					throw new RuntimeException("invalid value : "+value);
				}
			default: throw new RuntimeException("invalid type : "+type);
		}
		sb.append(typePrefix).append(value);
		if (isSmaller){
			sb.append('s');
		}
		sb.append(".png");
		return sb.toString();
	}
	
	/**g
	 * 监听Assets资源的加载进度。
	 * @author Muyangmin
	 * @create 2015-3-16
	 */
	public interface LoadingProgressListener{
		/**
		 * 加载进度改变时调用。
		 * <p>注意Task的概念，即将加载分为多个步骤（例如卡牌、音效...），每步中有0~100的进度。</p>
		 * @param progress 当前进度，取值在0~100之间。
		 * @param currentTaskDescription 当前任务描述。
		 */
		void onProgressChanged(int progress, String currentTaskDescription);
		/**
		 * 当所有加载资源完成时调用。
		 */
		void onLoadCompleted();
	}
	
}
