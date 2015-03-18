package com.mym.landlords.widget;

import android.view.MotionEvent;

/**
 * 将触摸屏实际捕捉到的TouchEvent映射回基本屏幕（800*400）尺寸上的坐标。
 * @author Muyangmin
 * @create 2015-3-17
 */
public final class MappedTouchEvent {
	public final int x;
	public final int y;
	private final MotionEvent originEvent;
	private static Mapper mapper;
	
	/**
	 * 初始化转换器。
	 * @param scaleX X轴缩放比例
	 * @param scaleY Y轴缩放比例。
	 */
	public static final void initMapper(float scaleX, float scaleY){
		mapper = new Mapper(scaleX, scaleY);
	}
	
	public static MappedTouchEvent translateEvent(MotionEvent event){
		if (mapper==null){
			throw new RuntimeException("must call initMapper() as early as possible.");
		}
		return mapper.translateEvent(event);
	}
	
	private MappedTouchEvent(int x, int y, MotionEvent event) {
		super();
		this.x = x;
		this.y = y;
		this.originEvent = event;
	}
	
	public int getAction(){
		return originEvent.getAction();
	}
	
	
	private static final class Mapper{
		 private float scaleX;
		 private float scaleY;
		 
		 public Mapper(float scaleX, float scaleY) {
			super();
			this.scaleX = scaleX;
			this.scaleY = scaleY;
		}

		public MappedTouchEvent translateEvent(MotionEvent event){
			 int x = (int) (event.getX()/scaleX);
			 int y = (int) (event.getY()/scaleY);
			 MappedTouchEvent map = new MappedTouchEvent(x, y, event);
			 return map;
		 }
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappedTouchEvent [x=").append(x).append(", y=")
				.append(y).append(", originEvent=").append(originEvent)
				.append("]");
		return builder.toString();
	}
	
}
