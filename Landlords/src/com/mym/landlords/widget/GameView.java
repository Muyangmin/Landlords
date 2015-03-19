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
	
	private static final String LOG_TAG = "GameView";
	
	private SurfaceHolder holder;
	private GameGraphics graphics;
	private GameScreen gamescreen;
	private Thread renderThread;	//渲染线程。为加入Activity的生命周期支持，在这里不赋值

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
		Log.d(LOG_TAG, "surfaceCreated");
		this.holder = holder;
//		redraw();
		renderThread = new RenderThread();
		renderThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (!hasWindowFocus){
			if (renderThread!=null && renderThread.isAlive()){
				try {
					renderThread.join();	//销毁线程。
				} catch (Exception e) {
					
				}
			}
			renderThread = null;
		}
	}
	
	private final class RenderThread extends Thread{
		
		public RenderThread() {
			super("RenderThread");
		}
		
		public void run() {
			long startTime, endTime, freeTime;
			while (true) {
				try {
//					Log.v(LOG_TAG, "I'm rendering...");
					startTime = System.currentTimeMillis();
					redraw();
					endTime = System.currentTimeMillis();
					freeTime = 17 - (endTime - startTime);
					if (freeTime > 0) {
						sleep(freeTime);// 稳定帧频
					}
				} catch (Exception e) {
					Log.w(LOG_TAG, "exception while rendering:" + e.getMessage());
				}
			}
		};
	}
}
