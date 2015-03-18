package com.mym.landlords.res;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;

/**
 * 负责图像的绘制和自动缩放控制。
 * @author Muyangmin
 * @create 2015-3-17
 */
public final class GameGraphics {

	/** 基准屏幕宽度。 */
	public static final int BASE_SCREEN_WIDTH = 800;
	/** 基准屏幕高度。 */
	public static final int BASE_SCREEN_HEIGHT = 480;

//	private Bitmap frameBuffer; // 底色
//	private Canvas canvas; // 画布对象
	private Paint paint; // 画笔对象
	private float scaleX; // X缩放比
	private float scaleY; // Y缩放比
	private Rect srcRect = new Rect(); // 源矩阵对象
	private Rect dstRect = new Rect(); // 目标矩阵对象
	
	private static GameGraphics instance;
	
	//初始化屏幕缩放比例。该方法仅被Assets调用。
	protected static synchronized void initGraphicsScale(Point outSize){
		instance = new GameGraphics(outSize);
	}

	/**
	 * 获得画笔对象。
	 */
	public static GameGraphics newInstance(){
		if (instance==null){
			throw new RuntimeException("must call initGraphicsScale() before create instance.");
		}
		return instance;
	}
	private GameGraphics(Point outSize){
		scaleX = outSize.x / (float) BASE_SCREEN_WIDTH;
		scaleY = outSize.y / (float) BASE_SCREEN_HEIGHT;
//		this.paint = new Paint();
	}

//	/**
//	 * 绘制指定的Bitmap。
//	 * @param bitmap 要绘制的图片。
//	 * @param x 目标左边缘位置
//	 * @param y 目标上边缘位置
//	 * @param srcX 缩放前左边缘位置
//	 * @param srcY 缩放前上边缘位置
//	 * @param srcWidth 缩放前宽度
//	 * @param srcHeight 缩放前高度
//	 */
//	public void drawBitmap(LiveBitmap bitmap, int x, int y, int srcX, int srcY,
//			int srcWidth, int srcHeight) {
//		srcRect.left = (int) (srcX * scaleX + 0.5f);
//		srcRect.top = (int) (srcY * scaleY + 0.5f);
//		srcRect.right = (int) ((srcX + srcWidth - 1) * scaleX + 0.5f);
//		srcRect.bottom = (int) ((srcY + srcHeight - 1) * scaleY + 0.5f);
//
//		dstRect.left = (int) (x * scaleX + 0.5f);
//		dstRect.top = (int) (y * scaleY + 0.5);
//		dstRect.right = (int) ((x + srcWidth - 1) * scaleX + 0.5f);
//		dstRect.bottom = (int) ((y + srcHeight - 1) * scaleY + 0.5f);
//
//		canvas.drawBitmap(bitmap.getBitmap(), srcRect, dstRect, null);
//	}
//
//
//	/**
//	 * 使用指定的Alpha值绘制Bitmap。
//	 * @param alpha 要使用的Alpha值 [0..255]。
//	 */
//	public void drawBitmap(LiveBitmap bitmap, int x, int y, int alpha) {
//		paint.setAlpha(alpha);
//		canvas.drawBitmap(bitmap.getBitmap(), x * scaleX, y * scaleY, paint);
//	}
//	
	/**
	 * 绘制指定的Bitmap。
	 * @param canvas 目标画布
	 * @param bitmap 要绘制的图片
	 * @param x 左边缘位置
	 * @param y 上边缘位置
	 */
	public void drawBitmap(Canvas canvas, LiveBitmap bitmap, int x, int y) {
		canvas.drawBitmap(bitmap.getBitmap(), x * scaleX, y * scaleY, null);
	}
//
//	/**
//	 * 绘制指定的Bitmap
//	 * @param bitmap 要绘制的Bitmap。
//	 * @param x 目标左边缘位置
//	 * @param y 目标上边缘位置
//	 * @param srcWidth 缩放前宽度
//	 * @param srcHeight 缩放前高度
//	 */
//	public void drawBitmap(LiveBitmap bitmap, int x, int y, int srcWidth,
//			int srcHeight) {
//		dstRect.left = (int) (x * scaleX + 0.5f);
//		dstRect.top = (int) (y * scaleY + 0.5);
//		dstRect.right = (int) ((x + srcWidth - 1) * scaleX + 0.5f);
//		dstRect.bottom = (int) ((y + srcHeight - 1) * scaleY + 0.5f);
//		canvas.drawBitmap(bitmap.getBitmap(), null, dstRect, null);
//	}
//
//	public void drawBitmapInParentCenter(LiveBitmap bitmap, Point center) {
//		int x = center.x - (int) (bitmap.getRawWidth() / 2 + 0.5f);
//		int y = center.y - (int) (bitmap.getRawHeight() / 2 + 0.5f);
//		drawBitmap(bitmap, x, y);
//	}

	public float getScaleX() {
		return scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}
}
