package com.mym.landlords.ui;

import java.util.ArrayList;
import java.util.List;

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
	
	protected static Intent getIntent(Context context){
		Intent intent = new Intent(context, MainActivity.class);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Point outPoint = new Point();
		getWindowManager().getDefaultDisplay().getSize(outPoint);	//测量大小
		graphics = GameGraphics.newInstance();
		gameView = new GameView(this, graphics, this);
		button = new BitmapButton(graphics, 120, 200, Assets.getInstance().cardbg);
		button.setListener(new onClickListener() {
			
			@Override
			public void onClicked(BitmapButton btn) {
				GlobalSoundPool.getInstance(MainActivity.this).playSound(Assets.getInstance().soundCardJokerB);
			}
		});
		setContentView(gameView);
	}

	@Override
	protected List<BitmapButton> getBitmapButtons() {
		List<BitmapButton> buttons = new ArrayList<BitmapButton>();
		buttons.add(button);
		return buttons;
	}
	
	@Override
	public void updateUI(GameGraphics graphics, Canvas canvas) {
//		graphics.drawBitmap(canvas, Assets.getInstance().spades[1], 100, 120);
		button.onDraw(canvas);
	}
}
