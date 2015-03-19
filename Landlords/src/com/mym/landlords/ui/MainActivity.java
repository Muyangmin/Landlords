package com.mym.landlords.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mym.landlords.ai.Player;
import com.mym.landlords.card.Card;
import com.mym.landlords.card.CardFactory;
import com.mym.landlords.res.Assets;
import com.mym.landlords.res.Assets.LoadingProgressListener;
import com.mym.landlords.res.GameGraphics;
import com.mym.landlords.res.GlobalSoundPool;
import com.mym.landlords.widget.BitmapButton;
import com.mym.landlords.widget.GameScreen;
import com.mym.landlords.widget.MappedTouchEvent;
import com.mym.landlords.widget.BitmapButton.onClickListener;
import com.mym.landlords.widget.GameView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AbsGameActivity implements GameScreen{
	
	private static final String LOG_TAG = "MainActivity";
	private GameGraphics graphics;
	private BitmapButton button;
	private GameView gameView;
	
	private GlobalSoundPool soundPool;
	private Assets assets;
	
	private Player playerLeft;		//左路 ，AI
	private Player playerHuman;		//中路，人类玩家
	private Player playerRight;		//右路，AI
	
	private ArrayList<Card> cardPack;			//总的卡牌包
	private List<Card> landlordCards;		//地主底牌
	
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
		
		initPlayerSeats();
		shuffleAndDealCards();
		
		button = new BitmapButton(graphics, 120, 200, Assets.getInstance().cardbg);
		button.setListener(new onClickListener() {
			
			@Override
			public void onClicked(BitmapButton btn) {
				GlobalSoundPool.getInstance(MainActivity.this).playSound(Assets.getInstance().soundCardJokerB);
			}
		});
	}
	
	//初始化玩家并分配座位
	private void initPlayerSeats(){
		playerLeft = new Player();
		playerHuman= new Player();
		playerRight = new Player();
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
		playerLeft.setHandCards(new ArrayList<>(cardPack.subList(0, 17)));
		playerHuman.setHandCards(new ArrayList<>(cardPack.subList(17, 34)));
		playerRight.setHandCards(new ArrayList<>(cardPack.subList(35, 52)));
		landlordCards = cardPack.subList(52, 55);
	}

	@Override
	protected List<BitmapButton> getBitmapButtons() {
		List<BitmapButton> buttons = new ArrayList<BitmapButton>();
		buttons.add(button);
		return buttons;
	}
	
	@Override
	public void updateUI(GameGraphics graphics, Canvas canvas) {
		button.onDraw(canvas);
	}
}
