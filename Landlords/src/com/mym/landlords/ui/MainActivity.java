package com.mym.landlords.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.mym.landlords.ai.Game;
import com.mym.landlords.ai.Game.Status;
import com.mym.landlords.ai.Player;
import com.mym.landlords.card.Bomb;
import com.mym.landlords.card.Card;
import com.mym.landlords.card.CardFactory;
import com.mym.landlords.card.CardType;
import com.mym.landlords.card.Pair;
import com.mym.landlords.card.Rocket;
import com.mym.landlords.card.Single;
import com.mym.landlords.card.Straight;
import com.mym.landlords.res.Assets;
import com.mym.landlords.res.GameGraphics;
import com.mym.landlords.res.GlobalSoundPool;
import com.mym.landlords.res.LiveBitmap;
import com.mym.landlords.widget.BitmapButton;
import com.mym.landlords.widget.BitmapButton.onClickListener;
import com.mym.landlords.widget.GameScreen;
import com.mym.landlords.widget.GameView;
import com.mym.landlords.widget.MappedTouchEvent;
import com.mym.util.PollingThread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class MainActivity extends Activity implements GameScreen{
	
	private static final String LOG_TAG = "MainActivity";
	private GameGraphics graphics;
	private GameView gameView;
	
	private GlobalSoundPool soundPool;
	private Assets assets;
	
	private Player playerLeft;		//左路 ，AI
	private Player playerHuman;		//中路，人类玩家
	private Player playerRight;		//右路，AI
	
	private ArrayList<Card> cardPack;			//总的卡牌包
	private List<Card> landlordCards;			//地主底牌
	private Game currentGame;					//当前游戏记录
	
    private Rect cardsTouchZone = new Rect();	//用于判断点击事件是否在玩家手牌区域内
    private float cardOffset;					//用于判断玩家点选的是哪张卡牌
    private GameLogicThread logicThread;
    private boolean isWaitingForUser;			//当前逻辑线程是否被玩家阻塞
    
    private ArrayList<Player> winners = new ArrayList<>();;			//获胜的玩家
    
//    Handler handler = new Handler();		//temporary

    private ArrayList<BitmapButton> btnCallLandlords;	//叫地主系列按钮
    private ArrayList<BitmapButton> btnGiveCards;		//出牌系列按钮
    
    /**
     * 逻辑控制线程。
     * @author Muyangmin
     * @create 2015-3-22
     */
    private class GameLogicThread extends PollingThread{
        private static final long LOGIC_THREAD_INTERVAL = 100;	//逻辑线程的间隔时间
    	private Player currentPlayer = null;	//记录当前已经进行的循环值
    	private Player startPlayer;				//当前循环第一次操作的玩家
    	private Player tempLandlord = null;			//记录叫地主分数最高的玩家
    	private CardType currentType;			//当前正在打的牌型

    	public GameLogicThread() {
			super("GameLogicThread", LOGIC_THREAD_INTERVAL);
		}
    	
    	@Override
    	protected void action() {
    		switch (currentGame.status) {
			case Preparing://该阶段无逻辑需要控制
				break;
			case CallingLandlord:
				callLandlord();
				break;
			case Playing:
				playing();
				break;
			case ShowingAICards:
				break;
			case Gameover:
				break;
			default:
				break;
			}
    	}
    	
    	private void playing(){
    		if (isWaitingForUser){
//    			Log.v(LOG_TAG, "waiting for user...");
    			return ;
    		}
    		//检查游戏是否应该结束
    		Player zeroCardPlayer = getWinnerPlayer();
    		if (zeroCardPlayer!=null){
    			winners.clear(); 
				winners.add(zeroCardPlayer);
				//如果率先达到0手牌的玩家不是地主，则另一个农民同时获胜
    			if (!zeroCardPlayer.isLandlord()){
					winners.add(zeroCardPlayer.getNextPlayer().isLandlord() 
							? zeroCardPlayer.getPriorPlayer() 
									: zeroCardPlayer.getNextPlayer());
    			}
    			currentGame.status = Status.Gameover;
    			//播放结束音效
				soundPool.playSound(winners.contains(playerHuman) ? assets.soundPlayWin
								: assets.soundPlayLose);
    			Log.i(LOG_TAG, "winner:"+winners);
    			return ;
    		}
    		
    		//如果已经在出牌中
    		if (startPlayer!=null){
    			CardType tempCardType;
    			//如果当前的玩家是AI，则执行跟牌策略
    			if (currentPlayer.isAiPlayer()){
    				boolean isFirst = currentType==null;
    				tempCardType = currentPlayer.followCards(currentType);
    				currentPlayer.giveOutCards(tempCardType);
    				performGiveCard(tempCardType, isFirst);
    				//检查游戏的结束
    				if (currentPlayer.getHandCards().size()==0){
    					return ;
    				}
    			}
    			//否则直接取出人类玩家的卡牌类型。
    			else{
					tempCardType = currentPlayer.getLastCards();
					synchronized (activeButtons) {
						activeButtons.removeAll(btnGiveCards);
					}
    			}//end of current Player
    			switchToNextPlayer();
				if (tempCardType!=null){
					currentType = tempCardType;
				}
    			if (currentPlayer.getPriorPlayer().getLastCards()==null
    					&& currentPlayer.getNextPlayer().getLastCards()==null){
    				Log.d(LOG_TAG, "no bigger cards, exec next round.");
    				//重置当前的卡牌。
    				currentType = null;
    			}
    			if (!currentPlayer.isAiPlayer()){
					isWaitingForUser = true;
					setActiveGiveCardButtons(currentType==null);
				}
    		}
    		else{
    			startPlayer = currentGame.landlordPlayer;
    			currentPlayer = startPlayer;
    			if (!currentPlayer.isAiPlayer()){
    				isWaitingForUser = true;
					setActiveGiveCardButtons(currentType==null);
    			}
    		}
    	}
    	
    	private void setActiveGiveCardButtons(final boolean isFirstOfCurrentRound){
    		if (btnGiveCards == null){
    			btnGiveCards = new ArrayList<>(4);
        		BitmapButton btnPass = new BitmapButton(graphics, 95, 240, assets.bitmapDoNotGiveCard);
    			BitmapButton btnGiveCard = new BitmapButton(graphics, 260, 240, assets.bitmapGiveCard);
    			BitmapButton btnRechoose = new BitmapButton(graphics, 380, 240, assets.bitmapRechoose);
    			BitmapButton btnTips = new BitmapButton(graphics, 500, 240, assets.bitmapTips);
    			btnPass.setOnClickListener(new onClickListener() {
					
					@Override
					public void onClicked(BitmapButton btn) {
						//TODO whether should clear pick status?
						//显式指定出牌为null
						currentPlayer.giveOutCards(null);
						performGiveCard(null, false);
						isWaitingForUser = false;
					}
				});
    			btnGiveCard.setOnClickListener(new onClickListener() {
					
					@Override
					public void onClicked(BitmapButton btn) {
						ArrayList<Card> pickedList = getPickedCards(currentPlayer);
	    				if (pickedList.size()==0){
	    					Log.d(LOG_TAG, "no card selected.");
	    					return;
	    					//TODO hint 没有选择任何卡牌
	    				}
	    				CardType tempCardType = CardType.createObjectFromCards(pickedList);
	    				if (tempCardType!=null && (!tempCardType.canAgainstType(currentType)) ){
	    					Log.d(LOG_TAG, "cardtype not match the rule, currentType="+currentType);
	    					return ;
	    					//TODO hint 不符合规则
	    				}
	    				else{
	    					currentPlayer.giveOutCards(tempCardType);
	    					//打出最后一手牌时无需播放卡牌音效
	    					if (currentPlayer.getHandCards().size()>0){
	    						performGiveCard(tempCardType, currentType==null);
	    					}
	    					isWaitingForUser = false;
	    				}
					}
				});
    			btnRechoose.setOnClickListener(new onClickListener() {
					
					@Override
					public void onClicked(BitmapButton btn) {
						synchronized (currentPlayer) {
							for (Card card : currentPlayer.getHandCards()){
								card.setPicked(false);
							}
						}
					}
				});
    			btnTips.setOnClickListener(new onClickListener() {
					
					@Override
					public void onClicked(BitmapButton btn) {
						Log.v(LOG_TAG, "button clicked ");
					}
				});

    			btnGiveCards.add(btnPass);
    			btnGiveCards.add(btnTips);
    			btnGiveCards.add(btnRechoose);
    			btnGiveCards.add(btnGiveCard);
    		}
    		synchronized (activeButtons) {
        		activeButtons.clear();
        		activeButtons.addAll(btnGiveCards);
    			//第一个出牌的时候不允许不出。
    			if (isFirstOfCurrentRound){
    				activeButtons.remove(btnGiveCards.get(0));
    			}
			}
//    		Log.d(LOG_TAG, "give card buttons added.");
    	}
    	
    	//获取玩家选中的手牌列表
    	private ArrayList<Card> getPickedCards(Player player){
    		ArrayList<Card> pickedList = new ArrayList<>();
    		for (Card card: player.getHandCards()){
    			if (card.isPicked()){
    				pickedList.add(card);
    			}
    		}
    		return pickedList;
    	}
    	
    	//检查手牌为0的玩家
    	private Player getWinnerPlayer(){
    		if (playerLeft.getHandCards().size()==0){
    			return playerLeft;
    		}
    		else if (playerRight.getHandCards().size()==0){
    			return playerRight;
    		}
    		else if (playerHuman.getHandCards().size()==0){
    			return playerHuman;
    		}
    		return null;
    	}
    	
    	//负责标记当前玩家的引用及切换之间的暂停。
    	private final void switchToNextPlayer(){
    		//如果接下来进行操作的是AI，则让AI等待一段时间再操作（主要是避免音效重叠）。
			currentPlayer = currentPlayer.getNextPlayer();
			if (currentPlayer.isAiPlayer()){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
    	}
    	
		//模拟叫牌
    	private void callLandlord(){
			if (isWaitingForUser){	//如果已经在等候玩家操作，则不做处理
//    			Log.v(LOG_TAG, "waiting for user...");
   				return ;
    		}
    		if (startPlayer!=null){	//循环已经开始
    			//只有AI才需要执行回合，非AI玩家的操作已经在UI线程执行。
    			if (currentPlayer.isAiPlayer()){
					currentPlayer.callLandlord(tempLandlord == null ? 0
							: tempLandlord.getCalledScore());
    			}
    			//得到该玩家的叫牌分数
    			int calledScore = currentPlayer.getCalledScore();
				//记录叫牌分数最高者，作为暂时的地主，如果到最后仍无人叫，则将其设置为地主。
    			if (calledScore> Game.BASIC_SCORE_NONE){
    				if (tempLandlord==null || calledScore>tempLandlord.getCalledScore()){
    					tempLandlord = currentPlayer;
    				}
    			}
				performCallLandlord(currentPlayer, calledScore);
				boolean isFinalCall = currentPlayer.getNextPlayer() == startPlayer;
				if (isFinalCall && (tempLandlord==null) ){
					//TODO alert it and decide what to do.
//					currentGame.status = Status.Gameover;
					return ;
				}
				//如果已经叫到三分或已经是最后一个，开始游戏
				if (calledScore == Game.BASIC_SCORE_THREE || isFinalCall) {
					tempLandlord.setLandlord(landlordCards);
					currentGame.landlordPlayer = tempLandlord;
					Log.d(LOG_TAG, "Landlord is "+ tempLandlord.getPlayerName());
					Log.d(LOG_TAG, "Landlord:"+tempLandlord.getHandCards().toString());
					currentGame.status = Status.Playing;
					startPlayer = null;
					currentPlayer = null;
					return;
				}
				else{
					/*//如果接下来进行操作的是AI，则让AI等待一段时间再操作（主要是避免音效重叠）。
					if (currentPlayer.getNextPlayer().isAiPlayer()){
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//由下一个人叫地主
					currentPlayer = currentPlayer.getNextPlayer();*/
					switchToNextPlayer();
					if (!currentPlayer.isAiPlayer()){
						isWaitingForUser = true;
						setActiveCallButtons(tempLandlord == null ? Game.BASIC_SCORE_NONE
								: tempLandlord.getCalledScore());
					}
				}
				
    		}
    		else{	//循环尚未开始，做初始化工作。
    			Log.d(LOG_TAG, "loopinit");
    			Random random = new Random(System.currentTimeMillis());
    			int x = random.nextInt(3);
    			startPlayer = (x == 0) ? playerLeft
    					: (x == 1 ? playerHuman : playerRight);
    			Log.d(LOG_TAG, "startPlayer index = "+x);
				currentPlayer = startPlayer;
    			if (!currentPlayer.isAiPlayer()){
    				isWaitingForUser = true;
    				setActiveCallButtons(Game.BASIC_SCORE_NONE);
    			}
    		}
    	}
    	
    	//init human action buttons
    	private void setActiveCallButtons(int minScore){
    		BitmapButton btnCallPass = new BitmapButton(graphics, 95, 240, assets.bitmapLandlordPass);
			BitmapButton btnCallP1 = new BitmapButton(graphics, 260, 240, assets.bitmapLandlordP1);
			BitmapButton btnCallP2 = new BitmapButton(graphics, 380, 240, assets.bitmapLandlordP2);
			BitmapButton btnCallP3 = new BitmapButton(graphics, 500, 240, assets.bitmapLandlordP3);
			btnCallLandlords = new ArrayList<>(4);
			btnCallPass.setOnClickListener(new CallLandlordBtnListener(Game.BASIC_SCORE_NONE));
			btnCallLandlords.add(btnCallPass);
			if (minScore < Game.BASIC_SCORE_ONE){
				btnCallP1.setOnClickListener(new CallLandlordBtnListener(Game.BASIC_SCORE_ONE));
				btnCallLandlords.add(btnCallP1);	
			}
			if (minScore < Game.BASIC_SCORE_TWO){
				btnCallP2.setOnClickListener(new CallLandlordBtnListener(Game.BASIC_SCORE_TWO));
				btnCallLandlords.add(btnCallP2);
			}
			//三分一定是可选的，因为上家如果叫了三分则根本不会轮到下家
			btnCallP3.setOnClickListener(new CallLandlordBtnListener(Game.BASIC_SCORE_THREE));
			btnCallLandlords.add(btnCallP3);
			synchronized(activeButtons){
				activeButtons.addAll(btnCallLandlords);
			}
			Log.d(LOG_TAG, "activeButton added.now size is "+activeButtons.size());
    	}
    	
		private class CallLandlordBtnListener implements
				BitmapButton.onClickListener {

			private int score;

			public CallLandlordBtnListener(int score) {
				this.score = score;
			}

			@Override
			public void onClicked(BitmapButton btn) {
				Log.d(LOG_TAG, "btn on click");
				currentPlayer.setCalledScore(score);
				isWaitingForUser = false;
				Log.d(LOG_TAG, "human player operation completed.");
				// 加锁避免并发修改（主要是主线程需要迭代该列表）
				synchronized (activeButtons) {
					activeButtons.removeAll(btnCallLandlords);
					Log.d(LOG_TAG, "btns clear.");
				}
			}
		}
        
    }
	protected static Intent getIntent(Context context){
		Intent intent = new Intent(context, MainActivity.class);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		graphics = GameGraphics.newInstance();
		gameView = new GameView(this, graphics, this);
		setContentView(gameView);
		soundPool = GlobalSoundPool.getInstance(this);
		assets = Assets.getInstance();
		currentGame = Game.newGame();
		currentGame.status = Status.Preparing;
		initPlayerSeats();
		shuffleAndDealCards();
//		currentGame.status = Status.Playing;
		Log.d(LOG_TAG, playerLeft.getPlayerName()+" cards:"+playerLeft.getHandCards().toString());
		//test code
//		playerLeft.makeCards();
		Log.d(LOG_TAG, playerHuman.getPlayerName()+" cards:"+playerHuman.getHandCards().toString());
		Log.d(LOG_TAG, playerRight.getPlayerName()+" cards:"+playerRight.getHandCards().toString());
		//test code
//		playerRight.makeCards();
		Log.d(LOG_TAG, "landlord cards:"+landlordCards.toString());
		logicThread = new GameLogicThread();
		logicThread.start();
		currentGame.status = Status.CallingLandlord;
		
//		handler.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
////				currentGame.status = Status.Playing;
////				playerHuman.setLandlord(landlordCards);
//			}
//		}, 1000);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (logicThread!=null && logicThread.isAlive()){
			logicThread.requestStopThread();
		}
	}
	
	//初始化玩家并分配座位
	private void initPlayerSeats(){
		playerLeft = Player.newAiPlayer("aiLeft");
		playerHuman= Player.newHumanPlayer("Human");
		playerRight = Player.newAiPlayer("aiRight");
		
		playerLeft.setSeat(playerRight, playerHuman);
		playerHuman.setSeat(playerLeft, playerRight);
		playerRight.setSeat(playerHuman, playerLeft);
	}
	
	//洗牌发牌
	private void shuffleAndDealCards(){
		if (cardPack==null){
			cardPack = CardFactory.newCardPack();
		}
		for (Card card: cardPack){
			card.setPicked(false);
		}
		//洗牌五次
		for (int i=0; i<5; i++){
			Collections.shuffle(cardPack);
		}
		playerLeft.setHandCards(cardPack.subList(0, 17));
		playerHuman.setHandCards(cardPack.subList(17, 34));
		playerRight.setHandCards(cardPack.subList(34, 51));
		landlordCards = cardPack.subList(51, 54);
	}
	private Random randomSoundGenerator = new Random();
	private void performGiveCard(CardType type, boolean isFirst) {
		if (type==null){
			soundPool.playSound(assets.soundPlayPass);
			return ;
		}
		if (!isFirst){
			if (type instanceof Bomb) {
				soundPool.playSound(assets.soundTypeBomb);
				soundPool.playSound(assets.soundEffectBoom);
			} else if (type instanceof Rocket) {
				soundPool.playSound(assets.soundTypeRocket);
				soundPool.playSound(assets.soundEffectBoom);
			}
			else{
				int i = randomSoundGenerator.nextInt(assets.soundPlayBigger.length);
				soundPool.playSound(assets.soundPlayBigger[i]);
			}
			return ;
		}
		if (type instanceof Pair){
			soundPool.playSound(assets.soundTypePair);
		}
		else if (type instanceof Single){
			switch (type.getCardList().get(0).getValue()){
			case Card.CARD_VALUE_JOKER_S:
				soundPool.playSound(assets.soundCardJokerS);
				break;
			case Card.CARD_VALUE_JOKER_B:
				soundPool.playSound(assets.soundCardJokerB);
				break;
			default:
				soundPool.playSound(assets.soundTypeSingle);
				break;
			}
		}//end of Single
		else if (type instanceof Straight){
			soundPool.playSound(assets.soundTypeShun1);
		}
		else if (type instanceof Bomb){
			soundPool.playSound(assets.soundTypeBomb);
			soundPool.playSound(assets.soundEffectBoom);
		}
		else if (type instanceof Rocket){
			soundPool.playSound(assets.soundTypeRocket);
			soundPool.playSound(assets.soundEffectBoom);
		}
		else {
			//默认出牌音效
			soundPool.playSound(assets.soundTypeSingle);
		}
	}
	
	/**
	 * 执行叫地主的操作。
	 * @param player 指定的玩家
	 * @param score 玩家叫的分数
	 */
	private void performCallLandlord(Player player, int score){
		int soundId;
		switch (score) {
		case Game.BASIC_SCORE_NONE:
			soundId=assets.soundLandloadPass;
			break;
		case Game.BASIC_SCORE_ONE:
			soundId=assets.soundLandloadP1;
			break;
		case Game.BASIC_SCORE_TWO:
			soundId=assets.soundLandloadP2;
			break;
		case Game.BASIC_SCORE_THREE:
			soundId=assets.soundLandloadP3;
			break;
		default:
			throw new RuntimeException("unhanlded score "+score);
		}
		soundPool.playSound(soundId);
	}
	
	/**
	 * 绘制AI卡牌背面图，按两列摆放。
	 * @param x 卡牌左边界
	 * @param y 卡牌上边界
	 * @param cardsNum 卡牌数目。
	 */
	private void drawBackLittleCards(GameGraphics g, Canvas canvas, int x, int y, int cardsNum)
    {
    	for (int i = 0; i < cardsNum; i++)
    	{
    		int offsetX = i % 2;
    		int offsetY = (int) (i / 2);
    		g.drawBitmap(canvas, assets.cardbg, x + offsetX * 25, y + offsetY * 8, 35, 48);
    	}
    }
    /**
     * 绘制底牌。
     */
	private void drawBottomCards(GameGraphics g, Canvas canvas)
    {
		int offset;
		switch (currentGame.status) {
		case Preparing:
		case CallingLandlord:
			offset = assets.cardbg.getRawWidth() + 5;
    		for (int i = 0; i < 3; i++)
    		{
    			g.drawBitmap(canvas, assets.cardbg, 290 + i * offset, 115);
    		}
			break;
		case Playing:
		case Gameover:
			offset = 30 + 3;
    		for (int i = 0; i < 3; i++)
    		{
    			 Card card = landlordCards.get(i);
    			 g.drawBitmap(canvas, assets.getCorrespondSmallBitmap(card), 300 + i * offset, 5, 30, 40);
    		}
			break;
		default:
			break;
		}
    }
	//绘制玩家的卡牌（即正面的卡牌）
	private synchronized void drawHumanPlayerCards(GameGraphics g, Canvas canvas){
		ArrayList<Card> list = playerHuman.getHandCards();
//		Log.d(LOG_TAG, "human cards:"+list.size());
		int len = list.size();
        int offsetX = 35;
        int offsetY = GameGraphics.BASE_SCREEN_HEIGHT - 15 - GameGraphics.CARD_HEIGHT;
        cardOffset = (float) (GameGraphics.BASE_SCREEN_WIDTH - 
        		(offsetX + offsetX + GameGraphics.CARD_WIDTH)) / (len - 1);
        if (cardOffset > 55)
        {
        	offsetX = (GameGraphics.BASE_SCREEN_WIDTH - (len - 1) * 55
        			- GameGraphics.CARD_WIDTH) / 2;
        	cardOffset = (float) 55;
        }
        setCardsTouchZone(offsetX, offsetY, 
        		GameGraphics.BASE_SCREEN_WIDTH - offsetX, 
        		GameGraphics.BASE_SCREEN_HEIGHT - 5);
        for(int i = 0; i < len; i++) {
        	int drawY = offsetY;
        	Card card = list.get(i);
        	if (card.isPicked())
        	{
        		drawY -= GameGraphics.Card_PICKED_OFFSET;
        	}
        	LiveBitmap bitmap = assets.getCorrespondBitmap(card);
			g.drawBitmap(canvas, bitmap, offsetX + i * ((int) cardOffset),
					drawY, GameGraphics.CARD_WIDTH, GameGraphics.CARD_HEIGHT);
        }
	}
	
	//设置卡牌点击矩阵
	private void setCardsTouchZone(int left, int top, int right, int bottom) {
		cardsTouchZone.left = left;
		cardsTouchZone.top = top;
		cardsTouchZone.right = right;
		cardsTouchZone.bottom = bottom;
	}

	/**画玩家和AI形象。 */
	private void drawPlayers(GameGraphics g, Canvas canvas) {
		int playerLX = GameGraphics.SCREEN_PADDING_HORIZONTAL;
		int playerLY = GameGraphics.SCREEN_MARGIN_VERTICAL;
		int playerRX = GameGraphics.BASE_SCREEN_WIDTH
				- GameGraphics.SCREEN_PADDING_HORIZONTAL
				- assets.playerRight.getRawWidth();
		int playerRY = GameGraphics.SCREEN_MARGIN_VERTICAL;
		int playerMX = GameGraphics.SCREEN_PADDING_HORIZONTAL;
		int playerMY = GameGraphics.BASE_SCREEN_HEIGHT - 15
				- GameGraphics.CARD_HEIGHT - 10
				- assets.playerHuman.getRawHeight();
		g.drawBitmap(canvas, assets.playerLeft, playerLX, playerLY);
		g.drawBitmap(canvas, assets.playerHuman, playerMX, playerMY);
		g.drawBitmap(canvas, assets.playerRight, playerRX, playerRY);
		
		// 画地主标记
		if (playerLeft.isLandlord()) {
			g.drawBitmap(canvas, assets.iconLandlord, playerLX
					+ assets.playerLeft.getRawWidth(), playerLY);
		} else if (playerHuman.isLandlord()) {
			g.drawBitmap(canvas, assets.iconLandlord, playerMX
					+ assets.playerHuman.getRawWidth(), playerMY);
		} else if (playerRight.isLandlord()) {
			g.drawBitmap(canvas, assets.iconLandlord, playerRX
					- assets.iconLandlord.getRawWidth(), playerRY);
		}
	}
	/**
	 * 用于判断点击事件是否发生在卡牌点击区域内。
	 * @param event 映射后的点击事件
	 * @return 如果在指定区域内，则返回true
	 */
	private boolean inCardsTouchZone(MappedTouchEvent event) {
		int x = event.x;
		int y = event.y;
		if (x > cardsTouchZone.left && x < cardsTouchZone.right
				&& y < cardsTouchZone.bottom && y > cardsTouchZone.top) {
			return true;
		} else {
			return false;
		}
	}

	//从玩家的点击位置判断其点选的卡牌的位置索引
	private int getCardsIndex(int x) {
		int index = (int) ((x - cardsTouchZone.left) / cardOffset);
		int size = playerHuman.getHandCards().size();

		if (x > (cardsTouchZone.right - (GameGraphics.CARD_WIDTH - cardOffset))) {
			return size - 1;
		} else if (index < size && index >= 0) {
			return index;
		} else {
			return -1;
		}
	}
	
	private void drawPlayerOutCards(GameGraphics graphics, Canvas canvas) {
		int offsetX = 0;
		int offsetY = 0;

		//等待玩家操作时无需绘制玩家的手牌，否则会叠在按钮下面很不好看
		if (playerHuman.getLastCards() != null && (!isWaitingForUser)) {
			ArrayList<Card> cards = playerHuman.getLastCards().getCardList();
			int len = cards.size();
			offsetX = (int) ((GameGraphics.BASE_SCREEN_WIDTH - len * 35) / 2);
			offsetY = 250;
			for (int i = 0; i < len; i++) {
				Card card = cards.get(i);

				LiveBitmap cardBitmap = assets.getCorrespondBitmap(card);
				graphics.drawBitmap(canvas, cardBitmap, offsetX + i * 35,
						offsetY, 63, 86);
			}
		}
		if (playerLeft.getLastCards() != null) {
			ArrayList<Card> cards = playerLeft.getLastCards().getCardList();
			int len = cards.size();
			offsetX = 120;
			offsetY = 100;
			for (int i = 0; i < len; i++) {
				Card card = cards.get(i);

				LiveBitmap cardBitmap = assets.getCorrespondBitmap(card);
				if (i < 6) {
					graphics.drawBitmap(canvas, cardBitmap, offsetX + i * 35,
							offsetY, 63, 86);
				} else if (i < 12) {
					graphics.drawBitmap(canvas, cardBitmap, offsetX + (i - 6)
							* 35, offsetY + 25, 63, 86);
				} else if (i < 18) {
					graphics.drawBitmap(canvas, cardBitmap, offsetX + (i - 12)
							* 35, offsetY + 50, 63, 86);
				} else {
					graphics.drawBitmap(canvas, cardBitmap, offsetX + (i - 18)
							* 35, offsetY + 75, 63, 86);
				}
			}
		}
		if (playerRight.getLastCards() != null) {
			ArrayList<Card> cards = playerRight.getLastCards().getCardList();
			int len = cards.size();
			offsetX = GameGraphics.BASE_SCREEN_WIDTH - 120 - 63;
			offsetY = 100;
			for (int i = 0; i < len; i++) {
				Card card = cards.get(i);

				LiveBitmap cardBitmap = assets.getCorrespondBitmap(card);
				;
				if (i < 6) {
					if (len < 6) {
						graphics.drawBitmap(canvas, cardBitmap, offsetX
								- (len - 1 - i) * 35, offsetY, 63, 86);
					} else {
						graphics.drawBitmap(canvas, cardBitmap, offsetX
								- (6 - 1 - i) * 35, offsetY, 63, 86);
					}
				} else if (i < 12) {
					graphics.drawBitmap(canvas, cardBitmap, offsetX - 5 * 35
							+ (i - 6) * 35, offsetY + 25, 63, 86);
				} else if (i < 18) {
					graphics.drawBitmap(canvas, cardBitmap, offsetX - 5 * 35
							+ (i - 12) * 35, offsetY + 50, 63, 86);
				} else {
					graphics.drawBitmap(canvas, cardBitmap, offsetX - 5 * 35
							+ (i - 18) * 35, offsetY + 75, 63, 86);
				}
			}
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		for (BitmapButton button: activeButtons){
			if (button.onTouch(MappedTouchEvent.translateEvent(ev))){
				break;
			}
		}
		if (ev.getAction()==MotionEvent.ACTION_UP){
			MappedTouchEvent event = MappedTouchEvent.translateEvent(ev);
			if (inCardsTouchZone(event)){
				int index = getCardsIndex(event.x);
				if (index >=0){
					Card handCard = playerHuman.getHandCards().get(index);
					handCard.setPicked(!handCard.isPicked());
				}
			}
		}
		return super.dispatchTouchEvent(ev);
	}
	
	private ArrayList<BitmapButton> activeButtons = new ArrayList<BitmapButton>(); // 当前正在监听的BitmapButton。
	
	private void drawActiveButtons(GameGraphics graphics, Canvas canvas){
		for (BitmapButton button: activeButtons){
			button.onPaint(canvas);
		}
	}
	
	@Override
	public void updateUI(GameGraphics graphics, Canvas canvas) {
		if (currentGame==null){
			Log.w(LOG_TAG, "updateUI called before game instance created.");
			return ;
		}
		drawBottomCards(graphics, canvas);
		//画手牌背面和数字
		if (currentGame.status != Status.ShowingAICards){
			drawBackLittleCards(graphics, canvas,
					GameGraphics.SCREEN_PADDING_HORIZONTAL + 10, 130,
					playerLeft.getHandCards().size());
			drawBackLittleCards(graphics, canvas, 
					GameGraphics.BASE_SCREEN_WIDTH
							- GameGraphics.SCREEN_PADDING_HORIZONTAL + 10
							- assets.playerRight.getRawWidth(),
					130, playerRight.getHandCards().size());
			graphics.drawText(canvas, assets.bitmapNumbers,
					String.valueOf(playerLeft.getHandCards().size()),
					GameGraphics.AIPLAYER_LEFT_CARDNUM_X,
					GameGraphics.AIPLAYER_CARDNUM_MARGIN_Y);
			graphics.drawText(canvas, assets.bitmapNumbers,
					String.valueOf(playerRight.getHandCards().size()),
					GameGraphics.AIPLAYER_RIGHT_CARDNUM_X,
					GameGraphics.AIPLAYER_CARDNUM_MARGIN_Y);
		}
		drawPlayers(graphics, canvas);
		drawHumanPlayerCards(graphics, canvas);
		drawPlayerOutCards(graphics, canvas);
		drawActiveButtons(graphics, canvas);
	}
	
}
