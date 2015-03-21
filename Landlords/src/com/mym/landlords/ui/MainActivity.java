package com.mym.landlords.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.mym.landlords.ai.Game;
import com.mym.landlords.ai.Game.Status;
import com.mym.landlords.ai.Player;
import com.mym.landlords.card.Card;
import com.mym.landlords.card.CardFactory;
import com.mym.landlords.card.HandCard;
import com.mym.landlords.res.Assets;
import com.mym.landlords.res.GameGraphics;
import com.mym.landlords.res.GlobalSoundPool;
import com.mym.landlords.res.LiveBitmap;
import com.mym.landlords.widget.BitmapButton;
import com.mym.landlords.widget.GameScreen;
import com.mym.landlords.widget.BitmapButton.onClickListener;
import com.mym.landlords.widget.GameView;
import com.mym.landlords.widget.MappedTouchEvent;
import com.mym.util.PollingThread;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

public class MainActivity extends AbsGameActivity implements GameScreen{
	
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
    private long timeLimit;						//出牌时间倒计时限制
    private static final long LOGIC_THREAD_INTERVAL = 50;	//逻辑线程的间隔时间
    private static final long USER_OPRTIME_LIMIT = 30000;	//最大阻塞时间，即用户的出牌时间，30秒
    
    Handler handler = new Handler();		//temporary
    
    private class GameLogicThread extends PollingThread{

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
				break;
			case ShowingAICards:
				break;
			case Gameover:
				break;
			default:
				break;
			}
    	}
		//模拟叫牌
    	private void callLandlord(){	
    		if (isWaitingForUser){	//如果已经在等候玩家操作，则不做处理
    			return ;
    		}
			Random random = new Random(System.currentTimeMillis());
			int x = random.nextInt(3);
			Player startPlayer = (x == 0) ? playerLeft
					: (x == 1 ? playerHuman : playerRight);
			if (!startPlayer.isAiPlayer()){
				isWaitingForUser = true;
				timeLimit = USER_OPRTIME_LIMIT;
				return ;
			}
			else{
				startPlayer.nextRound(currentGame);
				int calledScore = startPlayer.getCalledScore();
				performCallLandlord(startPlayer, calledScore);
				if (calledScore == Game.BASIC_SCORE_THREE){
					currentGame.status = Status.Playing;
					startPlayer.setLandlord(landlordCards);
				}
				//TODO 增加人类玩家的倒计时。
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
		Log.d(LOG_TAG, "player1 cards:"+playerLeft.getHandCards().toString());
		Log.d(LOG_TAG, "player2 cards:"+playerHuman.getHandCards().toString());
		Log.d(LOG_TAG, "player3 cards:"+playerRight.getHandCards().toString());
		Log.d(LOG_TAG, "landlord cards:"+landlordCards.toString());
		logicThread = new GameLogicThread();
		logicThread.start();
		currentGame.status = Status.CallingLandlord;
		
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				currentGame.status = Status.Playing;
//				playerHuman.setLandlord(landlordCards);
			}
		}, 1000);
	}
	
	//初始化玩家并分配座位
	private void initPlayerSeats(){
		playerLeft = Player.newAiPlayer();
		playerHuman= Player.newHumanPlayer();
		playerRight = Player.newAiPlayer();
		playerLeft.setSeat(playerRight, playerHuman);
		playerHuman.setSeat(playerLeft, playerRight);
		playerRight.setSeat(playerHuman, playerLeft);
	}
	
	//洗牌发牌
	private void shuffleAndDealCards(){
		if (cardPack==null){
			cardPack = CardFactory.newCardPack();
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
	private void drawHumanPlayerCards(GameGraphics g, Canvas canvas){
		ArrayList<HandCard> list = playerHuman.getHandCards();
		int len = list.size();
        int offsetX = 35;
        int offsetY = GameGraphics.BASE_SCREEN_HEIGHT - 15 - GameGraphics.CARD_HEIGHT;
        cardOffset = (float) (GameGraphics.BASE_SCREEN_WIDTH - (offsetX + offsetX + GameGraphics.CARD_WIDTH)) / (len - 1);
        if (cardOffset > 55)
        {
        	offsetX = (GameGraphics.BASE_SCREEN_WIDTH - (len - 1) * 55 - GameGraphics.CARD_WIDTH) / 2;
        	cardOffset = (float) 55;
        }
        setCardsTouchZone(offsetX, offsetY, GameGraphics.BASE_SCREEN_WIDTH - offsetX, GameGraphics.BASE_SCREEN_HEIGHT - 5);
        for(int i = 0; i < len; i++) {
        	int drawY = offsetY;
        	HandCard card = list.get(i);
        	if (card.isPicked())
        	{
        		drawY -= GameGraphics.Card_PICKED_OFFSET;
        	}
        	LiveBitmap bitmap = assets.getCorrespondBitmap(card.getCard());
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
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction()==MotionEvent.ACTION_UP){
			MappedTouchEvent event = MappedTouchEvent.translateEvent(ev);
			if (inCardsTouchZone(event)){
				int index = getCardsIndex(event.x);
				if (index >=0){
					HandCard handCard = playerHuman.getHandCards().get(index);
					handCard.setPicked(!handCard.isPicked());
				}
			}
		}
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	protected List<BitmapButton> getBitmapButtons() {
		List<BitmapButton> buttons = new ArrayList<BitmapButton>();
		return buttons;
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
	}
	
}
