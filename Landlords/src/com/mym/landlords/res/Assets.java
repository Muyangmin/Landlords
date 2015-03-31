package com.mym.landlords.res;

import com.mym.landlords.card.Card;
import com.mym.landlords.card.CardSuit;
import com.mym.landlords.widget.MappedTouchEvent;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.WindowManager;

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
	
	private static final String LOG_TAG = "Assets";
	
	private static Assets instance;
	
	/** 牌桌背景。  */
	public LiveBitmap bkgGameTable;
	
	/** 黑桃卡牌，按卡牌从小到大排序。  */
	public LiveBitmap[] cardSpades = new LiveBitmap[13];;
	/** 红桃卡牌，按卡牌从小到大排序。  */
	public LiveBitmap[] cardHearts = new LiveBitmap[13];;
	/** 梅花卡牌，按卡牌从小到大排序。  */
	public LiveBitmap[] cardClubs = new LiveBitmap[13];;
	/** 方片卡牌，按卡牌从小到大排序。  */
	public LiveBitmap[] cardDiamonds = new LiveBitmap[13];;
	
	/** 小型黑桃卡牌（仅有左上角的数字和花色，主要用于展示底牌），按卡牌从小到大排序。  */
	public LiveBitmap[] cardSmallSpades = new LiveBitmap[13];;
	/** 小型红桃卡牌（仅有左上角的数字和花色，主要用于展示底牌），按卡牌从小到大排序。  */
	public LiveBitmap[] cardSmallHearts = new LiveBitmap[13];;
	/** 小型梅花卡牌（仅有左上角的数字和花色，主要用于展示底牌），按卡牌从小到大排序。  */
	public LiveBitmap[] cardSmallClubs = new LiveBitmap[13];;
	/** 小型方片卡牌（仅有左上角的数字和花色，主要用于展示底牌），按卡牌从小到大排序。  */
	public LiveBitmap[] cardSmallDiamonds = new LiveBitmap[13];;
	
	/** 小王卡牌。注意：王不分大小图。 */
	public LiveBitmap cardJokerS;
	/** 大王卡牌。 注意：王不分大小图。*/
	public LiveBitmap cardJokerB;
	
	/** 卡牌背面图。 */
	public LiveBitmap cardbg;
	/** 数字信息图片。 */
	public LiveBitmap bitmapNumbers;
	/** AI形象左。 */
	public LiveBitmap playerLeft;
	/** 玩家形象。 */
	public LiveBitmap playerHuman;
	/** AI形象右。 */
	public LiveBitmap playerRight;
	/** 按钮背景图。 */
	public LiveBitmap bitmapBtnBkg;
	/** 按钮背景图。 */
	public LiveBitmap bitmapBtnBkgPressed;
	/** 叫地主按钮：不叫。 */
	public LiveBitmap bitmapLandlordPass;
	/** 叫地主按钮：1分。 */
	public LiveBitmap bitmapLandlordP1;
	/** 叫地主按钮：2分。 */
	public LiveBitmap bitmapLandlordP2;
	/** 叫地主按钮：3分。 */
	public LiveBitmap bitmapLandlordP3;
	/** 出牌按钮。 */
	public LiveBitmap bitmapGiveCard;
	/** 不出按钮。 */
	public LiveBitmap bitmapDoNotGiveCard;
	/** 重选按钮。 */
	public LiveBitmap bitmapRechoose;
	/** 提示按钮。 */
	public LiveBitmap bitmapTips;
	/** 地主图标 */
	public LiveBitmap iconLandlord;
	
	/** 叫地主音效：不叫。 */
	public int soundLandloadPass;
	/** 叫地主音效：1分。 */
	public int soundLandloadP1;
	/** 叫地主音效：2分。 */
	public int soundLandloadP2;
	/** 叫地主音效：3分。 */
	public int soundLandloadP3;
	
	/** 牌型音效：单。 */
	public int soundCardSingle;
	/** 牌型音效：对。 */
	public int soundCardTwo;
	/** 牌型音效：小王。 */
	public int soundCardJokerS;
	/** 牌型音效：大王。 */
	public int soundCardJokerB;
	/** 牌型音效：三不带。 */
	public int soundCardThree;
	/** 牌型音效：三带一。 */
	public int soundCardThree1;
	/** 牌型音效：三带对。 */
	public int soundCardThree2;
	/** 牌型音效：飞机。 */
	public int soundCardPlane;
	/** 牌型音效：顺子。 */
	public int soundCardShun1;
	/** 牌型音效：双顺（连对）。 */
	public int soundCardShun2;
	/** 牌型音效：炸弹。 */
	public int soundCardBoom;
	/** 牌型音效： 王炸（有些地方称为火箭）。 */
	public int soundCardRocket;
	
	/** 牌局音效：过。 */
	public int soundPlayPass;
	/** 牌局音效：跟牌压牌。 */
	public int[] soundPlayBigger;
	/** 牌局音效：炸弹爆炸。 */
	public int soundPlayBoom;
	/** 牌局音效：飞机。 */
	public int soundPlayPlane;
	/** 牌局音效：胜。 */
	public int soundPlayWin;
	/** 牌局音效：负。 */
	public int soundPlayLose;
	
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
//			throw new RuntimeException("Assets resources have been already loaded.");
			//这里不假定instance不为Null即代表其所持有的内存资源没有被释放，为了安全起见，强制重新加载。
			Log.e(LOG_TAG, "Assets resources have been already loaded.Force reloading...");
			//强制释放旧的资源，否则如果短时间多次频繁开关应用，会导致OOM。
			instance.recycleOldBitmap();
			Log.d(LOG_TAG, "old resources cleared.");
			instance = null;
		}
		if (context==null || listener==null){
			throw new NullPointerException("params cannot be null.");
		}
		instance = new Assets();
		instance.load(context, listener);
	}
	
	private final synchronized void recycleOldBitmap(){
		recycleBitmap(bitmapNumbers);
		recycleBitmap(bkgGameTable);
		recycleBitmap(cardSpades);
		recycleBitmap(cardHearts);
		recycleBitmap(cardClubs);
		recycleBitmap(cardDiamonds);
		recycleBitmap(cardSmallSpades);
		recycleBitmap(cardSmallHearts);
		recycleBitmap(cardSmallClubs);
		recycleBitmap(cardDiamonds);
		recycleBitmap(cardJokerB);
		recycleBitmap(cardJokerS);
		recycleBitmap(iconLandlord);
		recycleBitmap(playerHuman);
		recycleBitmap(playerLeft);
		recycleBitmap(playerRight);
		recycleBitmap(bitmapLandlordPass);
		recycleBitmap(bitmapLandlordP1);
		recycleBitmap(bitmapLandlordP2);
		recycleBitmap(bitmapLandlordP3);
	}
	
	private final void recycleBitmap(LiveBitmap[] array){
		if (array!=null){
			for (LiveBitmap bitmap : array){
				recycleBitmap(bitmap);
			}
		}
	}
	
	private final void recycleBitmap(LiveBitmap bitmap){
		try {
			if (bitmap!=null){
				bitmap.getBitmap().recycle();
			}
		} catch (Exception e) {
			Log.w(LOG_TAG, "exception while recycling bitmap");
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行实际的加载操作，由于公有方法中做了参数检查，这里不再检查。
	 */
	private final void load(Context context, LoadingProgressListener listener){
		final int totalBitmap = 54 + 52		//54=一副牌，52=一副牌的较小版本
							+ 1 + 1	+ 2		//卡牌背面图、牌桌背景图、按钮背景图
							+ 1				//数字图
							+ 4	+ 4			//叫地主按钮、出牌系列按钮
							+ 3 + 1;		//人物形象图、地主图标
		final int totalSoundCount = 24;		//数出来的
		int total = totalBitmap + totalSoundCount;
		int loadedBitmap = loadBitmap(0, total, context, listener);
		int loadedRes = loadSound(loadedBitmap, total, context, listener);
		//安全检查，避免有遗漏忘记加载的资源。
		if (loadedRes!=total){
			throw new RuntimeException("Resource loaded "+loadedRes+", but it was declared to be "+total);
		}
		listener.onLoadCompleted();
	}
	
	/**
	 * 加载声音资源
	 * @param hasCompleted 已加载的资源数目
	 * @param total 全部资源总数
	 * @return 返回已加载的资源数目。
	 */
	private final int loadSound(final int hasCompleted, final int total,
			Context context, LoadingProgressListener listener) {
		GlobalSoundPool sp = GlobalSoundPool.getInstance(context);
		int completed = hasCompleted;	//复位
		soundLandloadP1 = sp.loadSound(context, "landlord_p1.mp3");
		notifyProgressChanged(++completed, total, listener);
		soundLandloadP2 = sp.loadSound(context, "landlord_p2.mp3");
		notifyProgressChanged(++completed, total, listener);
		soundLandloadP3 = sp.loadSound(context, "landlord_p3.mp3");
		notifyProgressChanged(++completed, total, listener);
		soundLandloadPass = sp.loadSound(context, "landlord_pass.mp3");
		notifyProgressChanged(++completed, total, listener);
		soundCardPlane = sp.loadSound(context, "feiji.wav");
		notifyProgressChanged(++completed, total, listener);
		soundCardBoom = sp.loadSound(context, "zhadan.wav");
		notifyProgressChanged(++completed, total, listener);
		soundCardJokerS = sp.loadSound(context, "xiaowang.wav");
		notifyProgressChanged(++completed, total, listener);
		soundCardJokerB = sp.loadSound(context, "dawang.wav");
		notifyProgressChanged(++completed, total, listener);
		soundCardRocket = sp.loadSound(context, "wangzha.wav");
		notifyProgressChanged(++completed, total, listener);
		soundCardShun1 = sp.loadSound(context, "shunzi.wav");
		notifyProgressChanged(++completed, total, listener);
		soundCardShun2 = sp.loadSound(context, "liandui.wav");
		notifyProgressChanged(++completed, total, listener);
		soundCardSingle = sp.loadSound(context, "givecard.wav");
		notifyProgressChanged(++completed, total, listener);
		soundCardThree = sp.loadSound(context, "givecard.wav");
		notifyProgressChanged(++completed, total, listener);
		soundCardThree1 = sp.loadSound(context, "sandaiyi.wav");
		notifyProgressChanged(++completed, total, listener);
		soundCardThree2 = sp.loadSound(context, "sandaiyidui.wav");
		notifyProgressChanged(++completed, total, listener);
		soundCardTwo = sp.loadSound(context, "givecard.wav");
		notifyProgressChanged(++completed, total, listener);
		
		soundPlayBigger = new int[3];
		soundPlayBigger[0] = sp.loadSound(context, "dani1.wav");
		notifyProgressChanged(++completed, total, listener);
		soundPlayBigger[1] = sp.loadSound(context, "dani2.wav");
		notifyProgressChanged(++completed, total, listener);
		soundPlayBigger[2] = sp.loadSound(context, "dani3.wav");
		notifyProgressChanged(++completed, total, listener);
		soundPlayPass = sp.loadSound(context, "buyao.wav");
		notifyProgressChanged(++completed, total, listener);
		soundPlayBoom = sp.loadSound(context, "boom.wav");
		notifyProgressChanged(++completed, total, listener);
		soundPlayPlane = sp.loadSound(context, "plane.wav");
		notifyProgressChanged(++completed, total, listener);
		soundPlayLose = sp.loadSound(context, "lose.mp3");
		notifyProgressChanged(++completed, total, listener);
		soundPlayWin = sp.loadSound(context, "win.mp3");
		notifyProgressChanged(++completed, total, listener);
		return completed;
	}
	
	private final int loadBitmap(final int hasCompleted, final int total,
			Context context,
			LoadingProgressListener listener) {
		if (!(context instanceof Activity)) {
			throw new IllegalArgumentException(
					"This context must be instance of activity.");
		}
		WindowManager wm = ((Activity) context).getWindowManager();
		Point point = new Point();
		wm.getDefaultDisplay().getSize(point);
		Log.d(LOG_TAG, "window point:" + point.toString());
		//重要：这里将画笔工具和事件捕捉类的缩放比例一起设置。
		GameGraphics.initGraphicsScale(point);
		GameGraphics graphics = GameGraphics.newInstance();
		final float scaleX = graphics.getScaleX();
		final float scaleY = graphics.getScaleY();
		Log.d(LOG_TAG, "scalex = "+scaleX+", scaleY="+scaleY);
		MappedTouchEvent.initMapper(scaleX, scaleY);
		
		int completed = hasCompleted;
		// 加载黑桃图片
		for (int i = 0, value = Card.CARD_VALUE_3; i < 13; i++, value++) {
			cardSpades[i] = LiveBitmap.loadBitmap(context,
					getCardAssetsName(CardSuit.Spade, value, false), scaleX,
					scaleY);
			cardSmallSpades[i] = LiveBitmap.loadBitmap(context,
					getCardAssetsName(CardSuit.Spade, value, true), scaleX,
					scaleY);
			completed += 2;
			notifyProgressChanged(completed, total, listener);
		}
		// 加载红桃图片
		for (int i = 0, value = Card.CARD_VALUE_3; i < 13; i++, value++) {
			cardHearts[i] = LiveBitmap.loadBitmap(context,
					getCardAssetsName(CardSuit.Heart, value, false), scaleX,
					scaleY);
			cardSmallHearts[i] = LiveBitmap.loadBitmap(context,
					getCardAssetsName(CardSuit.Heart, value, true), scaleX,
					scaleY);
			completed += 2;
			notifyProgressChanged(completed, total, listener);
		}
		// 加载梅花图片
		for (int i = 0, value = Card.CARD_VALUE_3; i < 13; i++, value++) {
			cardClubs[i] = LiveBitmap.loadBitmap(context,
					getCardAssetsName(CardSuit.Club, value, false), scaleX,
					scaleY);
			cardSmallClubs[i] = LiveBitmap.loadBitmap(context,
					getCardAssetsName(CardSuit.Club, value, true), scaleX,
					scaleY);
			completed += 2;
			notifyProgressChanged(completed, total, listener);
		}
		// 加载方片图片
		for (int i = 0, value = Card.CARD_VALUE_3; i < 13; i++, value++) {
			cardDiamonds[i] = LiveBitmap.loadBitmap(context,
					getCardAssetsName(CardSuit.Diamond, value, false), scaleX,
					scaleY);
			cardSmallDiamonds[i] = LiveBitmap.loadBitmap(context,
					getCardAssetsName(CardSuit.Diamond, value, true), scaleX,
					scaleY);
			completed += 2;
			notifyProgressChanged(completed, total, listener);
		}
		cardJokerS = LiveBitmap.loadBitmap(
				context,
				getCardAssetsName(CardSuit.Joker, Card.CARD_VALUE_JOKER_S,
						false), scaleX, scaleY);
		cardJokerB = LiveBitmap.loadBitmap(
				context,
				getCardAssetsName(CardSuit.Joker, Card.CARD_VALUE_JOKER_B,
						false), scaleX, scaleY);
		
		completed += 2;
		notifyProgressChanged(completed, total, listener);
		cardbg = LiveBitmap.loadBitmap(context, "cardbg.png", scaleX, scaleY);
		notifyProgressChanged(++completed, total, listener);
		bkgGameTable = LiveBitmap.loadBitmap(context, "bkg_table.png", scaleX, scaleY);
		notifyProgressChanged(++completed, total, listener);
		bitmapNumbers = LiveBitmap.loadBitmap(context, "numbers.png", scaleX, scaleY);
		notifyProgressChanged(++completed, total, listener);
		
		playerLeft = LiveBitmap.loadBitmap(context, "playerLeft.png", scaleX, scaleY);
		playerHuman = LiveBitmap.loadBitmap(context, "playerHuman.png", scaleX, scaleY);
		playerRight = LiveBitmap.loadBitmap(context, "playerRight.png", scaleX, scaleY);
		completed +=3 ;
		notifyProgressChanged(completed, total, listener);
		iconLandlord = LiveBitmap.loadBitmap(context, "icLandlord.png", scaleX, scaleY);
		notifyProgressChanged(++completed, total, listener);
		
		//加载按钮图
		bitmapBtnBkg = LiveBitmap.loadBitmap(context, "bkg_btn_normal.png", scaleX, scaleY);
		notifyProgressChanged(++completed, total, listener);
		bitmapBtnBkgPressed = LiveBitmap.loadBitmap(context, "bkg_btn_pressed.png", scaleX, scaleY);
		notifyProgressChanged(++completed, total, listener);
		bitmapLandlordPass = LiveBitmap.loadBitmap(context, "landlord_pass.png", scaleX, scaleY);
		bitmapLandlordP1 = LiveBitmap.loadBitmap(context, "landlord_p1.png", scaleX, scaleY);
		bitmapLandlordP2 = LiveBitmap.loadBitmap(context, "landlord_p2.png", scaleX, scaleY);
		bitmapLandlordP3 = LiveBitmap.loadBitmap(context, "landlord_p3.png", scaleX, scaleY);
		completed +=4; 
		notifyProgressChanged(completed, total, listener);
		
		bitmapGiveCard = LiveBitmap.loadBitmap(context, "play_ahand.png", scaleX, scaleY);
		bitmapDoNotGiveCard = LiveBitmap.loadBitmap(context, "play_pass.png", scaleX, scaleY);
		bitmapRechoose = LiveBitmap.loadBitmap(context, "play_rechoose.png", scaleX, scaleY);
		bitmapTips = LiveBitmap.loadBitmap(context, "play_tip.png", scaleX, scaleY);
		completed +=4; 
		notifyProgressChanged(completed, total, listener);
		return completed;
	}

	//通知加载的进度
	private final void notifyProgressChanged(int completed, int total,
			LoadingProgressListener listener) {
		int progress = (int) (completed/(double)total*100);
		listener.onProgressChanged(progress);
	}
	
	/**
	 * 拼接卡牌文件名。
	 * @param type 卡牌花色。
	 * @param value 卡牌面值。
	 * @param isSmaller 是整张还是仅左上角。
	 * @return 返回拼接好的文件名。
	 */
	private String getCardAssetsName(CardSuit type, int value, boolean isSmaller){
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
	
	/**
	 * 获得卡牌对应的位图引用。
	 * @param card 卡牌对象
	 * @return 返回卡牌对应的位图。
	 */
	public final LiveBitmap getCorrespondBitmap(Card card){
		if (card==null){
			return null;
		}
		switch (card.getSuit()) {
			case Spade:
				return cardSpades[card.getValue() - Card.CARD_VALUE_3];
			case Heart:
				return cardHearts[card.getValue() - Card.CARD_VALUE_3];
			case Club:
				return cardClubs[card.getValue() - Card.CARD_VALUE_3];
			case Diamond:
				return cardDiamonds[card.getValue() - Card.CARD_VALUE_3];
			case Joker:
				if (card.getValue() == Card.CARD_VALUE_JOKER_B) {
					return cardJokerB;
				} else {
					return cardJokerS;
				}
			default:
				return null;
		}
	}
	
	/**
	 * 获得卡牌对应的小图引用。
	 * @param card 卡牌对象
	 * @return 返回卡牌对应的位图。
	 */
	public final LiveBitmap getCorrespondSmallBitmap(Card card){
		if (card==null){
			return null;
		}
		switch (card.getSuit()) {
			case Spade:
				return cardSmallSpades[card.getValue() - Card.CARD_VALUE_3];
			case Heart:
				return cardSmallHearts[card.getValue() - Card.CARD_VALUE_3];
			case Club:
				return cardSmallClubs[card.getValue() - Card.CARD_VALUE_3];
			case Diamond:
				return cardSmallDiamonds[card.getValue() - Card.CARD_VALUE_3];
			case Joker:
				if (card.getValue() == Card.CARD_VALUE_JOKER_B) {
					return cardJokerB;
				} else {
					return cardJokerS;
				}
			default:
				return null;
		}
	}
	
	/**
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
		void onProgressChanged(int progress/*, String currentTaskDescription*/);
		/**
		 * 当所有加载资源完成时调用。
		 */
		void onLoadCompleted();
	}
	
}
