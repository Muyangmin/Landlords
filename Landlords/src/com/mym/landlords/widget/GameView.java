package com.mym.landlords.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.mym.landlords.res.Assets;
import com.mym.landlords.res.GameGraphics;

/**
 * 主游戏界面绘制控件。
 * @author Muyangmin
 * @create 2015-3-17
 */
public class GameView extends SurfaceView implements RedrawableView,
		SurfaceHolder.Callback {
	
	private SurfaceHolder holder;
	private GameGraphics graphics;
	private GameScreen gamescreen;
	
	public GameView(Context context, GameGraphics graphics, GameScreen listener) {
		super(context);
		this.graphics = graphics;
		this.holder = getHolder();
		this.holder.addCallback(this);
		this.gamescreen = listener;
	}
	
	@Override
	public void redraw() {
		Canvas canvas = null;
		try {
			canvas = holder.lockCanvas();
			Log.d(VIEW_LOG_TAG, ""+(canvas==null));
			graphics.drawBitmap(canvas, Assets.getInstance().bkgGameTable, 0, 0);
			if (gamescreen!=null){
				gamescreen.updateUI(graphics, canvas);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//强制释放Canvas，保证下帧正确运行
			if (canvas != null) {
				holder.unlockCanvasAndPost(canvas);
			}
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("Main", "surfaceCreated");
		this.holder = holder;
		redraw();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
}
