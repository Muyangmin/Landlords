package com.mym.landlords.ui;

import java.util.List;

import com.mym.landlords.res.GameGraphics;
import com.mym.landlords.widget.BitmapButton;

import android.app.Activity;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 采用观察者模式的Activity，通过添加所有的Bitmap到一个列表中，再在
 * {@link #dispatchTouchEvent(MotionEvent)} 中进行分发。
 * 
 * @author Muyangmin
 * @create 2015-3-17
 */
public abstract class AbsGameActivity extends Activity implements GameScreen{
	
	private static final String LOG_TAG = "GameActivity";
	
	private List<BitmapButton> bitmapButtons;
	
	/**
	 * 提供需要注册监听的Bitmap按钮。
	 * @return 按钮的列表。
	 */
	protected abstract List<BitmapButton> getBitmapButtons();
	
	@Override
	protected void onResume() {
		super.onResume();
		bitmapButtons = getBitmapButtons();
		if (bitmapButtons==null){
			Log.w(LOG_TAG, "No bitmap button registered. Do you really want?");
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		bitmapButtons.clear();
		bitmapButtons = null;
	}
	
	@Override
	public abstract void updateUI(GameGraphics graphics, Canvas canvas);
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (bitmapButtons!=null){
			for (BitmapButton button: bitmapButtons){
				button.onTouch(MappedTouchEvent.translateEvent(ev));
			}
		}
		return super.dispatchTouchEvent(ev);
	}
}
