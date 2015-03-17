package com.mym.landlords.widget;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.mym.landlords.res.GameGraphics;
import com.mym.landlords.res.LiveBitmap;

/**
 * 虚拟的按钮控件，使用Android的控件思想进行封装。
 * @author Muyangmin
 * @create 2015-3-17
 */
public final class BitmapButton implements BitmapView{
	
	private onClickListener listener;
	private final GameGraphics graphics;
	private final int x;						//x坐标起点
	private final int y;						//y坐标起点
	private final LiveBitmap bitmapNormal;	//按钮图片
	private final LiveBitmap bitmapPressed;	//按钮按下的图片
	private boolean isPressed;			//是否已经被按下
	
	/**
	 * 构造一个图片按钮，按钮不具备按下效果。
	 * @param x 按钮的x轴起点坐标
	 * @param y 按钮的y轴起点坐标
	 * @param bitmap 按钮上显示的图片，不能为null
	 */
	public BitmapButton(GameGraphics graphics, int x, int y, LiveBitmap bitmap) {
		this(graphics, x, y, bitmap, bitmap);
	}

	/**
	 * 构造一个具备按下效果的图片按钮。
	 * @param x 按钮的x轴起点坐标
	 * @param y 按钮的y轴起点坐标
	 * @param bitmapNormal 按钮上显示的图片，不能为null
	 * @param bitmapPressed 按钮被按下时显示的图片，不能为null
	 */
	public BitmapButton(GameGraphics graphics, int x, int y, LiveBitmap bitmapNormal,
			LiveBitmap bitmapPressed) {
		super();
		if (bitmapNormal==null || bitmapPressed==null){
			throw new NullPointerException("cannot construct a bitmap button using null bitmap.");
		}
		this.graphics = graphics;
		this.x = x;
		this.y = y;
		this.bitmapNormal = bitmapNormal;
		this.bitmapPressed = bitmapPressed;
	}
	
	public final void onTouch(MotionEvent event){
		if (inBounds(event)){
			if (event.getAction()==MotionEvent.ACTION_DOWN){
				isPressed = true;
			}
			else if (event.getAction()==MotionEvent.ACTION_UP){
				isPressed = false;
				if (listener!=null){
					listener.onClicked(this);
				}
			}
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		graphics.drawBitmap(canvas, isPressed ? bitmapPressed : bitmapNormal, x, y);
	}
	
	/**
	 * 判断指定的事件是否在自己的区域中。
	 */
	private boolean inBounds(MotionEvent event){
		int width = bitmapNormal.getRawWidth();
		int height = bitmapNormal.getRawHeight();
        if(event.getX() > x && event.getX() < x + width - 1 && 
           event.getY() > y && event.getY() < y + height - 1){
        	return true;
        }
        else{
            return false;
        }
	}
	
	public interface onClickListener{
		/**
		 * 控件被点击时调用。
		 * @param btn 按钮自身的引用。
		 */
		void onClicked(BitmapButton btn);
	}

	public void setListener(onClickListener listener) {
		this.listener = listener;
	}
}
