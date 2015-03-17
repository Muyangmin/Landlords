package com.mym.landlords.ui;

import java.util.ArrayList;
import java.util.List;

import com.mym.landlords.res.Assets;
import com.mym.landlords.res.Assets.LoadingProgressListener;
import com.mym.landlords.res.GameGraphics;
import com.mym.landlords.res.GlobalSoundPool;
import com.mym.landlords.widget.BitmapButton;
import com.mym.landlords.widget.BitmapButton.onClickListener;
import com.mym.landlords.widget.GameView;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Assets.loadAssets(this, new LoadingProgressListener() {
			
			@Override
			public void onProgressChanged(int progress, String currentTaskDescription) {
				// TODO Auto-generated method stub
//				Log.d(LOG_TAG, currentTaskDescription+" "+progress);
			}
			
			@Override
			public void onLoadCompleted() {
				Log.d(LOG_TAG, "loading completed.");
			}
		});
		Bitmap gameBkg = Assets.getInstance().bkgGameTable.getBitmap();
		Point outPoint = new Point();
		getWindowManager().getDefaultDisplay().getSize(outPoint);	//测量大小
		graphics = new GameGraphics(gameBkg, outPoint);
		MappedTouchEvent.initMapper(graphics.getScaleX(), graphics.getScaleY());
		gameView = new GameView(this, graphics, this);
		button = new BitmapButton(graphics, 120, 200, Assets.getInstance().cardbg);
		button.setListener(new onClickListener() {
			
			@Override
			public void onClicked(BitmapButton btn) {
				GlobalSoundPool.getInstance(MainActivity.this).playSound(Assets.getInstance().cardJokerS);
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
