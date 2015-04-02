package com.mym.landlords.res;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.CountDownTimer;

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
	/** 卡牌原始宽度。 */
	public static final int CARD_WIDTH = 92;
	/** 卡牌原始高度。 */
	public static final int CARD_HEIGHT = 126;
	/** 卡牌被选中后向上抽出的高度。 */
	public static final int Card_PICKED_OFFSET = 10;	
	/** 游戏屏幕的水平内边距[离屏幕左右边界]。 */
	public static final int SCREEN_PADDING_HORIZONTAL = 3;
	/** 游戏屏幕的垂直内边距[离屏幕上下边界]。 */
	public static final int SCREEN_MARGIN_VERTICAL = 3;
	/** AI 玩家所剩手牌数目文字X坐标。 */
	public static final int AIPLAYER_LEFT_CARDNUM_X =25; // org=30 
	/** AI 玩家所剩手牌数目文字X坐标。 */
	public static final int AIPLAYER_RIGHT_CARDNUM_X=730;//org=750
	/** AI 玩家所剩手牌数目文字Y坐标。 */
	public static final int AIPLAYER_CARDNUM_MARGIN_Y=100;
	

//	private Bitmap frameBuffer; // 底色
//	private Canvas canvas; // 画布对象
	private Paint paint; // 画笔对象
	private float scaleX; // X缩放比
	private float scaleY; // Y缩放比
	private Rect srcRect = new Rect(); // 源矩阵对象
	private Rect dstRect = new Rect(); // 目标矩阵对象
	
	/**
	 * @see #setAlpha(int)
	 */
	private int currentAlpha=0;			//当前画笔的Alpha值，0=透明，255=不透明
	/**
	 * @see #setAlpha(int)
	 */
	private CountDownTimer alphaDecendTimer;	//用于消减Alpha
	
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
		this.paint = new Paint();
	}

	/**
	 * 绘制指定的Bitmap。
	 * @param canvas 画布
	 * @param bitmap 要绘制的图片。
	 * @param x 目标左边缘位置
	 * @param y 目标上边缘位置
	 * @param srcX 缩放前左边缘位置
	 * @param srcY 缩放前上边缘位置
	 * @param srcWidth 缩放前宽度
	 * @param srcHeight 缩放前高度
	 */
	public void drawBitmap(Canvas canvas, LiveBitmap bitmap, int x, int y, int srcX, int srcY,
			int srcWidth, int srcHeight) {
		srcRect.left = (int) (srcX * scaleX + 0.5f);
		srcRect.top = (int) (srcY * scaleY + 0.5f);
		srcRect.right = (int) ((srcX + srcWidth - 1) * scaleX + 0.5f);
		srcRect.bottom = (int) ((srcY + srcHeight - 1) * scaleY + 0.5f);

		dstRect.left = (int) (x * scaleX + 0.5f);
		dstRect.top = (int) (y * scaleY + 0.5);
		dstRect.right = (int) ((x + srcWidth - 1) * scaleX + 0.5f);
		dstRect.bottom = (int) ((y + srcHeight - 1) * scaleY + 0.5f);

		canvas.drawBitmap(bitmap.getBitmap(), srcRect, dstRect, null);
	}

	/**
	 * 设置画笔的Alpha值。该值仅对调用  {@link #drawBitmapUsingAlpha(Canvas, LiveBitmap, int, int)}有效。
	 * @param alpha 目标 alpha值，必须在0-255之间。
	 */
	public final void setAlpha(int alpha){
		if (alpha<0 || alpha > 255){
			throw new IllegalArgumentException("wrong alpha value " + alpha);
		}
		currentAlpha = alpha;
		if (alphaDecendTimer!=null){
			alphaDecendTimer.cancel();
		}
		alphaDecendTimer = new CountDownTimer(16*64,16) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				//从255衰减到0需要64次，大约1s
				currentAlpha -= 4;
				if (currentAlpha<=0){
					currentAlpha=0;
				}
				cancel();
				alphaDecendTimer = null;
			}
			
			@Override
			public void onFinish() {
				if (alphaDecendTimer != null){
					currentAlpha = 0;
					alphaDecendTimer.cancel();
					alphaDecendTimer = null;
				}
				
			}
		};
		alphaDecendTimer.start();
	}
	
	public final int getCurrentAlpha(){
		return currentAlpha;
	}

	/**
	 * 使用 {@link #currentAlpha}作为Alpha值绘制Bitmap。
	 * 可以通过调用 {@link #setAlpha(int)}来设置Alpha值。Alpha值会在设置之后自动递减，直至为0。
	 */
	public void drawBitmapUsingAlpha(Canvas canvas, LiveBitmap bitmap, int x, int y) {
		paint.setAlpha(currentAlpha);
		canvas.drawBitmap(bitmap.getBitmap(), x * scaleX, y * scaleY, paint);
	}
	
	/**
	 * 绘制指定的Bitmap，自动处理缩放比例。
	 * @param canvas 目标画布
	 * @param bitmap 要绘制的图片
	 * @param x 左边缘位置
	 * @param y 上边缘位置
	 */
	public void drawBitmap(Canvas canvas, LiveBitmap bitmap, int x, int y) {
		canvas.drawBitmap(bitmap.getBitmap(), x * scaleX, y * scaleY, null);//不需要Paint对象
	}

	/**
	 * 绘制指定的Bitmap
	 * @param bitmap 要绘制的Bitmap。
	 * @param x 目标左边缘位置
	 * @param y 目标上边缘位置
	 * @param srcWidth 缩放前宽度
	 * @param srcHeight 缩放前高度
	 */
	public void drawBitmap(Canvas canvas, LiveBitmap bitmap, int x, int y, int srcWidth,
			int srcHeight) {
		dstRect.left = (int) (x * scaleX + 0.5f);
		dstRect.top = (int) (y * scaleY + 0.5);
		dstRect.right = (int) ((x + srcWidth - 1) * scaleX + 0.5f);
		dstRect.bottom = (int) ((y + srcHeight - 1) * scaleY + 0.5f);
		canvas.drawBitmap(bitmap.getBitmap(), null, dstRect, null);
	}

	public void drawBitmapInParentCenter(Canvas canvas, LiveBitmap bitmap, Point center) {
		int x = center.x - (int) (bitmap.getRawWidth() / 2 + 0.5f);
		int y = center.y - (int) (bitmap.getRawHeight() / 2 + 0.5f);
		drawBitmap(canvas, bitmap, x, y);
	}
	/**
	 * 绘制数字形式的文本。
	 * @param msg 要绘制的消息，只能包含数字或空格，否则将抛出异常。
	 * @param numbeBitmap 要绘制的数字图片。
	 * @param x 文字的起点坐标
	 * @param y 文字的起点坐标
	 */
    public void drawText(Canvas canvas, LiveBitmap numbeBitmap, String msg, int x, int y) {
        if (!msg.matches("[0-9 ]*")){
			throw new IllegalArgumentException("drawable msg should contain only numbers and spaces. msg="+msg);
		}
		int len = msg.length();
        for (int i = 0; i < len; i++) {
            char character = msg.charAt(i);

            if (character == ' ') {
                x += 20;						//留白
                continue;
            }

            int srcX = (character - '0') * 17;
            int srcWidth = 17;					//字宽
                
            drawBitmap(canvas, numbeBitmap, x, y, srcX, 0, srcWidth, 21);
            x += srcWidth;
        }
    }
    
	public Point getCenter(LiveBitmap pixmap, float x, float y)
	{
		int centerX = (int) (x + pixmap.getRawWidth() / 2 + 0.5f);
		int centerY = (int) (y + pixmap.getRawHeight() / 2 + 0.5f);
		return new Point(centerX, centerY);
	}
	
	public float getScaleX() {
		return scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}
}
